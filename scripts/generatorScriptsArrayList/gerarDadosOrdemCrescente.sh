#!/bin/bash

# ===============================
# CONFIGURAÇÃO
# ===============================

OPERACOES=100000    # total de operações

WARMUP=3000         # inserções iniciais
VALOR_MAX=10000     # range dos valores
P_INSERT=50         # % inserção
P_REMOVE=0          # % remoção
P_SEARCH=50         # % busca

# ===============================
# VALIDAÇÃO DAS PORCENTAGENS
# ===============================

TOTAL_PERCENT=$((P_INSERT + P_SEARCH + P_REMOVE))

if (( TOTAL_PERCENT != 100 )); then
  echo "Erro: As porcentagens devem somar 100% (atual: $TOTAL_PERCENT%)"
  exit 1
fi

# ===============================
# MONTAGEM AUTOMÁTICA DO NOME
# ===============================

ORDEM="random"
OUT_DIR="./data/entradas/ArrayList"

mkdir -p "$OUT_DIR"

BASENAME="${ORDEM}_n${OPERACOES}_I${P_INSERT}_R${P_REMOVE}_S${P_SEARCH}"
CSV_FILE="${OUT_DIR}/${BASENAME}.csv"

# ===============================
# CÁLCULO DAS QUANTIDADES
# ===============================

N_I=$(( OPERACOES * P_INSERT / 100 ))
N_S=$(( OPERACOES * P_SEARCH / 100 ))
N_R=$(( OPERACOES * P_REMOVE / 100 ))

echo "Operações calculadas:"
echo "N_I=$N_I  N_S=$N_S  N_R=$N_R"

# ===============================
# ARQUIVO TEMPORÁRIO
# ===============================

tmp="$(mktemp)"
trap 'rm -f "$tmp"' EXIT
: > "$tmp"

# ===============================
# ESTRUTURA AUXILIAR
# ===============================

VALUES=()

# ===============================
# WARMUP (não grava no CSV)
# ===============================

echo "Realizando warm-up..."
for ((i=0; i<WARMUP; i++)); do
  VALUE=$((RANDOM % VALOR_MAX))
  VALUES+=("$VALUE")
done
echo "Warm-up concluído."

# ===============================
# INSERÇÕES
# ===============================

if (( N_I > 0 )); then
  echo "Gerando inserções..."
  for ((i=0; i<N_I; i++)); do
    INDEX=$((RANDOM % 100))
    VALUE=$((RANDOM % VALOR_MAX))
    VALUES+=("$VALUE")
    echo "I,$INDEX,$VALUE" >> "$tmp"
  done
else
  echo "Pulando inserções (P_INSERT=0)."
fi

TOTAL_VALUES=${#VALUES[@]}

# ===============================
# BUSCAS
# ===============================

if (( N_S > 0 )); then
  echo "Gerando buscas..."
  if (( TOTAL_VALUES == 0 )); then
    echo "Sem valores disponíveis para busca."
  else
    for ((i=0; i<N_S; i++)); do
      POS=$((RANDOM % TOTAL_VALUES))
      VALUE="${VALUES[POS]}"
      echo "S,$VALUE,$VALUE" >> "$tmp"
    done
  fi
else
  echo "Pulando buscas (P_SEARCH=0)."
fi

# ===============================
# REMOÇÕES
# ===============================

if (( N_R > 0 )); then
  echo "Gerando remoções..."
  
  MAX_REMOVE=$N_R
  if (( MAX_REMOVE > TOTAL_VALUES )); then
    MAX_REMOVE=$TOTAL_VALUES
  fi

  for ((i=0; i<MAX_REMOVE; i++)); do
    POS=$((RANDOM % TOTAL_VALUES))
    VALUE="${VALUES[POS]}"
    INDEX=$((RANDOM % 100))
    echo "R,$INDEX,$VALUE" >> "$tmp"
  done
else
  echo "Pulando remoções (P_REMOVE=0)."
fi

# ===============================
# EMBARALHA E SALVA
# ===============================

shuf "$tmp" > "$CSV_FILE"

echo "Arquivo CSV gerado (embaralhado): $CSV_FILE"
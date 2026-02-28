#!/bin/bash

# ===============================
# CONFIGURAÇÃO
# ===============================

OPERACOES=1000000    # total de operações

WARMUP=3000           # inserções iniciais
VALOR_MAX=10000      # range dos valores
P_INSERT=50        # % inserção
P_REMOVE=50           # % remoção
P_SEARCH=0            # % busca

# Caminho para salvar o arquivo CSV
CSV_FILE="operacoes.csv"

# ===============================
# GERAÇÃO DO CSV
# ===============================

# Criação do arquivo CSV com cabeçalho
echo "Operacao,Indice,Valor" > "$CSV_FILE"

# ===============================
# WARMUP (garante que não remove vazio)
# ===============================

for ((i=0; i<$WARMUP; i++))
do
    V=$((RANDOM % VALOR_MAX))
    echo "I,$i,$V"  # Inserção com valor aleatório
done >> "$CSV_FILE"

# ===============================
# GERAÇÃO PRINCIPAL
# ===============================

tmp="$(mktemp)"
trap 'rm -f "$tmp"' EXIT

N_I=$(( OPERACOES * P_INSERT / 100 ))  # Número de inserções
N_R=$(( OPERACOES * P_REMOVE / 100 ))  # Número de remoções
N_S=$(( OPERACOES * P_SEARCH / 100 )) # Número de buscas

# Gera as operações I, R e S
for ((i=0; i<N_I; i++)); do 
    # Forçar inserções com shift (inserir em posições aleatórias pequenas)
    INDEX=$((RANDOM % 100))  # Posições entre 0 e 100
    VALUE=$((RANDOM % VALOR_MAX))
    echo "I,$INDEX,$VALUE"
done >> "$CSV_FILE" 

for ((i=0; i<N_R; i++)); do 
    # Forçar remoções com shift (remover de posições aleatórias pequenas)
    INDEX=$((RANDOM % 100))  # Posições entre 0 e 100
    echo "R,$INDEX,"
done >> "$CSV_FILE" 

for ((i=0; i<N_S; i++)); do 
    # Forçar buscas que percorrem todo o array (busca por valor aleatório grande)
    VALUE=$((RANDOM % VALOR_MAX))
    echo "S,,$VALUE"
done >> "$CSV_FILE"

# Embaralha as operações para misturar inserções, remoções e buscas
shuf "$CSV_FILE" -o "$CSV_FILE"

echo "Arquivo CSV gerado: $CSV_FILE"
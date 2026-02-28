#!/bin/bash

# ===============================
# CONFIGURAÇÃO
# ===============================

OPERACOES=1000000    # total de operações

WARMUP=3000           # inserções iniciais
VALOR_MAX=10000      # range dos valores
P_INSERT=100         # 100% inserção
P_REMOVE=0           # % remoção
P_SEARCH=0           # % busca

# Caminho para salvar o arquivo CSV
CSV_FILE="./data/entradas/ArrayList/OrdemDeAdicao.csv"

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
    echo "I,$i,$V"  
done >> "$CSV_FILE"

# ===============================
# GERAÇÃO PRINCIPAL (Apenas inserções)
# ===============================

tmp="$(mktemp)"
trap 'rm -f "$tmp"' EXIT

N_I=$(( OPERACOES * P_INSERT / 100 ))  # Número de inserções
# Remoções e buscas são 0, então não precisamos calcular

# Gera as operações de inserção
for ((i=0; i<N_I; i++)); do 
    # Inserir em posições aleatórias pequenas
    INDEX=$((RANDOM % 100))  # Posições entre 0 e 100
    VALUE=$((RANDOM % VALOR_MAX))
    echo "I,$INDEX,$VALUE"
done >> "$CSV_FILE" 

# Embaralha as operações para misturar as inserções
shuf "$CSV_FILE" -o "$CSV_FILE"

echo "Arquivo CSV gerado: $CSV_FILE"
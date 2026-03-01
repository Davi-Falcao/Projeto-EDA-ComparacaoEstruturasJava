#!/bin/bash

# ===============================
# CONFIGURAÇÃO
# ===============================

OPERACOES=1000000    # total de operações

WARMUP=3000          # inserções iniciais
VALOR_MAX=10000      # range dos valores
P_INSERT=50          # 50% inserção
P_REMOVE=0           # % remoção
P_SEARCH=50          # 50% busca

# Caminho para salvar o arquivo CSV
CSV_FILE="./data/entradas/ArrayList/OrdemDeBusca.csv"

# ===============================
# GERAÇÃO DO CSV    
# ===============================

tmp="$(mktemp)"
trap 'rm -f "$tmp"' EXIT

N_I=$(( OPERACOES * P_INSERT / 100 ))  # Número de inserções
N_S=$(( OPERACOES * P_SEARCH / 100 )) # Número de buscas

# Arrays para armazenar os valores inseridos
VALUES=()

# Gera as operações de inserção e busca alternadas
for ((i=0; i<N_I; i++)); do 
    VALUE=$i  # O valor inserido será igual ao índice
    VALUES+=($VALUE)  # Armazena o valor gerado para inserção
    # Insere o valor com o índice consecutivo
    echo "I,$i,$VALUE" >> "$CSV_FILE"              

    # Faz a busca imediatamente após a inserção do próprio valor
    echo "S,$i,$VALUE" >> "$CSV_FILE"  # Realiza a busca para o valor inserido
done

echo "Arquivo CSV gerado: $CSV_FILE"
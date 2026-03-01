#!/bin/bash

# ===============================
# CONFIGURAÇÃO
# ===============================

OPERACOES=100000    # total de operações

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

# ===============================
# FASE DE WARM-UP
# ===============================

echo "Realizando warm-up..."
for ((i=0; i<WARMUP; i++)); do 
    VALUE=$i  # O valor inserido será igual ao índice
    # Insere o valor sem gravar no CSV
    # A busca é feita imediatamente após a inserção, mas não será registrada
    # Não gravamos nada no CSV durante o warm-up
    VALUES+=($VALUE)
done
echo "Warm-up concluído."

# ===============================
# FASE DE INSERÇÃO DOS VALORES
# ===============================

echo "Inserindo valores..."
for ((i=0; i<N_I; i++)); do 
    VALUE=$i  # O valor inserido será igual ao índice
    VALUES+=($VALUE)  # Armazena o valor gerado para inserção
    # Insere o valor com o índice consecutivo e grava no CSV
    echo "I,$i,$VALUE" >> "$CSV_FILE"              
done

# ===============================
# FASE DE BUSCA
# ===============================

echo "Realizando buscas para os valores inseridos..."
for VALUE in "${VALUES[@]}"; do
    # Realiza a busca para o valor inserido
    echo "S,$VALUE,$VALUE" >> "$CSV_FILE"  # Realiza a busca para o valor inserido
done

echo "Arquivo CSV gerado: $CSV_FILE"
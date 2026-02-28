#!/bin/bash

# ===============================
# CONFIGURAÇÃO
# ===============================

OPERACOES=1000000    # total de operações
WARMUP=3000        # inserções iniciais
P_INSERT=75       # % inserção
P_REMOVE=25       # % remoção
P_SEARCH=0        # % busca

# ===============================
# WARMUP (garante que não remove vazio)
# ===============================

# Gera valores em ordem crescente para o warmup
n=$((OPERACOES+WARMUP))

for ((i=0; i<WARMUP; i++))
do
    printf "I %d " "$n"
    ((n--))
done

# ===============================
# GERAÇÃO PRINCIPAL (decrescente)
# ===============================

tmp="$(mktemp)"
trap 'rm -f "$tmp"' EXIT

# Calcula a quantidade exata de inserções, remoções e buscas
N_I=$(( OPERACOES * P_INSERT / 100 ))
N_R=$(( OPERACOES * P_REMOVE / 100 ))
N_S=$(( OPERACOES * P_SEARCH / 100 ))

# Monta as operações em quantidade exata
for ((i=0; i<N_I; i++)); do echo "I"; done >> "$tmp" 
for ((i=0; i<N_R; i++)); do echo "R"; done >> "$tmp" 
for ((i=0; i<N_S; i++)); do echo "S"; done >> "$tmp"

# Inicializa o valor máximo para inserção, que vai ser decrementado  # Start com o maior valor

# Embaralha as operações e processa cada uma delas
shuf "$tmp" | while read -r op; do 
    if [ "$op" = "I" ]; then
        # Gera inserções com valores decrescentes
        printf "I %d " "$n"
        ((n--))  # Decrementa o valor para a próxima inserção
    
    elif [ "$op" = "R" ]; then
        # Gera remoções, sem valor específico
        printf "R "
    
    else
        # Gera buscas com valores aleatórios dentro do intervalo de inserções
        printf "S %d " "$((RANDOM % (i + 1)))"
    fi    
done
#!/bin/bash

# ===============================
# CONFIGURAÇÃO
# ===============================

OPERACOES=1000000    # total de operações
#| N (operações) |    Warmup          |
#| ------------- | ------------------ |
#| 1 000         | 100                |
#| 10 000        | 500                |
#| 100 000       | 1 000              |
#| 1 000 000     | 3 000              |

WARMUP=3000           # inserções iniciais
VALOR_MAX=1000      # range dos valores
P_INSERT=50        # % inserção
P_REMOVE=50           # % remoção
P_SEARCH=0            # % busca

# ===============================
# WARMUP (garante que não remove vazio)
# ===============================

for ((i=0; i<$WARMUP; i++))
do
    V=$((RANDOM % VALOR_MAX))
    printf "I %d " "$V"
done

# ===============================
# GERAÇÃO PRINCIPAL
# ===============================

tmp="$(mktemp)"
trap 'rm -f "$tmp"' EXIT

N_I=$(( OPERACOES * P_INSERT / 100 ))
N_R=$(( OPERACOES * P_REMOVE / 100 ))
N_S=$(( OPERACOES * P_SEARCH / 100 ))

# monta exatamente N_I, N_R, N_S linhas 
for ((i=0; i<N_I; i++)); do echo "I"; done >> "$tmp" 
for ((i=0; i<N_R; i++)); do echo "R"; done >> "$tmp" 
for ((i=0; i<N_S; i++)); do echo "S"; done >> "$tmp"

# embaralha (GNU coreutils) 
shuf "$tmp" | while read -r op; do 

    if [ "$op" = "I" ]; then
        printf "I %d " "$((RANDOM % VALOR_MAX))"
    
    elif [ "$op" = "R" ]; then
        printf "R "
    
    else
        printf "S %d " "$((RANDOM % VALOR_MAX))"
    fi    
 done



#!/bin/bash

# ===============================
# CONFIGURAÇÃO
# ===============================

OPERACOES=200000       # total de operações
WARMUP=5000            # inserções iniciais
VALOR_MAX=1000000      # range dos valores
P_INSERT=45            # % inserção
P_REMOVE=45            # % remoção
P_SEARCH=10            # % busca

# ===============================
# WARMUP (garante que não remove vazio)
# ===============================

for ((i=0; i<$WARMUP; i++))
do
    V=$((RANDOM % VALOR_MAX))
    echo "I $V"
done

# ===============================
# GERAÇÃO PRINCIPAL
# ===============================

for ((i=0; i<$OPERACOES; i++))
do
    R=$((RANDOM % 100))

    if [ $R -lt $P_INSERT ]; then
        # Inserção
        V=$((RANDOM % VALOR_MAX))
        echo "I $V"

    elif [ $R -lt $((P_INSERT + P_REMOVE)) ]; then
        # Remoção
        echo "R $V"

    else
        # Busca
        V=$((RANDOM % VALOR_MAX))
        echo "S $V"
    fi
done
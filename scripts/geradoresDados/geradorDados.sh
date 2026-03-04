#!/bin/bash

# ===============================
# PREDEFINIÇÕES
# ===============================

OPERACOES=1000000
WARMUP=0
VALOR_MAX=1000000

#================================
# MENU DE INTERAÇÃO
#================================

echo "Tipo de geração de dados:"
echo "1 - Dados com repetição."
echo "2 - Dados únicos."
read -p "Digite o número do tipo desejado: " TIPO

echo "Ordem dos dados gerados:"
echo "1 - Aleatória"
echo "2 - Crescente"
echo "3 - Decrescente"
read -p "Digite o número da ordem desejada: " ORDEM

if [ "$TIPO" == "1" ]; then
    TIPO="ComRepeticao"
elif [ "$TIPO" == "2" ]; then
    TIPO="Unica"
fi

if [ "$ORDEM" == "1" ]; then
    ORDEM="Random"
elif [ "$ORDEM" == "2" ]; then
    ORDEM="Crescente"
elif [ "$ORDEM" == "3" ]; then
    ORDEM="Decrescente"
fi

# ===============================
# MONTAGEM DO ARQUIVO GERADO
# ===============================

OUT_DIR="."

mkdir -p "$OUT_DIR"

BASENAME="entrada${ORDEM}${TIPO}"
CSV_FILE="${OUT_DIR}/${BASENAME}.csv"

# ===============================
# WARMUP
# ===============================

echo "Realizando warm-up..."

seq 0 $((VALOR_MAX-1)) | shuf -n "$WARMUP" > "$CSV_FILE" 

echo "Warm-up concluído."

# ===============================
# GERADOR DE DADOS
# ===============================

echo "Dados estão sendo gerados..."
 
ordenarDados() {
    if [ "$ORDEM" == "Crescente" ]; then
        sort -n
    elif [ "$ORDEM" == "Decrescente" ]; then
        sort -nr
    else
        cat
    fi
}

if [ "$TIPO" == "ComRepeticao" ]; then
    {
    for ((i=0; i<OPERACOES; i++)); do
        VALUE=$((RANDOM % VALOR_MAX))
        echo "$VALUE"
    done
    } | ordenarDados >> "$CSV_FILE"
else
    USADOS=$(cat "$CSV_FILE")   

    # Gera dados sem repetição
    seq 0 $((VALOR_MAX-1)) | grep -v -F -x -f <(echo "$USADOS") | shuf -n "$OPERACOES" | ordenarDados >> "$CSV_FILE" 
fi

echo "Arquivo com dados gerado: $CSV_FILE"

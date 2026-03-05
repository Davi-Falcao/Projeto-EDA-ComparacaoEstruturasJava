# Projeto EDA - Comparacao de Estruturas em Java

Projeto para benchmark de operacoes em estruturas de dados, medindo por operacao:
- `TempoExecucao(ns)`
- `MemoriaUso(bytes)`

As operacoes de entrada sao:
- `I`: insercao
- `R`: remocao
- `S`: busca

## 1) Pre-requisitos

- Java (JDK) 8+
- Maven no `PATH`
- Para gerar dados com script `.sh`: Git Bash, WSL ou Linux/macOS
- Para graficos: Python 3 com `pandas` e `matplotlib`

## 2) Compilacao e execucao com Maven

Compilar o projeto:

```bash
mvn clean compile
```

Executar o App (perfil `app`):

```bash
mvn exec:java -Papp
```

Sem argumentos, o App apenas exibe o menu de uso.

## 3) Formato dos dados de entrada

Os arquivos de entrada devem ficar em:

`data/entradas/ArrayList/`

Formato de cada linha (sem cabecalho):

```text
Operacao,Indice,Valor
```

Exemplo:

```text
I,0,10
I,1,11
S,0,10
R,1,0
```

Como o `BenchArrayList` interpreta:
- `I,indice,valor`: faz `add(indice, valor)`
- `R,indice,valor`: faz `remove(indice)` (campo `valor` e ignorado)
- `S,indice,valor`: faz `indexOf(valor)` (campo `indice` e ignorado)

## 4) Criacao de massa com script `.sh`

Script principal:

`scripts/generatorScriptsArrayList/gerarDadosRandom.sh`

Passos:

1. Abra o script e ajuste:
   - `OPERACOES`
   - `WARMUP`
   - `VALOR_MAX`
   - `P_INSERT`, `P_REMOVE`, `P_SEARCH` (a soma deve ser 100)
2. Execute:

```bash
bash scripts/generatorScriptsArrayList/gerarDadosRandom.sh
```

3. O arquivo CSV sera gerado em:

`data/entradas/ArrayList/`

Nome padrao gerado:

`random_n<OPERACOES>_I<P_INSERT>_R<P_REMOVE>_S<P_SEARCH>.csv`

Observacao: o script gera operacoes em arquivo temporario e aplica `shuf` no final, entao a ordem final fica embaralhada.

## 5) Rodar benchmark pelo App (fluxo recomendado)

Comando (PowerShell):

```powershell
mvn exec:java -Papp "-Dexec.args=arraylist random_n100000_I50_R0_S50.csv"
```

Comando (bash/WSL):

```bash
mvn exec:java -Papp -Dexec.args="arraylist random_n100000_I50_R0_S50.csv"
```

## 6) O que acontece internamente no benchmark (passo a passo)

### 6.1 Fluxo do `App`

1. Recebe 2 argumentos: benchmark e arquivo de entrada.
2. Para `arraylist`, executa `30` iteracoes.
3. Em cada iteracao, chama `BenchArrayList` e grava em:
   - `data/results/temp/arquivo_1.csv`
   - ...
   - `data/results/temp/arquivo_30.csv`
4. Seleciona o arquivo na posicao mediana da lista (indice `15`).
5. Copia esse arquivo para:
   - `data/results/ArrayList/result_<arquivo_de_entrada>`
6. Remove os arquivos temporarios.

### 6.2 Fluxo do `BenchArrayList`

1. Recebe `<arquivoEntrada> <arquivoSaida>`.
2. Resolve entrada:
   - Se vier apenas nome: usa `data/entradas/ArrayList/<nome>`.
   - Se vier caminho com `/` ou `\`: usa caminho informado.
3. Le o CSV linha a linha.
4. Executa cada operacao na estrutura `ArrayList` customizada.
5. Para cada linha, mede:
   - tempo com `System.nanoTime()`
   - memoria com `totalMemory - freeMemory`
6. Registra no CSV de saida:
   - `Operacao,TamanhoEntrada,TempoExecucao(ns),MemoriaUso(bytes)`
   - para `S`, a segunda coluna recebe o indice encontrado (`indexOf`).

## 7) Arquivos de saida

- Temporarios por iteracao:
  - `data/results/temp/arquivo_<n>.csv`
- Resultado final consolidado pelo App:
  - `data/results/ArrayList/result_<nome_arquivo_entrada>.csv`

## 8) Geracao dos graficos

Script de grafico:

`scripts/generatorGraph/ArrayList/ordemDeCrescimento.py`

Ele le estes arquivos fixos:
- `data/results/ArrayList/resultOrdemDeAdicao.csv`
- `data/results/ArrayList/resultOrdemDeBusca.csv`
- `data/results/ArrayList/resultCrescente_n100000_I50_R0_S50.csv`

### 8.1 Instalar dependencias Python

```bash
pip install pandas matplotlib
```

### 8.2 Preparar os CSVs esperados pelo script

Se necessario, renomeie/copie seus resultados gerados pelo App para esses nomes.

### 8.3 Executar o script

```bash
python scripts/generatorGraph/ArrayList/ordemDeCrescimento.py
```

O script abre 3 janelas (uma por grafico), usando `plt.show()`.

## 9) Estrutura principal

```text
src/main/java/dev/ProjetoEDA/
  App.java
  bench/BenchArrayList.java
  estruturas/arraylist/ArrayList.java

scripts/generatorScriptsArrayList/
  gerarDadosRandom.sh
  gerarDadosOrdemCrescente.sh

scripts/generatorGraph/ArrayList/
  ordemDeCrescimento.py

data/entradas/ArrayList/
  *.csv

data/results/
  temp/
  ArrayList/
```

## 10) Comandos uteis

```bash
mvn clean
mvn compile
mvn test
mvn package
mvn exec:java -Papp
```

# Projeto EDA - Comparacao de Estruturas em Java

Este projeto executa benchmarks de operacoes em estruturas de dados (atualmente, `ArrayList` customizado), medindo:
- tempo de execucao por operacao (`ns`)
- variacao de memoria por operacao (`bytes`)

As operacoes lidas do CSV sao:
- `I` (insercao)
- `R` (remocao)
- `S` (busca)

## O que o codigo faz

### Fluxo principal (`App.java`)
1. Le o argumento do Maven (`-Dexec.args`).
2. Executa o benchmark `arraylist` por **30 iteracoes**.
3. Cada iteracao gera um CSV temporario em `data/results/ArrayList/temp/`.
4. Ao final, copia o arquivo da posicao mediana (indice 15) para:
   - `data/results/ArrayList/resultOrdemDeBusca.csv`
5. Remove os arquivos temporarios.

### Benchmark (`BenchArrayList.java`)
1. Le as operacoes de entrada de `data/entradas/ArrayList/OrdemDeBusca.csv`.
2. Para cada linha, executa a operacao no `ArrayList` customizado.
3. Registra no CSV de saida:
   - operacao
   - tamanho atual da estrutura (ou indice encontrado em busca)
   - tempo de execucao
   - uso de memoria

## Como executar

### Pre-requisitos
- Java 8+
- Maven no `PATH`

### 1. Compilar
```bash
mvn clean compile
```

### 2. Ver menu do app
```bash
mvn exec:java -Papp
```

### 3. Rodar benchmark pelo app (recomendado)
Sem configuracao JSON:
```bash
mvn exec:java -Papp "-Dexec.args=arraylist"
```

Com configuracao JSON (PowerShell):
```bash
mvn exec:java -Papp "-Dexec.args=arraylist,{\"OrdemBusca\":true}"
```

Com configuracao JSON (bash):
```bash
mvn exec:java -Papp -Dexec.args='arraylist,{"OrdemBusca":true}'
```

## Formato de entrada e saida

### Entrada (`data/entradas/ArrayList/OrdemDeBusca.csv`)
Cada linha segue:
```text
Operacao,Indice,Valor
```

Exemplos:
```text
I,0,10
R,3,0
S,0,42
```

### Saida (`data/results/ArrayList/resultOrdemDeBusca.csv`)
Cabecalho:
```text
Operacao,TamanhoEntrada,TempoExecucao(ns),MemoriaUso(bytes)
```

Observacao:
- em operacao `S` (busca), a coluna `TamanhoEntrada` recebe o indice encontrado.

## Estrutura principal do projeto

```text
src/main/java/dev/ProjetoEDA/
  App.java
  bench/BenchArrayList.java
  estruturas/arraylist/ArrayList.java

src/tests/java/dev/ProjetoEDA/estruturas/
  ArrayListAsserts.java

data/entradas/ArrayList/
  OrdemDeBusca.csv
  OrdemDeAdicao.csv

data/results/ArrayList/
  resultOrdemDeBusca.csv
  resultOrdemDeAdicao.csv
```

## Comandos uteis

```bash
mvn clean
mvn compile
mvn test
mvn package
mvn exec:java -Papp
```

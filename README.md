# Projeto EDA - Comparacao de Estruturas em Java

Projeto para benchmark de operacoes em estruturas de dados (atualmente `ArrayList` customizado), medindo:
- tempo por operacao (`TempoExecucao(ns)`)
- variacao de memoria por operacao (`MemoriaUso(bytes)`)

As operacoes de entrada sao:
- `I` insercao
- `R` remocao
- `S` busca

## Visao geral do funcionamento

### `App.java` (orquestrador)
O `App`:
1. Le `-Dexec.args`.
2. Extrai o benchmark (ex.: `arraylist`) e o JSON de configuracao.
3. Valida o campo obrigatorio `Entrada` no JSON.
4. Executa o benchmark por 30 iteracoes.
5. Salva cada iteracao em arquivo temporario em `data/results/temp/`.
6. Seleciona o arquivo mediano (indice central) e copia para `data/results/ArrayList/result_<Entrada>`.
7. Remove os temporarios.

### `BenchArrayList.java` (executor)
O bench:
1. Recebe argumentos (com ou sem JSON de configuracao).
2. Resolve arquivo de entrada:
   - se for nome simples, usa `data/entradas/ArrayList/<arquivo>`
   - se tiver caminho (`/` ou `\\`), usa caminho informado.
3. Le cada linha do CSV (`Operacao,Indice,Valor`).
4. Executa a operacao na estrutura `ArrayList` customizada.
5. Registra no CSV de saida:
   - `Operacao`
   - `TamanhoEntrada` (ou indice encontrado, no caso de busca `S`)
   - `TempoExecucao(ns)`
   - `MemoriaUso(bytes)`

## Pre-requisitos

- Java 21
- Maven instalado e no `PATH`

## Como executar

### 1. Compilar
```bash
mvn clean compile
```

### 2. Mostrar menu/instrucoes do app
```bash
mvn exec:java -Papp
```

### 3. Rodar benchmark via app (recomendado)

PowerShell:
```powershell
mvn exec:java -Papp "-Dexec.args=arraylist,{\"Entrada\":\"crescente_n100000_I50_R0_S50.csv\"}"
```

bash/WSL:
```bash
mvn exec:java -Papp -Dexec.args='arraylist,{"Entrada":"crescente_n100000_I50_R0_S50.csv"}'
```

Com flag opcional:
```bash
mvn exec:java -Papp -Dexec.args='arraylist,{"Entrada":"crescente_n100000_I50_R0_S50.csv","OrdemBusca":true}'
```

## Campos do JSON (`-Dexec.args`)

Exemplo base:
```json
{"Entrada":"crescente_n100000_I50_R0_S50.csv","OrdemAdicao":false,"OrdemBusca":true}
```

Campos:
- `Entrada` (obrigatorio): nome do CSV de entrada ou caminho completo.
- `OrdemAdicao` (opcional, default `false`): quando `true`, insercao `I` usa `add(valor)` (fim da lista), ignorando o indice da linha.
- `OrdemBusca` (opcional, default `false`): no codigo atual, tambem faz insercao `I` usar `add(valor)`.

Observacao importante:
- No comportamento atual, se `OrdemAdicao` **ou** `OrdemBusca` for `true`, a insercao passa a ignorar `Indice` e inserir no fim.

## Como colocar dados no benchmark

### Formato do arquivo de entrada

Crie arquivos em `data/entradas/ArrayList/` sem cabecalho, com 3 colunas:
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

Regras praticas por operacao:
- `I,indice,valor`: insere `valor` na posicao `indice` (ou no fim, se flags de ordem estiverem ativas).
- `R,indice,valor`: remove no `indice`; o campo `valor` e ignorado.
- `S,indice,valor`: busca por `valor`; o campo `indice` e ignorado.

### Gerar massa automaticamente (scripts)

Os scripts em `scripts/generatorScriptsArrayList/` geram CSV em `data/entradas/ArrayList/`:
- `gerarDadosOrdemCrescente.sh`
- `gerarDadosRandom.sh`

Uso (Linux/WSL/Git Bash):
```bash
bash scripts/generatorScriptsArrayList/gerarDadosRandom.sh
```

Antes de executar, ajuste no script:
- `OPERACOES`
- `WARMUP`
- `P_INSERT`, `P_REMOVE`, `P_SEARCH` (devem somar 100)
- `VALOR_MAX`

## Rodar somente o bench (sem App)

Sem JSON (2 args: entrada e saida):
```bash
mvn exec:java -Dexec.mainClass=dev.ProjetoEDA.bench.BenchArrayList -Dexec.args='crescente_n100000_I50_R0_S50.csv,data/results/ArrayList/result_manual.csv'
```

Com JSON (3 args: json, entrada, saida):
```bash
mvn exec:java -Dexec.mainClass=dev.ProjetoEDA.bench.BenchArrayList -Dexec.args='{"OrdemBusca":true},crescente_n100000_I50_R0_S50.csv,data/results/ArrayList/result_manual.csv'
```

## Saidas geradas

- Temporarios por iteracao: `data/results/temp/arquivo_<n>.csv`
- Resultado final do app: `data/results/ArrayList/result_<nome_do_arquivo_entrada>`

Formato da saida:
```text
Operacao,TamanhoEntrada,TempoExecucao(ns),MemoriaUso(bytes)
```

## Estrutura principal

```text
src/main/java/dev/ProjetoEDA/
  App.java
  bench/BenchArrayList.java
  estruturas/arraylist/ArrayList.java

data/entradas/ArrayList/
  *.csv

data/results/
  temp/
  ArrayList/

scripts/generatorScriptsArrayList/
  gerarDadosOrdemCrescente.sh
  gerarDadosRandom.sh
```

## Comandos uteis

```bash
mvn clean
mvn compile
mvn test
mvn package
mvn exec:java -Papp
```

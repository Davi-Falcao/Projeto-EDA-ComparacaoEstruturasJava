# Projeto-EDA-ComparacaoEstruturasJava
Nosso projeto tem como objetivo comparar o desempenho de diferentes estruturas de dados implementadas na mesma linguagem de programação (Java), considerando tanto suas versões convencionais quanto versões otimizadas. A análise será conduzida sob diferentes cargas de dados e contemplará três operações fundamentais: inserção, remoção e busca.

## Como Usar Maven (MVN)

### Pré-requisitos
- Java 8 ou superior instalado
- Maven instalado e configurado no PATH

### Compilar o Projeto

Para compilar todos os arquivos fonte:
```bash
mvn clean compile
```

### Executar com Profiles

O projeto está configurado com **dois profiles**: `app` e `bench`.

#### 1. Executar o Benchmark (Profile `bench` - PADRÃO)

```bash
mvn exec:java -Pbench
```

Ou simplesmente (já que `bench` é ativado por padrão):
```bash
mvn exec:java
```

#### 2. Executar a Classe App (Profile `app`)

```bash
mvn exec:java -Papp
```

### Passar Parâmetros JSON

Para executar com argumentos JSON, combine o profile com `-Dexec.args`:

**Benchmark com JSON (PowerShell):**
```bash
mvn exec:java -Pbench '-Dexec.args={"OrdemAdicao":true}'
```

**Benchmark com JSON (bash):**
```bash
mvn exec:java -Pbench -Dexec.args='{"OrdemAdicao":true}'
```

**App com argumentos:**
```bash
mvn exec:java -Papp '-Dexec.args=valor1,valor2'
```

### Compilar e Executar em Uma Linha

```bash
mvn clean compile exec:java
```

Ou com profile específico:
```bash
mvn clean compile exec:java -Pbench
```

### Rotas Úteis

| Comando | Descrição |
|---------|-----------|
| `mvn clean` | Remove diretório `/target` |
| `mvn compile` | Compila o código |
| `mvn test` | Executa testes |
| `mvn package` | Gera JAR do projeto |
| `mvn exec:java` | Executa com profile padrão (bench) |
| `mvn exec:java -Pbench` | Executa BenchArrayListInsertion |
| `mvn exec:java -Papp` | Executa App |
| `mvn exec:java -Pbench '-Dexec.args={"OrdemAdicao":true}'` | Benchmark com JSON |

### Estrutura do Projeto

```
src/
├── main/java/dev/ProjetoEDA/
│   ├── App.java                    # Classe principal
│   ├── bench/
│   │   └── BenchArrayListInsertion.java  # Benchmark de inserção
│   └── estruturas/
│       └── arraylist/
│           └── ArrayList.java       # Implementação do ArrayList
└── test/java/dev/ProjetoEDA/
    └── estruturas/
        └── ArrayListAsserts.java    # Testes unitários

data/
├── entradas/
│   └── ArrayList/
│       └── OrdemDeAdicao.csv        # Dados de entrada
└── results/
    └── ArrayList/
        └── resultOrdemDeAdicao.csv  # Resultados do benchmark
```

### Parâmetros de Configuração

O JSON de configuração aceita:
- `OrdemAdicao` (boolean): Define se os dados seguem ordem de adição

Exemplo:
```json
{"OrdemAdicao":true}
```

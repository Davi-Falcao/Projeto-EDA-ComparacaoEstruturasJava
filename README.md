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

### Executar via App (Seletor de Benchmarks)

A classe `App.java` funciona como um menu para escolher qual benchmark executar. **Esta é a forma recomendada:**

#### 1. Exibir Menu de Opções
```bash
mvn exec:java
```

#### 2. Executar um Benchmark Específico

**Benchmark de Inserção em ArrayList (sem argumentos):**
```bash
mvn exec:java '-Dexec.args=arraylist-insertion'
```

**Benchmark com JSON (PowerShell):**
```bash
mvn exec:java '-Dexec.args=arraylist-insertion,{"OrdemDeBusca":true}'
```

**Benchmark com JSON (bash):**
```bash
mvn exec:java -Dexec.args='arraylist-insertion,{"OrdemDeBusca":true}'
```

### Executar Diretamente o Benchmark (Alternativa)

Se preferir usar o profile `bench` diretamente (sem passar pela App, de forma expl�cita):

```bash
mvn exec:java -Pbench
```

Com JSON:
```bash
mvn exec:java -Pbench '-Dexec.args={"OrdemAdicao":true}'
```

### Compilar e Executar em Uma Linha

```bash
# Exibir menu
mvn clean compile exec:java

# Executar benchmark específico
mvn clean compile exec:java '-Dexec.args=arraylist-insertion'
```

### Rotas Úteis

| Comando | Descrição |
|---------|-----------|
| `mvn clean` | Remove diretório `/target` |
| `mvn compile` | Compila o código |
| `mvn test` | Executa testes |
| `mvn package` | Gera JAR do projeto |
| `mvn exec:java` | Exibe menu de benchmarks |
| `mvn exec:java '-Dexec.args=arraylist-insertion'` | Executa Benchmark ArrayList |
| `mvn exec:java '-Dexec.args=arraylist-insertion,{"OrdemAdicao":true}'` | Benchmark com JSON |
| `mvn exec:java -Pbench` | Executa benchmark diretamente |
 
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



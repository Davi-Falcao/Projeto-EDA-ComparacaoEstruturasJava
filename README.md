# Projeto EDA - Comparação de Estruturas em Java

Repositório com estudo de desempenho baseado em operações comuns entre as seguintes estruturas de dados:

- **ArrayList**
- **LinkedList**
- **Binary Search Tree (BST)**
- **AVL Tree**
- **Árvore Rubro-Negra (PV)**
- **Heap**
- **PriorityQueue**

# Introdução e Objetivo

A compreensão do comportamento das estruturas de dados é essencial para utilização eficiente em diferentes situações, garantindo um código otimizado. Por isso, várias estruturas com visões e construções distintas surgiram para resolver problemas relacionados ao gerenciamento de dados. Neste projeto, diferentes implementações de listas, filas e árvores foram comparadas em diversos contextos, visando observar como cada uma lida com diferentes cargas de operações e volumes de dados. Os resultados de desempenho foram extraídos a partir do uso de memória e do tempo de execução nos diferentes cenários escolhidos.

Para garantir coesão de comparação, estruturas com objetivos semelhantes foram analisadas:

- **ArrayList X LinkedList**

- **Heap X PriorityQueue**

- **BST (Binary Search Tree) X AVL X PV (Árvore Rubro-Negra)**

Dessa forma, este estudo tem como objetivo aprofundar o conhecimento já existente na teoria, explorando tanto a complexidade quanto a implementação prática. Busca-se analisar a execução real, com informações concretas que permitam refletir e chegar a conclusões sobre quando utilizar cada estrutura e avaliar sua aplicabilidade de acordo com o contexto do seu trabalho, tanto pequenas entradas com poucos acessos ou até dados gigantes com várias alterações recorrentes.

## 1) Pré-requisitos

- Java (JDK) 8+
- Maven no `PATH`
- Para gerar dados com script `.sh`: Git Bash, WSL ou Linux/macOS
- Para gráficos: Python 3 com `matplotlib`

## 2) Compilação e execução com Maven

Compilar o projeto:

```bash
mvn clean compile
```

Executar o App:

```Powershell
mvn exec:java "-Dexec.args=<nome_do_benchmark> <tam_entrada>"
```

## 3) Arquivos de resultados são gerados automaticamente

Os arquivos de resultados são salvos automaticamente em:

```bash
repository/result/<Nome_estrutura>/
```

padrão do resultado

```bash
result_<Nome_da_Estrutura>_<ordem>_workload_<Caso>.csv
```

## 4) Gerar plotagem de gráficos automática

Executar a plotagem:

```bash
python ./scripts/generatorGraph/generate_graphs.py <nome_estrutura>
```

Os gráficos são salvos automaticamente em:

```bash
repository/result/graphs/<Nome_estrutura>
```

# Metodologia Experimental

O benchmark desenvolvido neste projeto tem como objetivo avaliar o desempenho das estruturas de dados analisadas sob diferentes padrões de uso e tamanhos de entrada. Para isso, foi definido um protocolo experimental padronizado que garante que todas as estruturas sejam avaliadas sob as mesmas condições.

## Conjuntos de entrada

Os experimentos utilizam três padrões de distribuição dos dados de entrada:

- **Random** --- valores gerados aleatoriamente sem repetição
- **Crescente** --- valores ordenados de forma estritamente crescente
- **Decrescente** --- valores ordenados de forma estritamente decrescente

Essas variações permitem analisar o impacto da ordem de inserção no comportamento das estruturas.

Cada arquivo de entrada contém **10⁶ elementos inteiros positivos**, podendo atingir valores de até **10⁸**. Os arquivos foram gerados automaticamente por meio de um **script em Bash**.

Devido a limitações de hardware, foram utilizados:

- **10³ elementos** para experimentos com **BST**
- **10⁵ elementos** para as demais estruturas

## Escalas de teste

Os experimentos são executados em múltiplos tamanhos de entrada. Os pontos intermediários são gerados automaticamente em **escala logarítmica**, permitindo observar o comportamento assintótico das estruturas à medida que o tamanho da entrada cresce.

## Warmup da JVM

Antes das medições efetivas, são executadas **3 rodadas de warmup**, com o objetivo de estabilizar a execução da JVM e reduzir interferências iniciais, como:

- Carregamento de classes
- Otimizações do compilador JIT

Os resultados dessas rodadas não são considerados na análise final.

# Avaliação das Operações

Os experimentos medem separadamente o desempenho das operações fundamentais das estruturas:

- **ADD** --- inserção
- **SEARCH** --- busca
- **REMOVE** --- remoção

Para cada tamanho de entrada `n`, a estrutura é reconstruída antes da medição da operação, garantindo que o tempo medido corresponda exclusivamente à operação avaliada.

### Inserção (ADD)

1. Cria-se uma nova estrutura
2. Inserem-se `n` elementos
3. Mede-se o tempo de uma inserção adicional

São realizadas **2000 repetições internas** para reduzir ruído experimental.

### Busca (SEARCH)

1. Cria-se uma nova estrutura
2. Inserem-se `n` elementos
3. Mede-se o tempo de busca de um elemento presente na estrutura

Também são realizadas **2000 repetições internas**.

### Remoção (REMOVE)

1. Cria-se uma nova estrutura
2. Inserem-se `n` elementos
3. Mede-se o tempo de remoção de um elemento

Neste caso são realizadas **200 repetições internas**, pois a estrutura é reconstruída a cada repetição.

# Cenários de Workload

Além das operações isoladas, o benchmark executa cenários mistos que simulam padrões de uso encontrados em aplicações reais. Cada cenário representa diferentes proporções entre inserções, remoções e buscas.

Os cenários utilizados foram:

- **100I0R0S** --- 100% inserções
- **75I25R0S** --- 75% inserções e 25% remoções
- **50I50R0S** --- 50% inserções e 50% remoções
- **50I25R25S** --- 50% inserções, 25% buscas e 25% remoções
- **50I0R50S** --- 50% inserções e 50% buscas

Em cada workload é executada uma sequência de operações sobre a mesma estrutura, acumulando o tempo gasto em cada tipo de operação. Ao final da execução, calcula-se o **tempo médio por operação**.

# Medição de Desempenho

Durante cada execução são coletadas duas métricas principais:

- **Tempo de execução**, medido em **nanosegundos**
- **Consumo de memória**, medido em **bytes**

## Medição de tempo

O tempo de execução das operações é medido utilizando:

`System.nanoTime()`

Esse método oferece resolução adequada para medições em nanossegundos e permite capturar o custo real das operações avaliadas.

## Medição de memória

O consumo de memória é estimado medindo a heap utilizada antes e depois da construção da estrutura com `n` elementos.

O processo consiste em:

1. Estabilizar a heap com `System.gc()`
2. Registrar a memória utilizada
3. Inserir os elementos na estrutura
4. Registrar novamente a memória utilizada
5. Calcular a diferença entre os valores obtidos

Para reduzir variações, múltiplas medições são realizadas e a **mediana** é utilizada como valor representativo.

# Tratamento Estatístico

Cada experimento executa:

- **3 rodadas de warmup**
- **9 rodadas de medição**

Os resultados das rodadas de medição são ordenados e o valor **mediano** é utilizado como resultado final. A mediana foi escolhida por ser menos sensível a variações ocasionais do sistema operacional, coleta de lixo da JVM e outros ruídos experimentais.

# Registro dos Resultados

Os resultados são armazenados em arquivos **CSV**, utilizando o seguinte formato:

```
TamanhoEntrada,Operacao,TempoMedio(ns),MemoriaUso(bytes)
```

Os arquivos são organizados no diretório:

```
repository/results/
```

Esse formato permite a posterior geração de **gráficos comparativos**, possibilitando analisar o comportamento das estruturas em diferentes cenários de execução.

## Arquitetura do Benchmark

O sistema de benchmark foi desenvolvido com separação clara entre **protocolo experimental**, **controle de execução** e **implementação das estruturas de dados**. Essa arquitetura garante que todas as estruturas sejam avaliadas sob exatamente as mesmas condições experimentais.

### Interface das estruturas

Todas as estruturas implementam a interface `Estrutura`, que define as operações fundamentais utilizadas nos experimentos:

- `add(int element)`
- `remove(int element)`
- `search(int element)`

Esse contrato permite que diferentes estruturas sejam avaliadas utilizando o mesmo protocolo de benchmark, sem dependência de detalhes de implementação.

### Organização da execução

A execução do benchmark é organizada em três camadas:

**Aplicação**

A classe `App` atua como ponto de entrada do sistema. Ela recebe os parâmetros informados na linha de comando (estrutura e tamanho máximo de entrada) e delega a execução ao controlador.

**Controle**

A classe `BenchController` é responsável por selecionar e instanciar o benchmark correspondente à estrutura solicitada. Essa centralização permite incluir novas estruturas adicionando apenas uma nova implementação de benchmark.

**Protocolo experimental**

A classe abstrata `Bench` implementa todo o protocolo de benchmark, incluindo:

- Carregamento das entradas
- Geração das escalas de teste
- Warmup da JVM
- Execução das medições
- Cálculo estatístico dos resultados
- Gravação em arquivos CSV

As classes concretas apenas definem qual estrutura será utilizada.

### Fluxo de execução

1. `App` recebe os parâmetros da linha de comando
2. `BenchController` seleciona o benchmark correspondente
3. A implementação de `Bench` é instanciada
4. O tamanho máximo da entrada é configurado
5. O método `run()` executa o protocolo experimental
6. Os resultados são registrados em arquivos CSV

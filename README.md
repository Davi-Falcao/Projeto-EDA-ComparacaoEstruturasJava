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
- Para graficos: Python 3 com `matplotlib`

## 2) Compilacao e execucao com Maven

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
result_<Nome_da_Estrutura>_<ordem>_<Caso>.csv
```

## 4) Gerar plotagem de gráficos automatica

Executar o a plotagem:
```bash
python ./scripts/generatorGraphs/generate_graphs.py <nome_estrutura>
```
Os graficos são salvos automaticamente em:
```bash
repository/result/graphs/<Nome_estrutura>
```

# Análise de Estruturas de Dados – Tempo de Execução e Consumo de Memória

Este repositório contém a experimentação realizada sobre a comparação de diferentes estruturas de dados que apresentam operações semelhantes, com o objetivo de analisar o impacto de cada implementação em termos de **tempo de execução** e **consumo de memória**.

As estruturas avaliadas no experimento são:

- **ArrayList**
- **LinkedList**
- **Binary Search Tree (BST)**
- **AVL Tree**
- **Árvore Rubro-Negra (PV)**
- **Heap**
- **PriorityQueue**

Cada uma dessas estruturas possui características próprias de organização interna, o que influencia diretamente o custo das operações realizadas sobre os dados.

---

# Metodologia do Experimento

A experimentação realizada neste projeto foi baseada na execução de diferentes cenários de operações aplicadas às estruturas analisadas. Esses cenários representam diferentes combinações de inserção, remoção e busca, simulando possíveis padrões de uso encontrados em aplicações reais.

Os cenários utilizados foram:

- **100I0R0S** — 100% inserções
- **50I50R0S** — 50% inserções e 50% remoções
- **75I25R0S** — 75% inserções e 25% remoções
- **50I25R25S** — 50% inserções, 25% buscas e 25% remoções
- **50I0R50S** — 50% inserções e 50% buscas

Além disso, os experimentos foram executados utilizando diferentes padrões de distribuição de dados de entrada:

- **Random** — valores gerados aleatoriamente
- **Crescente** — valores ordenados em ordem crescente
- **Decrescente** — valores ordenados em ordem decrescente

Durante cada execução foram coletadas duas métricas principais:

- **Tempo de execução**, medido em **nanosegundos**
- **Consumo de memória**, medido em **bytes**

Os resultados obtidos foram armazenados em arquivos no formato **CSV**, permitindo posteriormente a geração de **gráficos comparativos** que mostram o comportamento das estruturas analisadas em diferentes cenários de execução.

---

## Estrutura do Benchmark

O benchmark foi projetado utilizando um modelo de abstração que separa a lógica de medição da implementação das estruturas de dados.

Para isso, o projeto utiliza dois elementos principais:

- uma **classe abstrata de benchmark (`Bench`)**
- uma **interface comum para as estruturas (`Estrutura`)**

Essa organização permite que diferentes estruturas sejam testadas utilizando exatamente o mesmo protocolo experimental.

---

## Arquitetura do Benchmark

O benchmark foi estruturado de forma a separar a lógica de medição da implementação das estruturas de dados.

Todas as estruturas implementam uma interface comum (`Estrutura`), que define as operações básicas utilizadas nos experimentos: inserção, remoção e busca. Dessa forma, diferentes estruturas podem ser testadas utilizando exatamente o mesmo protocolo de benchmark.

A execução dos experimentos é coordenada pela classe **BenchController**, responsável por intermediar a comunicação entre a aplicação e as implementações concretas de benchmark.

Cada estrutura possui uma classe específica de benchmark, como:

- `BenchArrayList`
- `BenchLinkedList`
- `BenchBST`
- `BenchAVL`
- `BenchPV`
- `BenchHeap`
- `BenchPriorityQueue`

O fluxo de execução ocorre da seguinte forma:

1. A classe **App** recebe os parâmetros informados na linha de comando.
2. O **BenchController** identifica qual benchmark deve ser executado.
3. A implementação concreta de **Bench** correspondente à estrutura é instanciada.
4. O tamanho máximo da entrada é configurado.
5. O método `run()` inicia a execução dos experimentos.

Essa organização centraliza a seleção das estruturas em um único ponto do sistema, facilitando a manutenção do código e permitindo a inclusão de novas estruturas sem modificar a lógica principal da aplicação.

---
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
mvn exec:java "-Dexec.java=<nome_do_benchmark> <tam_entrada>"
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
python ./scripts/generate_graphs.py <nome_estrutura>
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

# Introdução

A escolha de estruturas de dados adequadas é um dos fatores mais importantes no desenvolvimento de sistemas eficientes. Estruturas diferentes podem oferecer operações semelhantes — como inserção, remoção e busca — porém apresentar comportamentos significativamente distintos quando analisadas em termos de desempenho e consumo de recursos.

Com o crescimento constante do volume de dados manipulados por aplicações modernas, torna-se cada vez mais relevante compreender como diferentes estruturas se comportam em cenários reais de execução. Pequenas diferenças na forma como os dados são organizados em memória podem resultar em impactos consideráveis no tempo necessário para realizar operações fundamentais.

Neste contexto, este estudo propõe a implementação e análise comparativa de diferentes estruturas de dados amplamente utilizadas na prática computacional. O objetivo é observar como cada estrutura responde a diferentes padrões de uso, avaliando principalmente o custo das operações básicas e o consumo de memória associado.

Para tornar essa análise mais clara, as estruturas foram organizadas em três grupos principais: estruturas lineares, estruturas baseadas em árvores de busca e estruturas baseadas em heap.

---

# Estruturas de Dados Avaliadas

## Estruturas baseadas em listas

O primeiro grupo analisado é composto por estruturas lineares utilizadas para armazenamento sequencial de elementos. Nesse grupo foram consideradas as estruturas **ArrayList** e **LinkedList**, que representam duas abordagens diferentes para a organização de dados sequenciais.

A **ArrayList** é baseada em um vetor dinâmico, no qual os elementos são armazenados em posições contíguas de memória. Essa característica permite acesso direto por índice em tempo constante, o que torna operações de leitura extremamente eficientes. Entretanto, operações de inserção ou remoção em posições intermediárias podem exigir deslocamento de diversos elementos, aumentando o custo da operação.

Por outro lado, a **LinkedList** utiliza uma estrutura baseada em nós encadeados. Cada elemento da lista contém uma referência para o próximo elemento da sequência, formando uma cadeia de nós conectados. Essa abordagem elimina a necessidade de realocação de blocos de memória e permite inserções e remoções mais eficientes em determinadas posições da estrutura. Em contrapartida, o acesso aos elementos torna-se sequencial, o que aumenta o custo de operações que dependem de percorrer a lista.

A comparação entre essas duas estruturas permite observar principalmente o impacto do acesso direto por índice, da realocação de memória e da manipulação de ponteiros na eficiência das operações.

---

## Estruturas baseadas em árvores de busca

O segundo grupo de estruturas analisadas é composto por árvores de busca, que organizam os elementos de forma hierárquica com o objetivo de tornar operações de busca mais eficientes.

Nesse grupo foram analisadas três estruturas: **Binary Search Tree (BST)**, **AVL Tree** e **Árvore Rubro-Negra (PV)**.

A **BST** representa a forma mais simples de árvore binária de busca, onde os elementos são organizados de forma que valores menores ficam posicionados à esquerda e valores maiores à direita. Essa estrutura permite buscas eficientes quando a árvore está relativamente balanceada, porém pode se tornar extremamente desbalanceada dependendo da ordem de inserção dos elementos, fazendo com que seu desempenho se aproxime do de uma lista encadeada.

Para resolver esse problema surgem estruturas auto-balanceadas como a **AVL Tree**, que mantém a diferença de altura entre as subárvores esquerda e direita dentro de um limite predefinido. Sempre que essa diferença ultrapassa o limite permitido, rotações são realizadas para restaurar o balanceamento da árvore.

Outra abordagem de balanceamento é utilizada na **Árvore Rubro-Negra (PV)**. Nessa estrutura, cada nó recebe uma cor (vermelho ou preto) e uma série de propriedades é mantida para garantir que a altura da árvore permaneça limitada. Embora o balanceamento não seja tão rigoroso quanto o da AVL, essa estrutura apresenta excelente desempenho na prática e é amplamente utilizada em implementações de bibliotecas padrão.

A comparação entre essas três estruturas permite analisar o impacto do balanceamento automático no desempenho das operações de inserção, remoção e busca.

---

## Estruturas baseadas em heap

O terceiro grupo analisado é composto por estruturas baseadas em **heap**, frequentemente utilizadas na implementação de filas de prioridade.

Nesse grupo foram analisadas as estruturas **Heap** e **PriorityQueue**.

O **heap binário** organiza os elementos de forma que o elemento de maior prioridade permaneça sempre na raiz da estrutura. Essa organização permite acesso eficiente ao elemento prioritário e garante operações de inserção e remoção com complexidade logarítmica.

Já a **PriorityQueue** representa uma implementação de fila de prioridade fornecida pela biblioteca padrão da linguagem Java. Internamente, essa estrutura também utiliza um heap como base de armazenamento, mas oferece uma abstração de alto nível que facilita sua utilização em aplicações.

A análise dessas estruturas permite observar o comportamento de estruturas que utilizam árvores implícitas para organizar os elementos e manter prioridades entre os dados armazenados.

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
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
- **10⁴ elementos** para experimentos com **Heap**
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

---

## ArrayList X LinkedList ##

### Uso de Memória ###

Por ser uma estrutura baseada em *array*, o aumento do uso de memória em uma *arraylist* depende da implementação do método resize(). Nesse estudo o redimensionamento sempre cria um array com o dobro da capacidade do anterior (sendo a capacidade inicial de 3000 posições). Assim, observa-se que o consumo de memória cresce geometricamente com razão 2 a partir do consumo inicial de 12040 bytes. Como o resize() é sempre chamado durante inserções quando a arraylist excede sua capacidade, então, mesmo quando a quantidade de elementos não dobra o espaço da memória já estará sendo consumido sem abrigar o total de valores permitidos. Devido a essa natureza do redimensionamento o gráfico possui uma curva de crescimento exponencial.

Já uma *linkedlist* é uma estrutura baseada em nós , em que cada nó possui um ponteiro para o próximo elemento e, no caso da implementação utilizada aqui, também para o elemento anterior, sendo assim uma *double linkedlist*. Cada elemento inserido cria um novo objeto na memória com seus ponteiros, o que faz o consumo de memória subir na operação de inserção. O gráfico evidencia um comportamento onde a memória cresce proporcionalmente ao aumento da entrada. Ou seja, existe um crescimento linear do uso de memória nessa estrutura.

Em termos de memória, embora linkedlist apresente um crescimento linear, seu consumo de memória aumenta a cada inserção. Diferentemente, a arraylist só exige mais memória quando não existirem mais posições disponíveis na capacidade atual. Isso fica evidente nos gráficos, para uma entrada de aproxidamente 20000 elementos, a arraylist consome em torno de 10^5 bytes, enquanto a linkedlist 5 * 10^5 bytes. Assim, pode-se concluir que essa diferença tende a continuar significativa junto do aumento da entrada, apenas em cenários muito restritos (entrada até 500 elementos) a linkedlist se mostra econômica com a memória.

|           |           |
|-----------|-----------|
| ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/arraylist/arraylist_random_workload_100I0R0S_memoria.png) | ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/linkedlist/linkedlist_random_workload_100I0R0S_memoria.png) |

### Tempo de execução ###

Em relação ao tempo de execução, os gráficos e dados mostram que a arraylist apresenta desempenho superior, sendo consideravelmente mais rápida que a linkedlist em realizar buscas e remoções. Embora ambas tenham complexidade O(n) nessas operações, linkedlist leva mais tempo para executá-las. Isso se deve ao fato de que, na linkedlist, os nós estão espalhados em diferentes lugares da memória, aumentando o tempo necessário para percorrer a lista. Por outro lado, na arraylist os elementos estão guardados no mesmo espaço da memória, favorecendo encontrar elementos mais rapidamente. 

Os resultados do estudo mostram a arraylist sendo até 13 vezes mais eficiente no tempo de execução que a linkedlist.

|           |           |
|-----------|-----------|
| ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/arraylist/arraylist_referencia_random_tempo.png) | ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/linkedlist/linkedlist_referencia_random_tempo.png) |

Algo interessante que pode ser percebido nos dados do estudo e que ficaram mais evidentes no caso **50I25R25S**, é a diferença entre o tempo de execução da busca e da remoção. Nos gráficos abaixo nota-se que essas operações levam praticamente o mesmo tempo para serem executadas na linkedlist, tendo uma variação pouco significativa. Entretanto, na arraylist com o crescimento da entrada essa disparidade se mostrou um tanto quanto relevante.

Esse resultado acontece por causa da forma que a arraylist remove seus elementos. Para evitar uma posição vazia entre dois elementos após a remoção, a arraylist desloca todos os elementos que estão à direita uma posição para a esquerda. Esse processo tem complexidade linear, o que tende a ser custoso quando a entrada é grande, principalmente se o elemento removido estiver no início. Na linkedlist, apenas os ponteiros são atualizados, mudanças de referência são constantes e quase não afetam o desempenho.

Na maior entrada testada (10^5 elementos) a remoção levou quase o dobro do tempo (8801 ns) que a busca (4714 ns) na arraylist.

|           |           |
|-----------|-----------|
| ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/arraylist/arraylist_random_workload_50I25R25S_tempo.png) | ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/linkedlist/linkedlist_random_workload_50I25R25S_tempo.png) |

---

## BST X AVL X PV ##

### Uso de Memória ###

Árvores Rubro-Negra (PV) e AVL são versões balanceadas da Árvore de Busca Binária (BST). Por esse motivo, carregam a mesma lógica básica de nós, possuindo três ponteiros (parent, left e right). Logo, compartilhando dessa organização para armazenar elementos, a memória consumida pelas três estruturas segue a mesma curva de crescimento linear, proporcional ao aumento do número de nós na árvore. Pela característica de nós encadeados, armazenando diversos ponteiros para objetos em variados lugares na memória, o uso dela é consideravelmente elevado em comparação às estruturas baseadas em array.

Abaixo os gráficos provam esse crescimento parelho entre as estruturas.

![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/bst/bst_random_workload_100I0R0S_memoria.png)

|           |           |
|-----------|-----------|
| ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/avl/avl_random_workload_100I0R0S_memoria.png) | ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/pv/pv_random_workload_100I0R0S_memoria.png) |

### Tempo de Execução ###

Distintamente do consumo de memória, o tempo de execução difere significativamente tanto dentro de uma mesma árvore quanto entre estruturas diferentes. Fatores como carga de operações e a ordem de inserção dos dados influenciam diretamente a eficiência das árvores. Com isso, dois cenários principais foram analisados a fim de permitir uma compreensão abrangente do comportamento e desempenho dessas estruturas no estudo, evitando repetições de dados e conclusões.

#### 1. Entrada de Dados Desordenados ####

Comparando cenários em que metade das operações são de remoção com aqueles em que metade são de busca, para ter-se uma bom ângulo de análise bem distribuído, observa-se que a remoção é quase duas vezes mais lenta que a busca. Esse comportamento é esperado, pois a remoção envolve a atualização de ponteiros na árvore. Esse processo se torna custoso quando o nó removido possui dois filhos, porque é necessário localizar seu predecessor ou sucessor para garantir a propriedade da BST.

|           |           |
|-----------|-----------|
| ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/bst/bst_random_workload_50I50R0S_tempo.png) | ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/bst/bst_random_workload_50I0R50S_tempo.png) |

No mesmo cenário, em árvores AVL e PV, a remoção é consideravelmente mais lenta devido aos balanceamentos feito na estrutura após a operação, que atualiza diversas referências dependendo da posição do elemento na árvore. Nesse caso, a BST se mostrou no geral mais eficiente que as versões balanceadas.

|           |           |
|-----------|-----------|
| ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/avl/avl_random_workload_50I50R0S_tempo.png) | ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/avl/avl_random_workload_50I0R50S_tempo.png) |

|           |           |
|-----------|-----------|
| ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/pv/pv_random_workload_50I50R0S_tempo.png) | ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/pv/pv_random_workload_50I0R50S_tempo.png) |


#### 2. Entrada de Dados Ordenados ####

Agora nessa situação onde os dados inseridos são crescentes, a árvore irá pender para o lado direito afetando muito negativamente a eficiência da BST em todas as operações.

|           |           |
|-----------|-----------|
| ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/bst/bst_crescente_workload_50I50R0S_tempo.png) | ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/bst/bst_crescente_workload_50I0R50S_tempo.png) |

As estrutura AVL e PV não apresentaram mudanças de comportamento graças ao auto-balanceamento, garantindo que a busca e inserção continuem O(log n). Assim, no cenário em que dados são inseridos em ordens crescente ou decrescente, as versões balanceadas de árvore garantem bastante eficiência e desempenho para se trabalhar.

|           |           |
|-----------|-----------|
| ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/avl/avl_crescente_workload_50I50R0S_tempo.png) | ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/avl/avl_crescente_workload_50I0R50S_tempo.png) |

|           |           |
|-----------|-----------|
| ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/pv/pv_crescente_workload_50I50R0S_tempo.png) | ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/pv/pv_crescente_workload_50I0R50S_tempo.png) |

---

## Heap X PriorityQueue ##

### Uso de Memória ###

Heap e PriorityQueue são estruturas que utilizam de um array para armazenar seus elementos. Com isso, o uso de memória é medido pelo consumo do array inicial multiplicado pela quantidade de vezes que ele é redimensionado. Seguindo a mesma lógica de arraylist e linkedlist, o gráfico cresce de maneira exeponencial mas com a vatagem de ser um espaço de memória contíguo que quanto maior a entrada menos frequentes são os redimensionamentos. Nas implementações desse estudo ambas estruturas possuem capacidade inicial em 20 posições.

Portanto, em questões de consumo de memória, ambas as estruturas são equivalentes e não apresentam diferenças perceptíveis.

|           |           |  
|-----------|-----------|                                                                          
|![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/heap/heap_random_workload_100I0R0S_memoria.png) | ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/priorityqueue/priorityqueue_random_workload_100I0R0S_memoria.png) |

### Tempo de Execução ###

Para deixar claro o quão distinto o desempenho em Heap e PriorityQueue é, todas as operações serão analisadas ao mesmo tempo no cenário **50I25R25S** em 3 situações de entradas diferentes.

##### 1. Entrada de Dados Desordenados #####

Nesse primeiro cenário, o comportamento das duas estruturas é praticamente oposto. Na heap, o elemento é sempre adicionado na última posição e, em seguida, é realizado o heapify para manter a propriedade da árvore, com custo de O(log n), correspondente à altura da árvore. Já na priorityqueue, o valor também é inserido no final, mas é executado um *insertion sort* para manter a ordenação da estrutura, tornando a operação mais custosa, ocorrendo em O(n).

Em relação à remoção, a heap remove o elemento e aplica novamente o heapify, mantendo o custo em O(log n). Por outro lado, na priority queue, a remoção ocorre em tempo constante, pois os valores já foram ordenados na inserção; basta retirar o elemento da última posição.

Na busca as estruturas têm complexidade linear com tempo de execução bastante semelhantes nos dados adquiridos pelo estudo.

|           |           |
|-----------|-----------|
| ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/heap/heap_random_workload_50I25R25S_tempo.png) | ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/priorityqueue/priorityqueue_random_workload_50I25R25S_tempo.png) |

##### 2. Entrada de Dados Crescentes #####

No segundo cenário, os gráficos e os tempos de execução mostraram resultados praticamente idênticos entre as duas estruturas. No entanto, o crescimento da entrada de dados beneficiou especialmente a priorityqueue, pois os elementos chegavam ordenados, tornando a inserção quase constante. Na heap, por outro lado, o desempenho permaneceu inalterado, pois ela mantém garantidamente complexidade O(log n) em remoções e percorre linearmente na busca.

|           |           |
|-----------|-----------|
| ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/heap/heap_crescente_workload_50I25R25S_tempo.png) | ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/priorityqueue/priorityqueue_crescente_workload_50I25R25S_tempo.png) |

##### 3. Entrada de Dados Decrescentes #####

No terceiro cenário, o gráfico da heap permanece o mesmo, como já explicado anteriormente. Porém, a priorityqueue sofreu uma grande queda de desempenho, pois o insertion sort agora opera sempre no pior caso, precisando percorrer toda a estrutura até encontrar a posição correta para inserir cada elemento. No cenário 1, a inserção de 10^4 elementos na priorityqueue levava cerca de 2000 ns, agora, o tempo passou para pouco mais de 4000 ns.

|           |           |
|-----------|-----------|
| ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/heap/heap_decrescente_workload_50I25R25S_tempo.png) | ![Graphic](src/main/java/dev/ProjetoEDA/repository/graphs/priorityqueue/priorityqueue_decrescente_workload_50I25R25S_tempo.png) |

---

### Observações sobre análises e limitações ###

Inevitavelmente, algumas limitações afetaram a análise mais profunda e precisa do comportamento das estruturas estudadas neste projeto. Durante as medições do tempo de execução, a verificação do uso de memória gerou ruídos e alterações nos dados adquiridos, que podem ou não ser perceptíveis nos gráficos e nos valores medidos. Alguns gráficos apresentaram ruídos evidentes, porém esses foram considerados irrelevantes para a análise principal e puderam ser ignorados. No entanto, a limitação no tamanho das entradas, principalmente nas estruturas heap e BST, impacta diretamente as conclusões vinda a partir das medições. A amplitude insuficiente dos dados dificulta a análise de determinadas características, como, por exemplo, a variação de valores brutos do tempo de execução em diferentes cenários e estruturas.
 
---

### Conclusões finais ###

Ficou evidenciado que a escolha da melhor estrutura de dados depende significativamente das necessidades do banco de dados e do padrão de acesso aos dados.

Na comparação entre árvores binárias de pesquisa, a BST é uma ótima escolha quando o banco de dados será muito acessado e os valores estão bem distribuídos. Nesse caso, a estrutura tende a manter uma boa eficiência nas suas operações. Mas, quando os dados seguem um padrão de crescimento ou decrescimento, a árvore pode se tornar desbalanceada, prejudicando o desempenho. Então, é mais adequado utilizar versões balanceadas, como AVL ou PV, que garantem balanceamento e melhor desempenho no pior caso. Já na análise  entre heap e priorityqueue, as diferenças são mais específicas e dependem das operações realizadas com maior frequência. A Heap apresenta um comportamento mais estável e consistente em diferentes cenários, garantindo eficiência na gestão dos dados, especialmente nas operações de inserção e remoção. A priorityqueue já depende fortemente do tipo de dados e das operações mais realizada. Seu funcionamento é semelhante ao de uma heap quando as entradas são crescentes, porém, quando as entradas são decrescentes, a estrutura pode se tornar menos eficiente, especialmente nas operações de inserção. Com valores aleatórios, o desempenho de inserção continua sendo um pouco mais lento que o da heap, mas ainda se mantém aceitável, enquanto as operações de busca e remoção permanecem eficientes. Na disputa entre arraylist e linkedlist a conclusão é mais direta e simple, arraylist é majoritariamente mais eficiente em todos os quesitos. Apesar do crescimento de memória do arraylist ser exponencial, na prática o aumento demora muito para acontecer e não se torna um problema para a comparação. Sobre as operações, o endereçamento contíguo de memória permite inserções e buscas extremamente mais rápidas do que a linkelist. Mesmo na remoção com o deslocamento dos elementos os dados mostraram que o arraylist continua mais eficiente. Apenas em cenários mais restritos a linkelist teria vantagem como remoções ou inserções no início e fim da lista.

Em síntese muitas varíaveis devem ser analisadas antes de definir a melhor estrutura para cada cenário e os estudos de casos facilitam nessa decisão.

---

## Referências ## 

XAVIER, Luiz Gustavo Coutinho; MENDIZABAL, Odorico M. Análise de Desempenho de Estruturas de Dados Concorrentes Implementadas na Linguagem Java. In: Escola Regional de Alto Desempenho da Região Sul (ERAD-RS). SBC, 2019.Acesso em: 14 fev. 2026.

CORMEN, Thomas H.; LEISERSON, Charles E.; RIVEST, Ronald L.; STEIN, Clifford; Algoritmos: teoria e prática. 3. ed. Rio de Janeiro: Campus, 2002. Acesso em: 14 fev. 2026.

João Arthur Brunet, 2019. Estruturas de Dados e Algoritmos, Computação @ UFCG, <http://joaoarthurbm.github.io/eda>. Acesso em: 14 fev. 2026.

ORACLE. Java Platform, Standard Edition – API Specification. Oracle, 2023. Disponível em: https://docs.oracle.com/javase/8/docs/api/. Acesso em: 14 fev. 2026.

STACKIFY. How to Monitor CPU, Memory, and Disk Usage in Java. Stackify. Disponível em: https://stackify.com/how-to-monitor-cpu-memory-and-disk-usage-in-java/
. Acesso em: 13 mar. 2026.

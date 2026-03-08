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

Executar o App (perfil `app`):

```bash
mvn exec:java "-Dexec.java=<nome_do_benchmark> <tam_entrada>"
```
## 3) Arquivos de resultados são gerados automaticamente

Os arquivos de resultados são salvos automaticamente em:
repository/result/<Nome_estrutura>/

```padrão do resultado
result_<Nome_da_Estrutura>_<ordem>_<Caso>.csv
```
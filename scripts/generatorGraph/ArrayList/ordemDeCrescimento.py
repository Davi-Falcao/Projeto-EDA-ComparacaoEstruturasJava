import pandas as pd
import matplotlib.pyplot as plt

file_path = './data/results/ArrayList/resultOrdemDeAdicao.csv'
print(f'Lendo dados do arquivo: {file_path}')
df = pd.read_csv(file_path)

insercao = df[df['Operacao'] == 'I']

plt.figure(figsize=(10, 6))
plt.plot(insercao['TamanhoEntrada'], insercao['TempoExecucao(ns)'], label='Inserção', linestyle='-', color='blue')

plt.xlabel('Tamanho da Entrada')
plt.ylabel('Tempo de Execução (ns)')
plt.title('Tempo de Execução vs Tamanho da Entrada (Inserção)')
plt.legend()
plt.grid(True)
plt.show()
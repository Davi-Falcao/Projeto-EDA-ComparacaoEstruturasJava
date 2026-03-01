import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.ticker import ScalarFormatter

plt.close('all')  # evita sobras de figuras anteriores

file_path = './data/results/ArrayList/resultOrdemDeAdicao.csv'
df = pd.read_csv(file_path)

insercao = df[df['Operacao'] == 'I'].copy()
insercao['TamanhoEntrada'] = pd.to_numeric(insercao['TamanhoEntrada'], errors='coerce')
insercao['TempoExecucao(ns)'] = pd.to_numeric(insercao['TempoExecucao(ns)'], errors='coerce')
insercao = insercao.sort_values('TamanhoEntrada')

fig, ax = plt.subplots(figsize=(10, 6))
ax.plot(insercao['TamanhoEntrada'], insercao['TempoExecucao(ns)'], label='Inserção', color='blue')
ax.set_ylim(0, 10000)

ax.set_xlabel('Tamanho da Entrada')
ax.set_ylabel('Tempo de Execução (ns)')
ax.set_title('Tempo de Execução vs Tamanho da Entrada (Inserção)')
ax.legend()
ax.grid(True)

# tira notação 1e6 e mostra número inteiro no eixo X
fmt = ScalarFormatter(useOffset=False)
fmt.set_scientific(False)
ax.xaxis.set_major_formatter(fmt)

plt.tight_layout()
plt.show()
plt.close(fig)



file_path = './data/results/ArrayList/resultOrdemDeBusca.csv'
df = pd.read_csv(file_path)

busca = df[df['Operacao'] == 'S'].copy()
busca['TamanhoEntrada'] = pd.to_numeric(busca['TamanhoEntrada'], errors='coerce')
busca['TempoExecucao(ns)'] = pd.to_numeric(busca['TempoExecucao(ns)'], errors='coerce')
busca = busca.sort_values('TamanhoEntrada')

fig, ax = plt.subplots(figsize=(10, 6))
ax.plot(busca['TamanhoEntrada'], busca['TempoExecucao(ns)'], label='Busca', color='red')

ax.set_xlabel('Tamanho da Entrada')
ax.set_ylabel('Tempo de Execução (ns)')
ax.set_title('Tempo de Execução vs Tamanho da Entrada (Busca)')
ax.legend()
ax.grid(True)

# tira notação 1e6 e mostra número inteiro no eixo X
fmt = ScalarFormatter(useOffset=False)
fmt.set_scientific(False)
ax.xaxis.set_major_formatter(fmt)

plt.tight_layout()
plt.show()
plt.close(fig)

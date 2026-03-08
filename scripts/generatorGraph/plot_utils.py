import csv
import os
from collections import defaultdict
import matplotlib.pyplot as plt


def read_points(csv_path):
    """
    CSV esperado:
      0 TamanhoEntrada
      1 Operacao
      2 TempoExecucao(ns)
      3 MemoriaUso(bytes)

    Conversões internas:
    - tempo: ns
    - memória: bytes

    Retorna:
    - tempo_series:   dict[operacao] -> list[(entrada, tempo_ns)]
    - memoria_series: dict[operacao] -> list[(entrada, memoria_bytes)]
    """

    tempo_series = defaultdict(list)
    memoria_series = defaultdict(list)

    with open(csv_path, "r", encoding="utf-8") as f:
        reader = csv.reader(f)
        next(reader)  # pula cabeçalho

        for row in reader:
            if not row:
                continue

            entrada = int(row[0])
            operacao = row[1].strip()
            tempo_ns = float(row[2])
            memoria_bytes = float(row[3])

            tempo_series[operacao].append((entrada, tempo_ns))
            memoria_series[operacao].append((entrada, memoria_bytes))

    for operacao in tempo_series:
        tempo_series[operacao].sort(key=lambda x: x[0])

    for operacao in memoria_series:
        memoria_series[operacao].sort(key=lambda x: x[0])

    return tempo_series, memoria_series


def nome_operacao(op):
    nomes = {
        "I": "Insertion",
        "R": "Remove",
        "S": "Search"
    }
    return nomes.get(op, op)


def garantir_diretorio(path):
    os.makedirs(path, exist_ok=True)


def plot_time_csv(csv_path, output_path=None):
    tempo_series, _ = read_points(csv_path)

    plt.figure()

    for operacao, pts in tempo_series.items():
        xs = [p[0] for p in pts]
        ys = [p[1] for p in pts]
        plt.plot(xs, ys, marker="o", label=nome_operacao(operacao))

    plt.xlabel("TamanhoEntrada (N)")
    plt.ylabel("Tempo (ns)")
    plt.title("Tempo por operação vs Tamanho da entrada")
    plt.legend()
    plt.xscale("log")

    # limite mínimo vertical = 1000 ns
    plt.ylim(bottom=100)
    plt.tight_layout()

    if output_path:
        diretorio = os.path.dirname(output_path)
        if diretorio:
            garantir_diretorio(diretorio)
        plt.savefig(output_path, dpi=200)
        plt.close()
    else:
        plt.show()


def plot_mem_csv(csv_path, output_path=None):
    _, memoria_series = read_points(csv_path)

    plt.figure()

    for operacao, pts in memoria_series.items():
        xs = [p[0] for p in pts]
        ys = [p[1] for p in pts]
        plt.plot(xs, ys, marker="o", label=nome_operacao(operacao))

    plt.xlabel("TamanhoEntrada (N)")
    plt.ylabel("Memória (bytes)")
    plt.title("Memória por operação vs Tamanho da entrada")
    plt.legend()
    plt.xscale("log")
    plt.tight_layout()

    if output_path:
        diretorio = os.path.dirname(output_path)
        if diretorio:
            garantir_diretorio(diretorio)
        plt.savefig(output_path, dpi=200)
        plt.close()
    else:
        plt.show()
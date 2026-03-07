import csv
from collections import defaultdict
import matplotlib.pyplot as plt

BASE_PATH = "/repository/results/Graphs/"


def mnt_path(filename):
    return BASE_PATH + filename


def read_points(csv_path):
    """
    CSV esperado:

    0 TamanhoEntrada
    1 Ordem
    2 Caso
    3 TempoExecucao(ns)
    4 MemoriaUso(bytes)

    Conversões:
    tempo -> ms
    memória -> bytes
    """

    tempo_series = defaultdict(list)
    memoria_series = defaultdict(list)

    with open(csv_path, "r", encoding="utf-8") as f:
        reader = csv.reader(f)
        next(reader)

        for row in reader:
            if not row:
                continue

            entrada = int(row[0])
            ordem = row[1]
            caso = row[2]

            tempo_ms = float(row[3]) / 1_000_000
            memoria_bytes = float(row[4])

            tempo_series[(ordem, caso)].append((entrada, tempo_ms))
            memoria_series[(ordem, caso)].append((entrada, memoria_bytes))

    for k in tempo_series:
        tempo_series[k].sort(key=lambda x: x[0])

    for k in memoria_series:
        memoria_series[k].sort(key=lambda x: x[0])

    return tempo_series, memoria_series


def plot_time_csv(csv_path, output_path=None):

    save_path = mnt_path(output_path) if output_path else None
    tempo_series, _ = read_points(csv_path)

    plt.figure()

    for (ordem, caso), pts in tempo_series.items():
        xs = [p[0] for p in pts]
        ys = [p[1] for p in pts]

        plt.plot(xs, ys, marker="o", label=f"{ordem} | {caso}")

    plt.xlabel("TamanhoEntrada (N)")
    plt.ylabel("Tempo (ms)")
    plt.title("Tempo de execução vs Tamanho da entrada")
    plt.legend()
    plt.xscale("log")
    plt.tight_layout()

    if save_path:
        plt.savefig(save_path, dpi=200)
    else:
        plt.show()


def plot_mem_csv(csv_path, output_path=None):

    save_path = mnt_path(output_path) if output_path else None
    _, memoria_series = read_points(csv_path)

    plt.figure()

    for (ordem, caso), pts in memoria_series.items():
        xs = [p[0] for p in pts]
        ys = [p[1] for p in pts]

        plt.plot(xs, ys, marker="o", label=f"{ordem} | {caso}")

    plt.xlabel("TamanhoEntrada (N)")
    plt.ylabel("Memória (bytes)")
    plt.title("Uso de memória vs Tamanho da entrada")
    plt.legend()
    plt.xscale("log")
    plt.tight_layout()

    if save_path:
        plt.savefig(save_path, dpi=200)
    else:
        plt.show()

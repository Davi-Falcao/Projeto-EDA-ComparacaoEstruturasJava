import csv
from collections import defaultdict
import matplotlib.pyplot as plt


def read_points(csv_path):
    series = defaultdict(list)

    with open(csv_path, "r", encoding="utf-8") as f:
        reader = csv.reader(f)
        next(reader)  # pula header

        for entrada, valor, caso, estrutura in reader:
            entrada = int(entrada)
            valor = float(valor)

            series[(estrutura, caso)].append((entrada, valor))

    for k in series:
        series[k].sort(key=lambda x: x[0])

    return series


def plot_time_csv(csv_path, output_path=None):
    data = read_points(csv_path)

    plt.figure()

    for (estrutura, caso), pts in data.items():
        xs = [p[0] for p in pts]
        ys = [p[1] / 1e6 for p in pts]  # ns -> ms

        plt.plot(xs, ys, marker="o", label=f"{estrutura} | {caso}")

    plt.xlabel("Entrada (N)")
    plt.ylabel("Tempo (ms)")
    plt.title("Tempo vs Entrada")
    plt.legend()

    plt.xscale("log")

    plt.tight_layout()

    if output_path:
        plt.savefig(output_path, dpi=200)
    else:
        plt.show()


def plot_mem_csv(csv_path, output_path=None):
    data = read_points(csv_path)

    plt.figure()

    for (estrutura, caso), pts in data.items():
        xs = [p[0] for p in pts]
        ys = [p[1] / (1024**2) for p in pts]  # bytes -> MB

        plt.plot(xs, ys, marker="o", label=f"{estrutura} | {caso}")

    plt.xlabel("Entrada (N)")
    plt.ylabel("Memória (MB)")
    plt.title("Memória vs Entrada")
    plt.legend()

    plt.xscale("log")

    plt.tight_layout()

    if output_path:
        plt.savefig(output_path, dpi=200)
    else:
        plt.show()

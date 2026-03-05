import csv
from collections import defaultdict
import matplotlib.pyplot as plt


def read_points(csv_path, y_index: int):
    """
    CSV fixo:
      0 Tamanho_Entrada
      1 Caso
      2 Estrutura
      3 Tempo_ms
      4 Memoria_bytes

    y_index: 3 para tempo, 4 para memória
    Retorna: dict[(estrutura, caso)] -> list[(entrada_int, y_float)]
    """
    series = defaultdict(list)

    with open(csv_path, "r", encoding="utf-8") as f:
        reader = csv.reader(f)
        next(reader)  # pula header

        for row in reader:
            entrada = int(row[0])
            caso = row[1]
            estrutura = row[2]
            y = float(row[y_index])

            series[(estrutura, caso)].append((entrada, y))

    for k in series:
        series[k].sort(key=lambda x: x[0])

    return series


def plot_time_csv(csv_path, output_path=None):
    data = read_points(csv_path, y_index=3)  # Tempo_ms

    plt.figure()
    for (estrutura, caso), pts in data.items():
        xs = [p[0] for p in pts]
        ys = [p[1] for p in pts]  # já está em ms
        plt.plot(xs, ys, marker="o", label=f"{estrutura} | {caso}")

    plt.xlabel("Tamanho_Entrada (N)")
    plt.ylabel("Tempo (ms)")
    plt.title("Tempo vs Entrada")
    plt.legend()
    plt.xscale("log")
    plt.tight_layout()

    if output_path:
        plt.savefig(output_path, dpi=200)
    else:
        plt.show()


def plot_mem_csv(csv_path, output_path=None, unit="MB"):
    data = read_points(csv_path, y_index=4)  # Memoria_bytes

    div = 1
    if unit == "KB":
        div = 1024
    elif unit == "MB":
        div = 1024**2
    elif unit == "GB":
        div = 1024**3

    plt.figure()
    for (estrutura, caso), pts in data.items():
        xs = [p[0] for p in pts]
        ys = [p[1] / div for p in pts]
        plt.plot(xs, ys, marker="o", label=f"{estrutura} | {caso}")

    plt.xlabel("Tamanho_Entrada (N)")
    plt.ylabel(f"Memória ({unit})")
    plt.title("Memória vs Entrada")
    plt.legend()
    plt.xscale("log")
    plt.tight_layout()

    if output_path:
        plt.savefig(output_path, dpi=200)
    else:
        plt.show()

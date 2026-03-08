import csv
import os
from collections import defaultdict
import matplotlib.pyplot as plt


def read_points(csv_path):
    """
    CSV esperado:
      0 TamanhoEntrada
      1 Operacao
      2 TempoMedio(ns)
      3 MemoriaUso(bytes)

    Retorna:
    - tempo_series:   dict[operacao] -> list[(entrada, tempo_ns)]
    - memoria_series: dict[operacao] -> list[(entrada, memoria_bytes)]
    """
    tempo_series = defaultdict(list)
    memoria_series = defaultdict(list)

    with open(csv_path, "r", encoding="utf-8") as f:
        reader = csv.reader(f)
        next(reader, None)

        for row in reader:
            if not row or len(row) < 4:
                continue

            try:
                entrada = int(row[0])
                operacao = row[1].strip()
                tempo_ns = float(row[2])
                memoria_bytes = float(row[3])
            except (ValueError, IndexError):
                continue

            tempo_series[operacao].append((entrada, tempo_ns))
            memoria_series[operacao].append((entrada, memoria_bytes))

    for operacao in tempo_series:
        tempo_series[operacao].sort(key=lambda x: x[0])

    for operacao in memoria_series:
        memoria_series[operacao].sort(key=lambda x: x[0])

    return tempo_series, memoria_series


def nome_operacao(op):
    nomes = {
        "I": "Add",
        "S": "Search",
        "R": "Remove",
        "ADD": "Add",
        "SEARCH": "Search",
        "REMOVE": "Remove",
        "add": "Add",
        "search": "Search",
        "remove": "Remove",
    }
    return nomes.get(op, op)


def garantir_diretorio(path):
    os.makedirs(path, exist_ok=True)


def extrair_titulo_base(csv_path):
    """
    Formatos aceitos:
      result_<estrutura>_<ordem>_<operacao>.csv
      result_<estrutura>_<ordem>_workload_<caso>.csv
    """
    nome_arquivo = os.path.basename(csv_path)
    nome_sem_ext = os.path.splitext(nome_arquivo)[0]
    partes = nome_sem_ext.split("_")

    if len(partes) >= 4 and partes[0].lower() == "result":
        estrutura = partes[1]
        ordem = partes[2]

        if len(partes) >= 5 and partes[3].lower() == "workload":
            return estrutura, ordem, "Workload", "_".join(partes[4:])

        return estrutura, ordem, "Operação", "_".join(partes[3:])

    return None, None, None, None


def plot_time_csv(csv_path, output_path=None):
    tempo_series, _ = read_points(csv_path)
    estrutura, ordem, tipo, alvo = extrair_titulo_base(csv_path)

    plt.figure(figsize=(10, 6))

    for operacao, pts in tempo_series.items():
        xs = [p[0] for p in pts]
        ys = [p[1] for p in pts]
        plt.plot(xs, ys, marker="o", label=nome_operacao(operacao))

    plt.xlabel("Tamanho da entrada (N)")
    plt.ylabel("Tempo médio por operação (ns)")
    if estrutura and ordem and tipo and alvo:
        plt.title(f"Tempo - {estrutura.capitalize()} - {ordem} - {tipo} - {alvo}")
    else:
        plt.title("Tempo por operação vs Tamanho da entrada")

    if tempo_series:
        plt.legend()

    plt.xscale("log")
    plt.grid(True, which="both", linestyle="--", alpha=0.4)
    plt.tight_layout()

    if output_path:
        diretorio = os.path.dirname(output_path)
        if diretorio:
            garantir_diretorio(diretorio)
        plt.savefig(output_path, dpi=200, bbox_inches="tight")
        plt.close()
    else:
        plt.show()


def plot_mem_csv(csv_path, output_path=None):
    _, memoria_series = read_points(csv_path)
    estrutura, ordem, tipo, alvo = extrair_titulo_base(csv_path)

    plt.figure(figsize=(10, 6))

    for operacao, pts in memoria_series.items():
        xs = [p[0] for p in pts]
        ys = [p[1] for p in pts]
        plt.plot(xs, ys, marker="o", label=nome_operacao(operacao))

    plt.xlabel("Tamanho da entrada (N)")
    plt.ylabel("Memória usada (bytes)")

    if estrutura and ordem and tipo and alvo:
        plt.title(f"Memória - {estrutura.capitalize()} - {ordem} - {tipo} - {alvo}")
    else:
        plt.title("Memória por operação vs Tamanho da entrada")

    if memoria_series:
        plt.legend()

    plt.xscale("log")
    plt.grid(True, which="both", linestyle="--", alpha=0.4)
    plt.tight_layout()

    if output_path:
        diretorio = os.path.dirname(output_path)
        if diretorio:
            garantir_diretorio(diretorio)
        plt.savefig(output_path, dpi=200, bbox_inches="tight")
        plt.close()
    else:
        plt.show()
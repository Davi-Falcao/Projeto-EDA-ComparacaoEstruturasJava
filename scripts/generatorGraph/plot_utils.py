import csv
import os
from collections import defaultdict
import matplotlib.pyplot as plt


def read_points(csv_path):
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


def aplicar_limite_y_condicional(series_dict, limite_y=None, margem=0.10):
    if not series_dict:
        return

    valores = [y for pts in series_dict.values() for _, y in pts]

    if not valores:
        return

    ymax = max(valores)

    if limite_y is not None:
        if ymax <= limite_y:
            plt.ylim(0, limite_y)
    else:
        topo = ymax * (1 + margem)
        if topo == 0:
            topo = 1
        plt.ylim(0, topo)


def plot_series(series_dict, ylabel, title, output_path=None, limite_y=None, x_log=False):
    plt.figure(figsize=(10, 6))

    for operacao, pts in series_dict.items():
        xs = [p[0] for p in pts]
        ys = [p[1] for p in pts]
        plt.plot(xs, ys, marker="o", label=nome_operacao(operacao))

    plt.xlabel("Tamanho da entrada (N)")
    plt.ylabel(ylabel)
    plt.title(title)

    if series_dict:
        plt.legend()

    if x_log:
        plt.xscale("log")

    aplicar_limite_y_condicional(series_dict, limite_y)
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


def plot_time_csv(csv_path, output_path=None, limite_y=None, x_log=False):
    tempo_series, _ = read_points(csv_path)
    estrutura, ordem, tipo, alvo = extrair_titulo_base(csv_path)

    if estrutura and ordem and tipo and alvo:
        title = f"Tempo - {estrutura.capitalize()} - {ordem} - {tipo} - {alvo}"
    else:
        title = "Tempo por operação vs Tamanho da entrada"

    plot_series(
        tempo_series,
        ylabel="Tempo médio por operação (ns)",
        title=title,
        output_path=output_path,
        limite_y=limite_y,
        x_log=x_log
    )


def plot_mem_csv(csv_path, output_path=None, limite_y=None, x_log=False):
    _, memoria_series = read_points(csv_path)
    estrutura, ordem, tipo, alvo = extrair_titulo_base(csv_path)

    if estrutura and ordem and tipo and alvo:
        title = f"Memória - {estrutura.capitalize()} - {ordem} - {tipo} - {alvo}"
    else:
        title = "Memória por operação vs Tamanho da entrada"

    plot_series(
        memoria_series,
        ylabel="Memória usada (bytes)",
        title=title,
        output_path=output_path,
        limite_y=limite_y,
        x_log=x_log
    )


def juntar_series_csvs(csv_paths, tipo="tempo"):
    series_final = defaultdict(list)

    for csv_path in csv_paths:
        tempo_series, memoria_series = read_points(csv_path)
        origem = tempo_series if tipo == "tempo" else memoria_series

        for operacao, pts in origem.items():
            series_final[operacao].extend(pts)

    for operacao in series_final:
        series_final[operacao].sort(key=lambda x: x[0])

    return series_final


def plot_referencia_agrupada(
    csv_paths,
    estrutura,
    ordem,
    output_path=None,
    tipo="tempo",
    limite_y=None,
    x_log=False
):
    series = juntar_series_csvs(csv_paths, tipo=tipo)

    if tipo == "tempo":
        ylabel = "Tempo médio por operação (ns)"
        title = f"Tempo - {estrutura.capitalize()} - Referência - {ordem}"
    else:
        ylabel = "Memória usada (bytes)"
        title = f"Memória - {estrutura.capitalize()} - Referência - {ordem}"

    plot_series(
        series,
        ylabel=ylabel,
        title=title,
        output_path=output_path,
        limite_y=limite_y,
        x_log=x_log
    )
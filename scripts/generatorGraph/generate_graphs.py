import os
import argparse
from plot_utils import (
    plot_time_csv,
    plot_mem_csv,
    plot_referencia_agrupada,
)

INPUT_DIR = "src/main/java/dev/ProjetoEDA/repository/results"
OUTPUT_DIR = "src/main/java/dev/ProjetoEDA/repository/graphs"


def extrair_info_nome_arquivo(csv_path):
    nome_arquivo = os.path.basename(csv_path)
    nome_sem_ext = os.path.splitext(nome_arquivo)[0]
    partes = nome_sem_ext.split("_")

    if len(partes) < 4 or partes[0].lower() != "result":
        raise ValueError(f"Nome de arquivo inválido: {nome_arquivo}")

    estrutura = partes[1]
    ordem = partes[2]

    if len(partes) >= 5 and partes[3].lower() == "workload":
        tipo = "workload"
        alvo = "_".join(partes[4:])
    else:
        tipo = "operacao"
        alvo = "_".join(partes[3:])

    return estrutura, ordem, tipo, alvo


def listar_csvs(nome_estrutura=None):
    arquivos = []

    if nome_estrutura:
        pasta_estrutura = os.path.join(INPUT_DIR, nome_estrutura.lower())

        if not os.path.exists(pasta_estrutura):
            raise FileNotFoundError(f"Diretório não encontrado: {pasta_estrutura}")

        for arquivo in os.listdir(pasta_estrutura):
            if arquivo.lower().endswith(".csv"):
                arquivos.append(os.path.join(pasta_estrutura, arquivo))

        return sorted(arquivos)

    if not os.path.exists(INPUT_DIR):
        raise FileNotFoundError(f"Diretório não encontrado: {INPUT_DIR}")

    for pasta in os.listdir(INPUT_DIR):
        caminho_pasta = os.path.join(INPUT_DIR, pasta)

        if os.path.isdir(caminho_pasta):
            for arquivo in os.listdir(caminho_pasta):
                if arquivo.lower().endswith(".csv"):
                    arquivos.append(os.path.join(caminho_pasta, arquivo))

    return sorted(arquivos)


def gerar_grafico_workload(csv_path):
    estrutura, ordem, tipo, alvo = extrair_info_nome_arquivo(csv_path)

    if tipo != "workload":
        return

    pasta_saida = os.path.join(OUTPUT_DIR, estrutura.lower())
    os.makedirs(pasta_saida, exist_ok=True)

    tempo_output = os.path.join(
        pasta_saida,
        f"{estrutura.lower()}_{ordem}_{tipo}_{alvo}_tempo.png"
    )

    memoria_output = os.path.join(
        pasta_saida,
        f"{estrutura.lower()}_{ordem}_{tipo}_{alvo}_memoria.png"
    )

    plot_time_csv(
        csv_path,
        output_path=tempo_output,
        limite_y=None,
        x_log=False
    )

    plot_mem_csv(
        csv_path,
        output_path=memoria_output,
        limite_y=None,
        x_log=False
    )

    print(f"[OK] workload {os.path.basename(csv_path)}")


def agrupar_referencias_por_ordem(csvs):
    grupos = {
        "random": [],
        "crescente": [],
        "decrescente": []
    }

    estrutura_ref = None

    for caminho in csvs:
        estrutura, ordem, tipo, _ = extrair_info_nome_arquivo(caminho)

        if tipo == "operacao" and ordem in grupos:
            grupos[ordem].append(caminho)
            estrutura_ref = estrutura

    return estrutura_ref, grupos


def gerar_graficos_referencia_agrupados(nome_estrutura):
    csvs = listar_csvs(nome_estrutura)
    estrutura, grupos = agrupar_referencias_por_ordem(csvs)

    if not estrutura:
        print("Nenhum CSV de referência encontrado.")
        return

    pasta_saida = os.path.join(OUTPUT_DIR, estrutura.lower())
    os.makedirs(pasta_saida, exist_ok=True)

    for ordem, arquivos in grupos.items():
        if not arquivos:
            continue

        tempo_output = os.path.join(
            pasta_saida,
            f"{estrutura.lower()}_referencia_{ordem}_tempo.png"
        )

        memoria_output = os.path.join(
            pasta_saida,
            f"{estrutura.lower()}_referencia_{ordem}_memoria.png"
        )

        plot_referencia_agrupada(
            arquivos,
            estrutura=estrutura,
            ordem=ordem,
            output_path=tempo_output,
            tipo="tempo",
            limite_y=None,
            x_log=False
        )

        plot_referencia_agrupada(
            arquivos,
            estrutura=estrutura,
            ordem=ordem,
            output_path=memoria_output,
            tipo="memoria",
            limite_y=None,
            x_log=False
        )

        print(f"[OK] referência agrupada {estrutura} {ordem}")


def gerar_todos():
    encontrados = listar_csvs()

    if not encontrados:
        print("Nenhum CSV encontrado.")
        return

    estruturas = set()

    for caminho in encontrados:
        try:
            estrutura, _, tipo, _ = extrair_info_nome_arquivo(caminho)
            estruturas.add(estrutura.lower())

            if tipo == "workload":
                gerar_grafico_workload(caminho)

        except Exception as e:
            print(f"[ERRO] {os.path.basename(caminho)} -> {e}")

    for estrutura in sorted(estruturas):
        try:
            gerar_graficos_referencia_agrupados(estrutura)
        except Exception as e:
            print(f"[ERRO] referência agrupada {estrutura} -> {e}")


def gerar_por_estrutura(nome_estrutura):
    try:
        encontrados = listar_csvs(nome_estrutura)
    except Exception as e:
        print(f"[ERRO] {e}")
        return

    if not encontrados:
        print(f"Nenhum CSV encontrado para a estrutura: {nome_estrutura}")
        return

    for caminho in encontrados:
        try:
            _, _, tipo, _ = extrair_info_nome_arquivo(caminho)

            if tipo == "workload":
                gerar_grafico_workload(caminho)

        except Exception as e:
            print(f"[ERRO] {os.path.basename(caminho)} -> {e}")

    try:
        gerar_graficos_referencia_agrupados(nome_estrutura)
    except Exception as e:
        print(f"[ERRO] referência agrupada {nome_estrutura} -> {e}")


def main():
    parser = argparse.ArgumentParser(
        description="Gerador de gráficos: referência agrupada + workload individual"
    )

    parser.add_argument(
        "estrutura",
        nargs="?",
        help="Nome da estrutura para gerar os gráficos dela. Ex: arraylist, bst, avl"
    )

    args = parser.parse_args()

    if args.estrutura:
        gerar_por_estrutura(args.estrutura)
    else:
        gerar_todos()


if __name__ == "__main__":
    main()
import os
import argparse
from plot_util import plot_time_csv, plot_mem_csv

INPUT_DIR = "src/main/java/dev/ProjetoEDA/repository/results"
OUTPUT_DIR = "src/main/java/dev/ProjetoEDA/repository/graphs"



def extrair_info_nome_arquivo(csv_path):
    nome_arquivo = os.path.basename(csv_path)
    nome_sem_ext = os.path.splitext(nome_arquivo)[0]

    partes = nome_sem_ext.split("_")

    if len(partes) < 4:
        raise ValueError(f"Nome de arquivo inválido: {nome_arquivo}")

    prefixo = partes[0]
    estrutura = partes[1]
    ordem = partes[2]
    caso = "_".join(partes[3:])

    if prefixo.lower() != "result":
        raise ValueError(f"Nome de arquivo inválido: {nome_arquivo}")

    return estrutura, ordem, caso



def gerar_graficos(csv_path):
    estrutura, ordem, caso = extrair_info_nome_arquivo(csv_path)

    pasta_saida = os.path.join(OUTPUT_DIR, estrutura.lower())
    os.makedirs(pasta_saida, exist_ok=True)

    tempo_output = os.path.join(
        pasta_saida,
        f"{estrutura.lower()}_{ordem}_{caso}_tempo.png"
    )

    memoria_output = os.path.join(
        pasta_saida,
        f"{estrutura.lower()}_{ordem}_{caso}_memoria.png"
    )

    plot_time_csv(csv_path, tempo_output)
    plot_mem_csv(csv_path, memoria_output)

    print(f"[OK] {os.path.basename(csv_path)}")



def listar_csvs(nome_estrutura=None):
    arquivos = []

    if nome_estrutura:
        pasta_estrutura = os.path.join(INPUT_DIR, nome_estrutura.lower())

        if not os.path.exists(pasta_estrutura):
            raise FileNotFoundError(f"Diretório não encontrado: {pasta_estrutura}")

        for arquivo in os.listdir(pasta_estrutura):
            if arquivo.lower().endswith(".csv"):
                arquivos.append(os.path.join(pasta_estrutura, arquivo))

        return arquivos

    if not os.path.exists(INPUT_DIR):
        raise FileNotFoundError(f"Diretório não encontrado: {INPUT_DIR}")

    for pasta in os.listdir(INPUT_DIR):
        caminho_pasta = os.path.join(INPUT_DIR, pasta)

        if os.path.isdir(caminho_pasta):
            for arquivo in os.listdir(caminho_pasta):
                if arquivo.lower().endswith(".csv"):
                    arquivos.append(os.path.join(caminho_pasta, arquivo))

    return arquivos



def gerar_todos():
    for caminho in listar_csvs():
        try:
            gerar_graficos(caminho)
        except Exception as e:
            print(f"[ERRO] {os.path.basename(caminho)} -> {e}")



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
            gerar_graficos(caminho)
        except Exception as e:
            print(f"[ERRO] {os.path.basename(caminho)} -> {e}")



def main():
    parser = argparse.ArgumentParser(
        description="Gerador de gráficos para benchmarks"
    )

    parser.add_argument(
        "estrutura",
        nargs="?",
        help="Nome da estrutura para gerar todos os gráficos dela. Ex: arraylist, bst, avl"
    )

    args = parser.parse_args()

    if args.estrutura:
        gerar_por_estrutura(args.estrutura)
    else:
        gerar_todos()



if __name__ == "__main__":
    main()

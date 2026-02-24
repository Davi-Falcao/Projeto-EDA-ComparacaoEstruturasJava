import csv
import random
from pathlib import Path

OUT_DIR = Path("../../data/traceArrayList")

OPS = {
    "ADD_END": "ADD_END",
    "ADD_AT": "ADD_AT",
    "GET": "GET",
    "REMOVE_AT": "REMOVE_AT",
    "INDEX_OF": "INDEX_OF",
}


def write_row(w, op, a=None, b=None):
    """
    Serializa uma operação no formato CSV: op,a,b.

    Esse método não contém lógica de geração de carga,
    apenas padroniza o registro das operações que serão
    executadas posteriormente pelo ArrayList em Java.

    Impacto no experimento:
    - Garante separação entre geração de workload e execução.
    - Evita que custo de geração interfira no benchmark.
    - Permite replay determinístico do mesmo conjunto de operações.
    """
    w.writerow([op, "" if a is None else a, "" if b is None else b])


def gen_trace(filename: str, n_ops: int, scenario: str, seed: int = 42):
    """
    Gera um arquivo de trace com n_ops operações para estressar
    diferentes comportamentos do ArrayList implementado como vetor dinâmico.

    Cenários e impactos analisados:

    append:
        - Predominância de inserções no final.
        - Mede crescimento amortizado e custo de resize (cópia de vetor).
        - Remove no fim tende a O(1).
        - GET mede acesso direto por índice (O(1)).

    front_shift:
        - Inserções e remoções no índice 0.
        - Força shift máximo de elementos.
        - Representa pior caso clássico de vetor dinâmico.
        - Espera-se comportamento aproximadamente O(n²).

    middle_shift:
        - Inserções e remoções no meio.
        - Mede custo elevado de movimentação parcial do vetor.
        - Representa cenário realista de pior caso intermediário.

    search:
        - Fase inicial de preenchimento seguida de buscas por valor.
        - Mede custo linear de indexOf (O(n)).
        - Controla proporção de hits e misses.

    mixed:
        - Combinação de operações.
        - Simula uso mais próximo de aplicações reais.
        - Permite observar estabilidade e throughput médio.

    Observação metodológica:
    - Mantém tamanho lógico interno para evitar índices inválidos.
    - Seed fixa permite reprodutibilidade.
    """
    random.seed(seed)
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    path = OUT_DIR / filename

    size = 0
    next_value = 1

    with path.open("w", newline="", encoding="utf-8") as f:
        w = csv.writer(f)
        w.writerow(["op", "a", "b"])

        for _ in range(n_ops):
            if scenario == "append":
                r = random.random()
                if r < 0.85:
                    write_row(w, OPS["ADD_END"], next_value)
                    next_value += 1
                    size += 1
                elif r < 0.95:
                    idx = random.randrange(size) if size > 0 else 0
                    write_row(w, OPS["GET"], idx)
                else:
                    if size > 0:
                        write_row(w, OPS["REMOVE_AT"], size - 1)
                        size -= 1
                    else:
                        write_row(w, OPS["ADD_END"], next_value)
                        next_value += 1
                        size += 1

            elif scenario == "front_shift":
                r = random.random()
                if r < 0.60:
                    write_row(w, OPS["ADD_AT"], 0, next_value)
                    next_value += 1
                    size += 1
                else:
                    if size > 0:
                        write_row(w, OPS["REMOVE_AT"], 0)
                        size -= 1
                    else:
                        write_row(w, OPS["ADD_AT"], 0, next_value)
                        next_value += 1
                        size += 1

            elif scenario == "middle_shift":
                r = random.random()
                if r < 0.55:
                    mid = size // 2
                    write_row(w, OPS["ADD_AT"], mid, next_value)
                    next_value += 1
                    size += 1
                elif r < 0.90:
                    if size > 0:
                        mid = size // 2
                        write_row(w, OPS["REMOVE_AT"], mid)
                        size -= 1
                    else:
                        write_row(w, OPS["ADD_END"], next_value)
                        next_value += 1
                        size += 1
                else:
                    idx = random.randrange(size) if size > 0 else 0
                    write_row(w, OPS["GET"], idx)

            elif scenario == "search":
                if size < max(1, n_ops // 3):
                    write_row(w, OPS["ADD_END"], next_value)
                    next_value += 1
                    size += 1
                else:
                    if random.random() < 0.70 and size > 0:
                        val = random.randrange(1, next_value)
                    else:
                        val = next_value + random.randrange(1, max(2, size))
                    write_row(w, OPS["INDEX_OF"], val)

            elif scenario == "mixed":
                r = random.random()
                if r < 0.30:
                    write_row(w, OPS["ADD_END"], next_value)
                    next_value += 1
                    size += 1
                elif r < 0.40:
                    if size > 0:
                        write_row(w, OPS["REMOVE_AT"], size - 1)
                        size -= 1
                    else:
                        write_row(w, OPS["ADD_END"], next_value)
                        next_value += 1
                        size += 1
                elif r < 0.45:
                    mid = size // 2
                    write_row(w, OPS["ADD_AT"], mid, next_value)
                    next_value += 1
                    size += 1
                elif r < 0.50:
                    if size > 0:
                        mid = size // 2
                        write_row(w, OPS["REMOVE_AT"], mid)
                        size -= 1
                    else:
                        write_row(w, OPS["ADD_END"], next_value)
                        next_value += 1
                        size += 1
                else:
                    idx = random.randrange(size) if size > 0 else 0
                    write_row(w, OPS["GET"], idx)
            else:
                raise ValueError("scenario inválido")

    print(f"[OK] {path} | ops={n_ops} | scenario={scenario} | seed={seed}")


if __name__ == "__main__":
    """
    Configuração experimental automatizada.

    Ajustes aplicados:
    - Múltiplas escalas de tamanho (crescimento controlado).
    - Múltiplas seeds por cenário (redução de ruído).
    - Nomeação padronizada para facilitar análise automática.

    Estratégia experimental:
    - 10k e 100k → validação funcional.
    - 300k+      → início da análise de crescimento.
    - 1M+        → observação clara de comportamento assintótico.

    executando o mesmo trace algumas vezes antes da medição.
    """

    SCENARIOS = ["append", "front_shift", "middle_shift", "search", "mixed"]
    SIZES = [10_000, 100_000, 300_000, 1_000_000]
    SEEDS = [1, 2, 3]

    for scenario in SCENARIOS:
        for n_ops in SIZES:
            for seed in SEEDS:
                filename = f"{scenario}_{n_ops}_seed{seed}.csv"
                gen_trace(filename, n_ops, scenario, seed)
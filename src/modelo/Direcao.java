package modelo;

/**
 * Direção de expansão de um navio a partir da sua célula inicial.
 *
 * Lido do arquivo como:
 *   d = down  (expande para baixo,    linha cresce)
 *   u = up    (expande para cima,     linha decresce)
 *   r = right (expande para direita,  coluna cresce)
 *   l = left  (expande para esquerda, coluna decresce)
 */
public enum Direcao {

    DOWN  ("d"),
    UP    ("u"),
    RIGHT ("r"),
    LEFT  ("l");

    private final String codigo;

    Direcao(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Converte o caractere lido do arquivo no enum correspondente.
     * @throws IllegalArgumentException se o código for desconhecido
     */
    public static Direcao fromCodigo(String codigo) {
        for (Direcao d : values()) {
            if (d.codigo.equalsIgnoreCase(codigo)) return d;
        }
        throw new IllegalArgumentException("Direção desconhecida: '" + codigo + "'. Use d, u, r ou l.");
    }

    /** Variação de linha por célula expandida (+1, -1 ou 0). */
    public int deltaLinha() {
        return switch (this) {
            case DOWN  ->  1;
            case UP    -> -1;
            default    ->  0;
        };
    }

    /** Variação de coluna por célula expandida (+1, -1 ou 0). */
    public int deltaColuna() {
        return switch (this) {
            case RIGHT ->  1;
            case LEFT  -> -1;
            default    ->  0;
        };
    }
}

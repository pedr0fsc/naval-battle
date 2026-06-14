package modelo;

public enum TipoNavio {
    FRAGATA(1),
    SUBMARINO(2),
    CRUZADOR(3),
    ENCOURACADO(4),
    PORTA_AVIOES(5);

    private final int tamanho;

    TipoNavio(int tamanho) {
        this.tamanho = tamanho;
    }

    public int getTamanho() {
        return tamanho;
    }

    public static TipoNavio fromString(String nome) {
        return TipoNavio.valueOf(nome.toUpperCase());
    }
}

package excecoes;

/**
 * Lançada quando um jogador tenta atacar uma posição que já foi atacada anteriormente,
 * seja ela um navio ou água. Contém informações sobre a posição que causou a exceção para facilitar o diagnóstico e feedback ao usuário.
 */

public class PosicaoJaAtacadaException extends Exception {
    private final int linha;
    private final int coluna;

    public PosicaoJaAtacadaException(String mensagem, int linha, int coluna) {
        super(mensagem + " Posição tentada: [" + (linha + 1) + "," + ((char)('a' + coluna)) + "]");
        this.linha = linha;
        this.coluna = coluna;
    }

    public int getLinha() { return linha; }
    public int getColuna() { return coluna; }
}
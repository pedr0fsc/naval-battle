package excecoes;

/**
 * Lançada quando o arquivo de configuração contém dados
 * inválidos ou em formato inesperado.
 */
public class ConfiguracaoInvalidaException extends Exception {

    private final int numeroLinha;

    public ConfiguracaoInvalidaException(String mensagem, int numeroLinha) {
        super("Linha " + numeroLinha + ": " + mensagem);
        this.numeroLinha = numeroLinha;
    }

    public ConfiguracaoInvalidaException(String mensagem) {
        super(mensagem);
        this.numeroLinha = -1;
    }

    public int getNumeroLinha() {
        return numeroLinha;
    }
}

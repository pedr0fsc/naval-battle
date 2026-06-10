package excecoes;

/**
 * Lançada quando um navio é posicionado fora dos limites
 * do tabuleiro ou sobre outro navio já existente.
 */
public class PosicionamentoInvalidoException extends Exception {

    private final String nomeJogador;
    private final String posicao;

    public PosicionamentoInvalidoException(String mensagem, String nomeJogador, String posicao) {
        super("Jogador '" + nomeJogador + "', navio em '" + posicao + "': " + mensagem);
        this.nomeJogador = nomeJogador;
        this.posicao = posicao;
    }

    public String getNomeJogador() { return nomeJogador; }
    public String getPosicao()     { return posicao; }
}

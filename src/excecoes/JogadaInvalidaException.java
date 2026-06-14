package excecoes;

/**
 * Lançada quando um jogador tenta realizar uma jogada inválida, como atacar fora dos limites do tabuleiro ou fornecer coordenadas em formato incorreto.
 */

public class JogadaInvalidaException extends Exception {
    public JogadaInvalidaException(String mensagem) {
        super(mensagem);
    }
}
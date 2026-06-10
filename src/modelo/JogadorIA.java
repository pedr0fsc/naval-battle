package modelo;

/**
 * Jogador controlado por inteligência artificial.
 * No P2, o motor do jogo gera os ataques automaticamente para este tipo.
 */
public class JogadorIA extends Jogador {

    private static final long serialVersionUID = 1L;

    public JogadorIA(String nome) {
        super(nome);
    }

    @Override
    public String getTipo() { return "IA"; }
}

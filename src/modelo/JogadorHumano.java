package modelo;

/**
 * Jogador controlado por humano.
 * No P2, a interface gráfica aguarda input do usuário para este tipo.
 */
public class JogadorHumano extends Jogador {

    private static final long serialVersionUID = 1L;

    public JogadorHumano(String nome) {
        super(nome);
    }

    @Override
    public String getTipo() { return "HUMANO"; }
}

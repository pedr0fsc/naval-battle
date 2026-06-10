package modelo;

/**
 * Navio comum: afunda quando todas as suas seções são atingidas.
 * É o tipo padrão criado a partir da leitura do arquivo.
 *
 * O projeto pode evoluir com subclasses especializadas (ex: NavioEspecial
 * com regras distintas), mantendo o polimorfismo via referência Navio.
 */
public class NavioComum extends Navio {

    private static final long serialVersionUID = 1L;

    public NavioComum(String posicaoInicial, Direcao direcao, int tamanho) {
        super(posicaoInicial, direcao, tamanho);
    }

    /**
     * Afunda quando TODAS as seções foram atingidas.
     * Implementação do método abstrato de Navio.
     */
    @Override
    public boolean eAfundado() {
        return getSeccoesAtingidas().stream().allMatch(s -> s);
    }
}

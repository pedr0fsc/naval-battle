package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstrata que representa um jogador da Batalha Naval.
 *
 * Subclasses: JogadorHumano e JogadorIA.
 * O método getTipo() é abstrato — cada subclasse retorna seu identificador.
 *
 * A chamada polimórfica central do projeto ocorre em todosNaviosAfundados():
 * chama navio.eAfundado() via referência do tipo abstrato Navio, sem
 * conhecer o tipo concreto de cada navio.
 */
public abstract class Jogador implements Serializable, Persistivel {

    private static final long serialVersionUID = 1L;

    private final String nome;
    private final Tabuleiro tabuleiro;
    private final List<Navio> navios;

    public Jogador(String nome) {
        this.nome      = nome;
        this.tabuleiro = new Tabuleiro();
        this.navios    = new ArrayList<>();
    }

    // ----------------------------------------------------------------
    // Método abstrato
    // ----------------------------------------------------------------

    /** Retorna o tipo do jogador ("HUMANO" ou "IA"). */
    public abstract String getTipo();

    // ----------------------------------------------------------------
    // Métodos concretos
    // ----------------------------------------------------------------

    public void adicionarNavio(Navio navio) {
        navios.add(navio);
        for (int[] celula : navio.getCelulas()) {
            tabuleiro.marcarNavio(celula[0], celula[1]);
        }
    }

    /**
     * Verifica se todos os navios do jogador foram afundados.
     * CHAMADA POLIMÓRFICA: navio.eAfundado() via referência Navio.
     */
    public boolean todosNaviosAfundados() {
        return navios.stream().allMatch(Navio::eAfundado);
    }

    @Override
    public String gerarDescricao() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Jogador: %s (%s) | %d navio(s)%n", nome, getTipo(), navios.size()));
        for (Navio n : navios) sb.append(n.gerarDescricao()).append('\n');
        sb.append('\n');
        sb.append(tabuleiro.gerarDescricao());
        return sb.toString();
    }

    // ----------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------

    public String getNome()         { return nome; }
    public Tabuleiro getTabuleiro() { return tabuleiro; }
    public List<Navio> getNavios()  { return new ArrayList<>(navios); }
}

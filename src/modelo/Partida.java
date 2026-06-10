package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Objeto raiz que encapsula toda a configuração inicial da partida.
 * É este objeto que o P1 serializa e o P2 desserializa para iniciar o jogo.
 */
public class Partida implements Serializable, Persistivel {

    private static final long serialVersionUID = 1L;

    private final List<Jogador> jogadores;
    private final int quantidadeNaviosPorJogador;

    public Partida(int quantidadeNaviosPorJogador) {
        this.quantidadeNaviosPorJogador = quantidadeNaviosPorJogador;
        this.jogadores = new ArrayList<>();
    }

    public void adicionarJogador(Jogador jogador) {
        jogadores.add(jogador);
    }

    public List<Jogador> getJogadores()              { return new ArrayList<>(jogadores); }
    public int getQuantidadeNaviosPorJogador()        { return quantidadeNaviosPorJogador; }

    @Override
    public String gerarDescricao() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════╗\n");
        sb.append("  CONFIGURAÇÃO INICIAL — BATALHA NAVAL  \n");
        sb.append("╚══════════════════════════════════════╝\n");
        sb.append(String.format("Navios por jogador: %d%n%n", quantidadeNaviosPorJogador));
        for (Jogador j : jogadores) {
            sb.append("──────────────────────────────────────\n");
            sb.append(j.gerarDescricao()).append('\n');
        }
        return sb.toString();
    }
}

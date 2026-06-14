package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstrata que representa um navio no tabuleiro.
 *
 * Cada navio é definido por:
 *   - uma posição inicial (coluna letra + linha número, ex: "a5")
 *   - uma direção de expansão (d, u, r, l)
 *   - um tamanho (número de células que ocupa)
 *
 * Subclasses concretas definem tipos específicos de navios e podem
 * sobrescrever eAfundado() para regras especiais.
 *
 * A chamada polimórfica ocorre em Jogador.todosNaviosAfundados(),
 * que chama navio.eAfundado() via referência do tipo abstrato Navio.
 */
public abstract class Navio implements Serializable, Persistivel {

    private static final long serialVersionUID = 1L;

    private final TipoNavio tipo;
    // Posição inicial lida do arquivo (ex: "a5")
    private final String posicaoInicial;
    // Direção de expansão lida do arquivo
    private final Direcao direcao;
    // Tamanho do navio em células
    private final int tamanho;
    // Cada índice representa uma seção; true = foi atingida
    private final List<Boolean> seccoesAtingidas;
    // Células ocupadas no tabuleiro [linha][coluna] após posicionamento
    private final List<int[]> celulas;

    public Navio(TipoNavio tipo, String posicaoInicial, Direcao direcao, int tamanho) {
        this.tipo             = tipo;
        this.posicaoInicial   = posicaoInicial;
        this.direcao          = direcao;
        this.tamanho          = tamanho;
        this.seccoesAtingidas = new ArrayList<>();
        this.celulas          = new ArrayList<>();
        for (int i = 0; i < tamanho; i++) seccoesAtingidas.add(false);
    }
    
    public TipoNavio getTipo() { return tipo; }

    // ----------------------------------------------------------------
    // Método abstrato — chamada polimórfica via referência Navio
    // ----------------------------------------------------------------

    /**
     * Verifica se o navio foi completamente afundado.
     * Subclasses podem sobrescrever para regras especiais.
     */
    public abstract boolean eAfundado();

    // ----------------------------------------------------------------
    // Métodos concretos
    // ----------------------------------------------------------------

    /**
     * Calcula e armazena as células ocupadas a partir da posição
     * e direção informadas, usando o tabuleiro para converter
     * a notação "a5" em índices [linha][coluna].
     *
     * @param linhaInicial  índice de linha (0-based) da posição inicial
     * @param colunaInicial índice de coluna (0-based) da posição inicial
     */
    public void calcularCelulas(int linhaInicial, int colunaInicial) {
        celulas.clear();
        for (int i = 0; i < tamanho; i++) {
            int l = linhaInicial + i * direcao.deltaLinha();
            int c = colunaInicial + i * direcao.deltaColuna();
            celulas.add(new int[]{l, c});
        }
    }

    /**
     * Registra um hit na seção do índice informado.
     * @return true se o navio foi afundado com este hit
     */
    public boolean receberAtaque(int seccao) {
        if (seccao >= 0 && seccao < tamanho) {
            seccoesAtingidas.set(seccao, true);
        }
        return eAfundado();
    }

    @Override
    public String gerarDescricao() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  Navio [%s] pos=%s dir=%s tam=%d afundado=%s celulas=",
                getClass().getSimpleName(),
                posicaoInicial,
                direcao.name(),
                tamanho,
                eAfundado() ? "SIM" : "NAO"));
        for (int[] c : celulas) sb.append(String.format("(%d,%d)", c[0], c[1]));
        return sb.toString();
    }

    // ----------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------

    public String getPosicaoInicial()       { return posicaoInicial; }
    public Direcao getDirecao()             { return direcao; }
    public int getTamanho()                 { return tamanho; }
    public List<int[]> getCelulas()         { return new ArrayList<>(celulas); }
    public List<Boolean> getSeccoesAtingidas() { return new ArrayList<>(seccoesAtingidas); }
}

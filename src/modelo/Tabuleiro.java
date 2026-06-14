package modelo;

import java.io.Serializable;

/**
 * Representa o tabuleiro de um jogador (grade 10x10 padrão,
 * mas adaptável ao tamanho definido pelo número de colunas-letra).
 *
 * Notação do arquivo: coluna = letra (a=0, b=1, ...) / linha = número (1=0, 2=1, ...)
 *
 * Conversão:
 *   coluna: 'a' → 0, 'b' → 1, ..., 'j' → 9
 *   linha:  '1' → 0, '2' → 1, ..., '10'→ 9
 */
public class Tabuleiro implements Serializable, Persistivel {

    private static final long serialVersionUID = 1L;

    public enum Celula {
        VAZIA('.'), NAVIO('N'), AGUA('~'), ACERTO('X');
        final char simbolo;
        Celula(char s) { this.simbolo = s; }

        public boolean podeRevelar() {
            return this != NAVIO;
        }
    }

    private static final int TAMANHO = 10;

    private final Celula[][] grade;

    public Tabuleiro() {
        this.grade = new Celula[TAMANHO][TAMANHO];
        for (int i = 0; i < TAMANHO; i++)
            for (int j = 0; j < TAMANHO; j++)
                grade[i][j] = Celula.VAZIA;
    }

    // ----------------------------------------------------------------
    // Conversão de notação "a5" → índices [linha][coluna]
    // ----------------------------------------------------------------

    /**
     * Converte a letra da coluna em índice (a=0, b=1, ...).
     */
    public static int letraParaColuna(char letra) {
        return Character.toLowerCase(letra) - 'a';
    }

    /**
     * Converte o número da linha (como string) em índice 0-based.
     * Ex: "5" → 4
     */
    public static int numeroParaLinha(String numero) {
        return Integer.parseInt(numero) - 1;
    }

    /**
     * Interpreta uma posição no formato "a5":
     *   [0] = índice de linha (0-based)
     *   [1] = índice de coluna (0-based)
     */
    public static int[] parsePosicao(String posicao) {
        char colLetra = posicao.charAt(0);
        String linhaStr = posicao.substring(1);
        int coluna = letraParaColuna(colLetra);
        int linha  = numeroParaLinha(linhaStr);
        return new int[]{linha, coluna};
    }

    // ----------------------------------------------------------------
    // Operações sobre o tabuleiro
    // ----------------------------------------------------------------

    public void marcarNavio(int linha, int coluna) {
        grade[linha][coluna] = Celula.NAVIO;
    }

    public boolean dentroDosLimites(int linha, int coluna) {
        return linha >= 0 && linha < TAMANHO && coluna >= 0 && coluna < TAMANHO;
    }

    public boolean celulaLivre(int linha, int coluna) {
        return grade[linha][coluna] == Celula.VAZIA;
    }

    public boolean foiAtacado(int linha, int coluna) {
        return grade[linha][coluna] == Celula.AGUA || grade[linha][coluna] == Celula.ACERTO;
    }

    public Celula getCelula(int linha, int coluna) {
        return grade[linha][coluna];
    }

    public void setCelula(int linha, int coluna, Celula celula) {
        grade[linha][coluna] = celula;
    }

    public static int getTamanho() { return TAMANHO; }

    // ----------------------------------------------------------------
    // Representação visual para log/console
    // ----------------------------------------------------------------

    @Override
    public String gerarDescricao() {
        StringBuilder sb = new StringBuilder();
        sb.append("    a b c d e f g h i j\n");
        for (int i = 0; i < TAMANHO; i++) {
            sb.append(String.format("%2d  ", i + 1));
            for (int j = 0; j < TAMANHO; j++) {
                sb.append(grade[i][j].simbolo).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}

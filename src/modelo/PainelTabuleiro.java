package modelo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Componente visual que encapsula a representação gráfica de um Tabuleiro.
 */
public class PainelTabuleiro extends JPanel {
    private final JButton[][] botoes = new JButton[10][10];

    public PainelTabuleiro(String titulo, boolean atacavel, ActionListener listener) {
        setLayout(new GridLayout(11, 11));
        setBorder(BorderFactory.createTitledBorder(titulo));
        
        add(new JLabel("")); // Canto
        for (int i = 0; i < 10; i++) add(new JLabel(String.valueOf((char)('a' + i)), SwingConstants.CENTER));

        for (int i = 0; i < 10; i++) {
            add(new JLabel(String.valueOf(i + 1), SwingConstants.CENTER));
            for (int j = 0; j < 10; j++) {
                botoes[i][j] = new JButton();
                botoes[i][j].setPreferredSize(new Dimension(40, 40));
                
                if (atacavel) {
                    botoes[i][j].addActionListener(listener);
                    botoes[i][j].putClientProperty("linha", i);
                    botoes[i][j].putClientProperty("coluna", j);
                } else {
                    botoes[i][j].setEnabled(false);
                    botoes[i][j].setFocusable(false);
                }
                add(botoes[i][j]);
            }
        }
    }

    public void atualizar(Tabuleiro tabuleiro, boolean revelarNavios, boolean habilitado) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                JButton b = botoes[i][j];
                Tabuleiro.Celula celula = tabuleiro.getCelula(i, j);
                
                if (celula == Tabuleiro.Celula.AGUA) b.setBackground(Color.BLUE);
                else if (celula == Tabuleiro.Celula.ACERTO) b.setBackground(Color.RED);
                else if (revelarNavios && celula == Tabuleiro.Celula.NAVIO) b.setBackground(Color.GRAY);
                else b.setBackground(Color.WHITE);
                
                if (b.getActionListeners().length > 0) {
                    b.setEnabled(habilitado);
                }
            }
        }
    }
}

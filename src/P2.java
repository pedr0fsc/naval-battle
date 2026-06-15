import excecoes.JogadaInvalidaException;
import excecoes.PosicaoJaAtacadaException;
import logica.ControleJogo;
import modelo.*;
import persistencia.ResultadoFinal;
import persistencia.SerializadorPartida;

import javax.swing.*;
import java.awt.*;
import java.io.File;


public class P2 {

    private static final String ARQUIVO_BIN_PADRAO = "dados" + File.separator + "partida.bin";

    public static void main(String[] args) {
        String arquivoBin = args.length > 0 ? args[0] : ARQUIVO_BIN_PADRAO;

        try {
            System.out.println("Restaurando partida de: " + arquivoBin);
            SerializadorPartida serializador = new SerializadorPartida(arquivoBin);
            Partida partida = serializador.carregar();
            
            SwingUtilities.invokeLater(() -> new JanelaJogo(partida));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar partida: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}

class JanelaJogo extends JFrame {
    private final ControleJogo controle;
    private final PainelTabuleiro panelDefesa;
    private final PainelTabuleiro panelAtaque;

    public JanelaJogo(Partida partida) {
        this.controle = new ControleJogo(partida);
        setTitle("Batalha Naval");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2));

        panelDefesa = new PainelTabuleiro("Defesa", false, null);
        panelAtaque = new PainelTabuleiro("Ataque", true, e -> {
            JButton b = (JButton) e.getSource();
            realizarAtaque((int)b.getClientProperty("linha"), (int)b.getClientProperty("coluna"));
        });

        add(panelDefesa);
        add(panelAtaque);

        atualizarVisualizacao();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void realizarAtaque(int linha, int coluna) {
        if (controle.getJogadorAtual() instanceof modelo.JogadorIA) return;

        try {
            String resultado = controle.registrarAtaque(linha, coluna);
            System.out.println(resultado);
            atualizarVisualizacao();

            if (controle.verificarFimDeJogo()) {
                Jogador vencedor = controle.getJogador1().todosNaviosAfundados() ? controle.getJogador2()
                        : controle.getJogador1();
                Jogador perdedor = vencedor == controle.getJogador1() ? controle.getJogador2()
                        : controle.getJogador1();
                ResultadoFinal.salvar(vencedor, perdedor);
                finalizarJogo();

            } else if (controle.getJogadorAtual() instanceof modelo.JogadorIA) {
                javax.swing.Timer timer = new javax.swing.Timer(500, e -> jogarIA());
                timer.setRepeats(false);
                timer.start();
            }

        } catch (excecoes.JogadaInvalidaException | excecoes.PosicaoJaAtacadaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Jogada Inválida", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void jogarIA() {
        try {
            String resultado = controle.executarJogadaIA();
            System.out.println(resultado);
            atualizarVisualizacao();
            
            if (controle.verificarFimDeJogo()) {
                Jogador vencedor = controle.getJogador1().todosNaviosAfundados() ? controle.getJogador2()
                        : controle.getJogador1();
                Jogador perdedor = vencedor == controle.getJogador1() ? controle.getJogador2()
                        : controle.getJogador1();
                ResultadoFinal.salvar(vencedor, perdedor);
                finalizarJogo();

            } else if (controle.getJogadorAtual() instanceof modelo.JogadorIA) {
                javax.swing.Timer timer = new javax.swing.Timer(500, e -> jogarIA());
                timer.setRepeats(false);
                timer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void finalizarJogo() {
        JOptionPane.showMessageDialog(this, "Fim de Jogo! O vencedor é: " + controle.getJogadorAtual().getNome());
        dispose();
    }

    private void atualizarVisualizacao() {
        Jogador humano = controle.getJogador1();
        Jogador ia = controle.getJogador2();
        boolean isTurnoHumano = (controle.getJogadorAtual() == humano);

        panelDefesa.atualizar(humano.getTabuleiro(), true, false);
        panelAtaque.atualizar(ia.getTabuleiro(), true, isTurnoHumano);
    }
}

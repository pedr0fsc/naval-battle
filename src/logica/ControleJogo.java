package logica;

import excecoes.JogadaInvalidaException;
import excecoes.PosicaoJaAtacadaException;
import modelo.Jogador;
import modelo.Navio;
import modelo.Partida;
import modelo.Tabuleiro;
import modelo.Tabuleiro.Celula;
import persistencia.LoggerPartida;

import java.util.List;
import java.util.Random;

public class ControleJogo {

    private final Partida partida;
    private final Jogador jogador1;
    private final Jogador jogador2;
    private Jogador jogadorAtual;
    private final Random random;
    private final List<int[]> alvosIA = new java.util.ArrayList<>();


    public ControleJogo(Partida partida) {
        this.partida = partida;
        List<Jogador> jogadores = partida.getJogadores();
        this.jogador1 = jogadores.get(0);
        this.jogador2 = jogadores.get(1);
        this.jogadorAtual = jogador1; 
        this.random = new Random();
    }

    
    public String registrarAtaque(int linha, int coluna) 
            throws JogadaInvalidaException, PosicaoJaAtacadaException {

        if (linha < 0 || linha >= 10 || coluna < 0 || coluna >= 10) {
            throw new JogadaInvalidaException("Coordenada fora dos limites do tabuleiro 10x10.");
        }

        Jogador oponente = (jogadorAtual == jogador1) ? jogador2 : jogador1;

        Tabuleiro tabuleiroOponente = oponente.getTabuleiro();
        Celula estadoAtual = tabuleiroOponente.getCelula(linha, coluna);

        if (estadoAtual == Celula.AGUA || estadoAtual == Celula.ACERTO) {
            throw new PosicaoJaAtacadaException("Esta posição já foi atacada anteriormente!", linha, coluna);
        }

        StringBuilder sb = new StringBuilder();

        LoggerPartida.registrar(jogadorAtual.getNome() + " atirou em " + (char)('A' + coluna) + (linha + 1)
        );


        if (estadoAtual == Celula.NAVIO) {
            tabuleiroOponente.setCelula(linha, coluna, Celula.ACERTO);

            if (jogadorAtual == jogador2) {
                adicionarAlvosVizinhos(linha, coluna);
            }
            String afundouMsg = atualizarSeccaoNavio(oponente, linha, coluna);
            
            sb.append(String.format("ACERTOU! %s bombardeou [%d, %c] do oponente.", 
                    jogadorAtual.getNome(), (linha + 1), (char)('a' + coluna)));
            LoggerPartida.registrar("Acerto");

            if (afundouMsg != null) {
                sb.append("\n").append(afundouMsg);
                LoggerPartida.registrar(afundouMsg);
            }


        } else {
            tabuleiroOponente.setCelula(linha, coluna, Celula.AGUA);
            sb.append(String.format("ÁGUA! %s disparou em [%d, %c].", 
                    jogadorAtual.getNome(), (linha + 1), (char)('a' + coluna)));
            LoggerPartida.registrar("Água");
            alternarTurno(); 
        }
        return sb.toString();
    }


    public String executarJogadaIA() throws JogadaInvalidaException, PosicaoJaAtacadaException {
        Jogador oponente = (jogadorAtual == jogador1) ? jogador2 : jogador1;
        Tabuleiro tabOponente = oponente.getTabuleiro();

        // Modo de caça da COMPUTADOR
        while (!alvosIA.isEmpty()) {
            int[] alvo = alvosIA.remove(0);
            int linha = alvo[0];
            int coluna = alvo[1];
            if (linha < 0 || linha >= 10 || coluna < 0 || coluna >= 10) {continue;}
            Celula estado = tabOponente.getCelula(linha, coluna);
            if (estado != Celula.AGUA && estado != Celula.ACERTO) {
                return registrarAtaque(linha, coluna);
            }
        }

       // Modo aleatório
        int linhaAleatoria;
        int colunaAleatoria;

        do {linhaAleatoria = random.nextInt(10);
            colunaAleatoria = random.nextInt(10);
        } while (tabOponente.getCelula(linhaAleatoria, colunaAleatoria) ==
                Celula.AGUA || tabOponente.getCelula(linhaAleatoria, colunaAleatoria) == Celula.ACERTO
        );
        return registrarAtaque(linhaAleatoria, colunaAleatoria);

    }
    // Quando o computador acerta, ele guarda alvo adjacentes
    private void adicionarAlvosVizinhos(int linha, int coluna) {

        int[][] direcoes = {
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
        };
        for (int[] d : direcoes) {
            int novaLinha = linha + d[0];
            int novaColuna = coluna + d[1];
            if (novaLinha >= 0 && novaLinha < 10 && novaColuna >= 0 && novaColuna < 10) {
                alvosIA.add(new int[]{novaLinha, novaColuna});
            }
        }
    }

    private String atualizarSeccaoNavio(Jogador oponente, int linhaAlvo, int colunaAlvo) {
        for (Navio navio : oponente.getNavios()) {
            List<int[]> celulas = navio.getCelulas();
            for (int i = 0; i < celulas.size(); i++) {
                int[] c = celulas.get(i);
                if (c[0] == linhaAlvo && c[1] == colunaAlvo) {
                    if (navio.receberAtaque(i)) {
                        return String.format(">>> AFUNDOU! O %s de %s foi destruído!", 
                                navio.getTipo(), oponente.getNome());
                    }
                    return null;
                }
            }
        }
        return null;
    }

    public void alternarTurno() {
        jogadorAtual = (jogadorAtual == jogador1) ? jogador2 : jogador1;
    }

    public boolean verificarFimDeJogo() {
        return jogador1.todosNaviosAfundados() || jogador2.todosNaviosAfundados();
    }

    public Jogador getJogadorAtual() { return jogadorAtual; }
    public Jogador getJogador1()     { return jogador1; }
    public Jogador getJogador2()     { return jogador2; }

    public Partida getPartida() {
    return partida;
}
}
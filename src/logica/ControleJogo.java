package logica;

import excecoes.JogadaInvalidaException;
import excecoes.PosicaoJaAtacadaException;
import modelo.Jogador;
import modelo.Direcao;
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

    private int linhaInicial = -1;
    private int colunaInicial = -1;
    private int linhaAtual = -1;
    private int colunaAtual = -1;
    private Direcao direcaoAtual = null;
    private int tentativaDirecao = 0;

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
        LoggerPartida.registrar(jogadorAtual.getNome() + " atirou em " + (char)('A' + coluna) + (linha + 1));

        if (estadoAtual == Celula.NAVIO) {
            tabuleiroOponente.setCelula(linha, coluna, Celula.ACERTO);

            if (jogadorAtual == jogador2) {
                computadorAcertou(linha, coluna);
            }

            String afundouMsg = atualizarSeccaoNavio(oponente, linha, coluna);

            sb.append(String.format("ACERTOU! %s bombardeou [%d, %c] do oponente.",
                    jogadorAtual.getNome(), (linha + 1), (char)('a' + coluna)));
            LoggerPartida.registrar("Acerto");

            if (afundouMsg != null) {
                sb.append("\n").append(afundouMsg);
                LoggerPartida.registrar(afundouMsg);

                if (jogadorAtual == jogador2) {
                    resetarMemoriaIA();
                }
            }

        } else {
            tabuleiroOponente.setCelula(linha, coluna, Celula.AGUA);

            if (jogadorAtual == jogador2 && linhaInicial != -1) {
                computadorErrou();
            }

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

        if (linhaInicial != -1) {
            int[] proximoAlvo = obterProximoAlvo(tabOponente);
            if (proximoAlvo != null) {
                return registrarAtaque(proximoAlvo[0], proximoAlvo[1]);
            }
        }

        int linhaAleatoria, colunaAleatoria;
        do {
            linhaAleatoria = random.nextInt(10);
            colunaAleatoria = random.nextInt(10);
        } while (tabOponente.getCelula(linhaAleatoria, colunaAleatoria) == Celula.AGUA ||
                tabOponente.getCelula(linhaAleatoria, colunaAleatoria) == Celula.ACERTO);

        return registrarAtaque(linhaAleatoria, colunaAleatoria);
    }

    private void computadorAcertou(int linha, int coluna) {
        if (linhaInicial == -1) {
            linhaInicial = linha;
            colunaInicial = coluna;
        } else if (direcaoAtual == null) {
            if (linha > linhaInicial) direcaoAtual = Direcao.DOWN;
            else if (linha < linhaInicial) direcaoAtual = Direcao.UP;
            else if (coluna > colunaInicial) direcaoAtual = Direcao.RIGHT;
            else if (coluna < colunaInicial) direcaoAtual = Direcao.LEFT;
        }
        linhaAtual = linha;
        colunaAtual = coluna;
    }

    private void computadorErrou() {
        if (direcaoAtual != null) {
            switch (direcaoAtual) {
                case DOWN: direcaoAtual = Direcao.UP; break;
                case UP: direcaoAtual = Direcao.DOWN; break;
                case RIGHT: direcaoAtual = Direcao.LEFT; break;
                case LEFT: direcaoAtual = Direcao.RIGHT; break;
            }
            linhaAtual = linhaInicial;
            colunaAtual = colunaInicial;
        } else {
            tentativaDirecao++;
        }
    }

    private int[] obterProximoAlvo(Tabuleiro tab) {
        if (direcaoAtual != null) {
            int[] alvo = deslocar(linhaAtual, colunaAtual, direcaoAtual);
            if (coordenadaValida(alvo[0], alvo[1]) &&
                    tab.getCelula(alvo[0], alvo[1]) != Celula.AGUA &&
                    tab.getCelula(alvo[0], alvo[1]) != Celula.ACERTO) {
                return alvo;
            }
            computadorErrou();
            alvo = deslocar(linhaAtual, colunaAtual, direcaoAtual);
            if (coordenadaValida(alvo[0], alvo[1]) &&
                    tab.getCelula(alvo[0], alvo[1]) != Celula.AGUA &&
                    tab.getCelula(alvo[0], alvo[1]) != Celula.ACERTO) {
                return alvo;
            }
        } else {
            Direcao[] direcoes = Direcao.values();
            while (tentativaDirecao < direcoes.length) {
                Direcao direcaoParaTestar = direcoes[tentativaDirecao];
                int[] alvo = deslocar(linhaInicial, colunaInicial, direcaoParaTestar);
                if (coordenadaValida(alvo[0], alvo[1]) &&
                        tab.getCelula(alvo[0], alvo[1]) != Celula.AGUA &&
                        tab.getCelula(alvo[0], alvo[1]) != Celula.ACERTO) {
                    return alvo;
                }
                tentativaDirecao++;
            }
        }
        resetarMemoriaIA();
        return null;
    }

    private int[] deslocar(int linha, int coluna, Direcao direcao) {
        switch (direcao) {
            case DOWN: return new int[]{linha + 1, coluna};
            case UP: return new int[]{linha - 1, coluna};
            case RIGHT: return new int[]{linha, coluna + 1};
            case LEFT: return new int[]{linha, coluna - 1};
            default: return new int[]{linha, coluna};
        }
    }

    private boolean coordenadaValida(int linha, int coluna) {
        return linha >= 0 && linha < 10 && coluna >= 0 && coluna < 10;
    }

    private void resetarMemoriaIA() {
        linhaInicial = -1;
        colunaInicial = -1;
        linhaAtual = -1;
        colunaAtual = -1;
        direcaoAtual = null;
        tentativaDirecao = 0;
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
    public Partida getPartida()      { return partida; }
}
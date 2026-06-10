package persistencia;

import excecoes.ConfiguracaoInvalidaException;
import excecoes.PosicionamentoInvalidoException;
import modelo.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lê o arquivo de configuração no formato definido pelo professor:
 *
 *   5              ← quantidade de navios por jogador
 *                  ← linha em branco
 *   Pedro          ← nome do jogador 1
 *   a5 d           ← posição direção  (repete N vezes)
 *   b3 l
 *   ...
 *                  ← linha em branco
 *   Sophia         ← nome do jogador 2
 *   b5 d
 *   ...
 *
 * Formato de posição: letra + número  (ex: "a5" = coluna a, linha 5)
 * Formato de direção: d=baixo, u=cima, r=direita, l=esquerda
 * O tamanho de cada navio é inferido pela sequência (navio 1 = tam 1, navio 2 = tam 2, etc.)
 * pois o arquivo não explicita tamanhos individuais.
 *
 * REPASSA exceções ao chamador — não usa try-catch internamente (requisito RA2).
 */
public class LeitorConfiguracao {

    private final String caminhoArquivo;

    public LeitorConfiguracao(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    /**
     * Ponto de entrada: lê o arquivo e devolve a Partida pronta.
     *
     * Repassa ConfiguracaoInvalidaException, PosicionamentoInvalidoException
     * e IOException ao chamador sem capturá-las (requisito RA2: throws sem try-catch).
     */
    public Partida lerConfiguracao()
            throws ConfiguracaoInvalidaException, PosicionamentoInvalidoException, IOException {

        List<String> linhas = lerLinhasDoArquivo();
        return construirPartida(linhas);
    }

    // ----------------------------------------------------------------
    // Leitura bruta das linhas — repassa IOException (requisito RA2)
    // ----------------------------------------------------------------

    private List<String> lerLinhasDoArquivo() throws IOException {
        List<String> resultado = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                resultado.add(linha.trim());
            }
        }
        return resultado;
    }

    // ----------------------------------------------------------------
    // Construção da Partida a partir das linhas
    // ----------------------------------------------------------------

    private Partida construirPartida(List<String> linhas)
            throws ConfiguracaoInvalidaException, PosicionamentoInvalidoException {

        if (linhas.isEmpty())
            throw new ConfiguracaoInvalidaException("Arquivo de configuração está vazio.");

        // Linha 1: quantidade de navios
        int quantNavios = parseQuantidade(linhas.get(0), 1);
        Partida partida = new Partida(quantNavios);

        // Percorre o restante das linhas agrupando blocos por jogador
        int i = 1;
        while (i < linhas.size()) {

            // Pula linhas em branco entre blocos
            if (linhas.get(i).isEmpty()) { i++; continue; }

            // Próxima linha não-vazia é o nome do jogador
            String nomeJogador = linhas.get(i);
            i++;

            // Valida que o nome parece um nome (não um "a5 d")
            if (nomeJogador.contains(" "))
                throw new ConfiguracaoInvalidaException(
                        "Esperava nome de jogador, mas encontrou: '" + nomeJogador + "'", i);

            // Lê exatamente quantNavios linhas de posição
            List<String> linhasNavio = new ArrayList<>();
            while (linhasNavio.size() < quantNavios && i < linhas.size()) {
                String linha = linhas.get(i);
                if (!linha.isEmpty()) linhasNavio.add(linha);
                i++;
            }

            if (linhasNavio.size() < quantNavios)
                throw new ConfiguracaoInvalidaException(
                        "Jogador '" + nomeJogador + "' tem menos navios que o esperado ("
                        + quantNavios + "). Encontrados: " + linhasNavio.size() + ".");

            // Cria o jogador (todos são HUMANO por padrão; o P2 distingue pela posição na lista)
            Jogador jogador = new JogadorHumano(nomeJogador);

            // Processa cada linha de navio
            for (int n = 0; n < linhasNavio.size(); n++) {
                int tamanho = n + 1; // navio 0 → tamanho 1, navio 1 → tamanho 2, etc.
                Navio navio = parseNavio(linhasNavio.get(n), nomeJogador, tamanho, i - quantNavios + n);
                validarPosicionamento(navio, jogador.getTabuleiro(), nomeJogador);
                jogador.adicionarNavio(navio);
            }

            partida.adicionarJogador(jogador);
        }

        if (partida.getJogadores().isEmpty())
            throw new ConfiguracaoInvalidaException("Nenhum jogador encontrado no arquivo.");

        return partida;
    }

    // ----------------------------------------------------------------
    // Parsers individuais
    // ----------------------------------------------------------------

    /**
     * Converte a primeira linha do arquivo no número de navios.
     * Repassa ConfiguracaoInvalidaException ao chamador (requisito RA2).
     */
    private int parseQuantidade(String linha, int numeroLinha)
            throws ConfiguracaoInvalidaException {
        try {
            int q = Integer.parseInt(linha.trim());
            if (q <= 0)
                throw new ConfiguracaoInvalidaException(
                        "Quantidade de navios deve ser maior que zero.", numeroLinha);
            return q;
        } catch (NumberFormatException e) {
            throw new ConfiguracaoInvalidaException(
                    "Primeira linha deve ser a quantidade de navios (inteiro). Encontrado: '"
                    + linha + "'", numeroLinha);
        }
    }

    /**
     * Interpreta uma linha no formato "a5 d" e cria o Navio correspondente.
     * Repassa ConfiguracaoInvalidaException ao chamador (requisito RA2).
     */
    private Navio parseNavio(String linha, String nomeJogador, int tamanho, int numeroLinha)
            throws ConfiguracaoInvalidaException {

        String[] partes = linha.split("\\s+");
        if (partes.length < 2)
            throw new ConfiguracaoInvalidaException(
                    "Formato inválido para navio de '" + nomeJogador
                    + "'. Esperado: 'posição direção' (ex: a5 d). Encontrado: '" + linha + "'",
                    numeroLinha);

        String posicao       = partes[0].toLowerCase();
        String codigoDirecao = partes[1].toLowerCase();

        // Valida formato da posição: deve começar com letra e ter número(s) após
        if (!posicao.matches("[a-j]\\d+"))
            throw new ConfiguracaoInvalidaException(
                    "Posição inválida: '" + posicao
                    + "'. Use letra (a-j) seguida de número (1-10). Ex: a5, h8.",
                    numeroLinha);

        // Valida e converte a direção
        Direcao direcao;
        try {
            direcao = Direcao.fromCodigo(codigoDirecao);
        } catch (IllegalArgumentException e) {
            throw new ConfiguracaoInvalidaException(e.getMessage(), numeroLinha);
        }

        // Calcula índices [linha][coluna] a partir de "a5"
        int[] indices = Tabuleiro.parsePosicao(posicao);
        int linhaIdx  = indices[0];
        int colunaIdx = indices[1];

        // Cria o navio, calcula suas células e retorna
        Navio navio = new NavioComum(posicao, direcao, tamanho);
        navio.calcularCelulas(linhaIdx, colunaIdx);
        return navio;
    }

    // ----------------------------------------------------------------
    // Validação de posicionamento — lança PosicionamentoInvalidoException
    // ----------------------------------------------------------------

    /**
     * Verifica se todas as células do navio estão dentro dos limites
     * e não estão sobrepostas a outros navios já posicionados.
     *
     * Repassa PosicionamentoInvalidoException ao chamador (requisito RA2).
     */
    private void validarPosicionamento(Navio navio, Tabuleiro tabuleiro, String nomeJogador)
            throws PosicionamentoInvalidoException {

        for (int[] celula : navio.getCelulas()) {
            int l = celula[0];
            int c = celula[1];

            if (!tabuleiro.dentroDosLimites(l, c))
                throw new PosicionamentoInvalidoException(
                        "Célula (" + (l + 1) + "," + (char)('a' + c) + ") está fora do tabuleiro 10x10.",
                        nomeJogador, navio.getPosicaoInicial());

            if (!tabuleiro.celulaLivre(l, c))
                throw new PosicionamentoInvalidoException(
                        "Célula (" + (l + 1) + "," + (char)('a' + c) + ") já está ocupada por outro navio.",
                        nomeJogador, navio.getPosicaoInicial());
        }
    }
}

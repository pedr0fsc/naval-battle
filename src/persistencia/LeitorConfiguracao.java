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
                linha = linha.trim();
                // Ignora linhas vazias e comentários que iniciam com '#'
                if (!linha.isEmpty() && !linha.startsWith("#")) {
                    resultado.add(linha);
                }
            }
        }
        return resultado;
    }

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
    
    // ----------------------------------------------------------------
    // Construção da Partida a partir das linhas
    // ----------------------------------------------------------------

    private Partida construirPartida(List<String> linhas)
            throws ConfiguracaoInvalidaException, PosicionamentoInvalidoException {

        if (linhas.isEmpty())
            throw new ConfiguracaoInvalidaException("Arquivo de configuração está vazio.");

        int quantNavios = parseQuantidade(linhas.get(0), 1);
        if (quantNavios < 1 || quantNavios > 10)
            throw new ConfiguracaoInvalidaException("Quantidade de navios deve ser entre 1 e 10.");
            
        Partida partida = new Partida(quantNavios);

        int i = 1;
        while (i < linhas.size() && linhas.get(i).isEmpty()) i++;

        if (i >= linhas.size())
            throw new ConfiguracaoInvalidaException("Nome do jogador não encontrado no arquivo.");

        String nomeJogador = linhas.get(i++);
        Jogador jogadorHumano = new JogadorHumano(nomeJogador);

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

        java.util.Map<TipoNavio, Integer> contagem = new java.util.EnumMap<>(TipoNavio.class);
        for (int n = 0; n < linhasNavio.size(); n++) {
            Navio navio = parseNavio(linhasNavio.get(n), nomeJogador, i - quantNavios + n);
            
            contagem.put(navio.getTipo(), contagem.getOrDefault(navio.getTipo(), 0) + 1);
            if (contagem.get(navio.getTipo()) > 2)
                throw new ConfiguracaoInvalidaException("Limite de 2 navios do tipo " + navio.getTipo() + " excedido.", i - quantNavios + n);
                
            validarPosicionamento(navio, jogadorHumano.getTabuleiro(), nomeJogador);
            jogadorHumano.adicionarNavio(navio);
        }

        partida.adicionarJogador(jogadorHumano);
        Jogador ia = new JogadorIA("Computador");
        posicionarNaviosIA(ia, quantNavios);
        partida.adicionarJogador(ia);

        return partida;
    }

    private void posicionarNaviosIA(Jogador ia, int quantNavios) {
        java.util.Random random = new java.util.Random();
        Tabuleiro tab = ia.getTabuleiro();
        Direcao[] direcoes = Direcao.values();
        TipoNavio[] tipos = TipoNavio.values();
        
        java.util.Map<TipoNavio, Integer> contagem = new java.util.EnumMap<>(TipoNavio.class);

        for (int i = 0; i < quantNavios; i++) {
            TipoNavio tipo;
            int tentativas = 0;
            do {
                tipo = tipos[random.nextInt(tipos.length)];
                tentativas++;
            } while (contagem.getOrDefault(tipo, 0) >= 2 && tentativas < 100);
            
            contagem.put(tipo, contagem.getOrDefault(tipo, 0) + 1);
            
            boolean posicionado = false;
            while (!posicionado) {
                int l = random.nextInt(10);
                int c = random.nextInt(10);
                Direcao d = direcoes[random.nextInt(direcoes.length)];
                
                String pos = "" + (char)('a' + c) + (l + 1);
                Navio navio = new NavioComum(tipo, pos, d, tipo.getTamanho());
                navio.calcularCelulas(l, c);

                boolean cabe = true;
                for (int[] celula : navio.getCelulas()) {
                    if (!tab.dentroDosLimites(celula[0], celula[1]) || !tab.celulaLivre(celula[0], celula[1])) {
                        cabe = false;
                        break;
                    }
                }

                if (cabe) {
                    ia.adicionarNavio(navio);
                    posicionado = true;
                }
            }
        }
    }
    
    private Navio parseNavio(String linha, String nomeJogador, int numeroLinha)
            throws ConfiguracaoInvalidaException {

        String[] partes = linha.split("\\s+");
        if (partes.length < 3)
            throw new ConfiguracaoInvalidaException(
                    "Formato inválido para navio de '" + nomeJogador
                    + "'. Esperado: 'tipo posição direção' (ex: fragata a5 d). Encontrado: '" + linha + "'",
                    numeroLinha);

        TipoNavio tipo;
        try {
            tipo = TipoNavio.fromString(partes[0]);
        } catch (IllegalArgumentException e) {
            throw new ConfiguracaoInvalidaException("Tipo de navio inválido: " + partes[0], numeroLinha);
        }

        String posicao       = partes[1].toLowerCase();
        String codigoDirecao = partes[2].toLowerCase();

        if (!posicao.matches("[a-j]\\d+"))
            throw new ConfiguracaoInvalidaException(
                    "Posição inválida: '" + posicao
                    + "'. Use letra (a-j) seguida de número (1-10). Ex: a5, h8.",
                    numeroLinha);

        Direcao direcao;
        try {
            direcao = Direcao.fromCodigo(codigoDirecao);
        } catch (IllegalArgumentException e) {
            throw new ConfiguracaoInvalidaException(e.getMessage(), numeroLinha);
        }

        int[] indices = Tabuleiro.parsePosicao(posicao);
        
        Navio navio = new NavioComum(tipo, posicao, direcao, tipo.getTamanho());
        navio.calcularCelulas(indices[0], indices[1]);
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

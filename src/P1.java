import excecoes.ConfiguracaoInvalidaException;
import excecoes.PosicionamentoInvalidoException;
import modelo.Partida;
import persistencia.LeitorConfiguracao;
import persistencia.SerializadorPartida;

import java.io.File;
import java.io.IOException;

/**
 * P1 - Batalha Naval | Programacao Orientada a Objetos
 *
 *  Fluxo de execucao:
 *  1. Le "dados/configuracao.txt"
 *  2. Valida dados e posicionamentos
 *  3. Serializa o objeto Partida em "dados/partida.bin"
 *
 *  Uso:
 *    java -cp out P1                                           (caminhos padrao)
 *    java -cp out P1 dados\configuracao.txt dados\partida.bin (Windows)
 *    java -cp out P1 dados/configuracao.txt dados/partida.bin (Linux/Mac)
 */
public class P1 {

    // File.separator garante o separador correto em cada sistema operacional
    // Windows usa \ e Linux/Mac usam /
    private static final String ARQUIVO_TXT_PADRAO =
            "dados" + File.separator + "dados/configuracao.txt";
    private static final String ARQUIVO_BIN_PADRAO =
            "dados" + File.separator + "partida.bin";

    public static void main(String[] args) {

        // Aceita caminhos como argumento ou usa os padroes
        String arquivoTxt = args.length > 0 ? args[0] : ARQUIVO_TXT_PADRAO;
        String arquivoBin = args.length > 1 ? args[1] : ARQUIVO_BIN_PADRAO;

        System.out.println("Batalha Naval - P1: Configuracao Inicial\n");

        // Garante que a pasta de saida existe antes de tentar salvar
        File pastaDados = new File(arquivoBin).getParentFile();
        if (pastaDados != null && !pastaDados.exists()) {
            pastaDados.mkdirs();
        }

        try {
            // Passo 1: Leitura e construcao dos objetos
            System.out.println("[1/3] Lendo arquivo: " + arquivoTxt);
            LeitorConfiguracao leitor = new LeitorConfiguracao(arquivoTxt);
            Partida partida = leitor.lerConfiguracao();
            System.out.println("      OK - " + partida.getJogadores().size()
                    + " jogador(es) carregado(s), "
                    + partida.getQuantidadeNaviosPorJogador()
                    + " navio(s) cada.\n");

            // Passo 2: Resumo no console
            System.out.println("[2/3] Configuracao carregada:");
            System.out.println("-----------------------------------------");
            System.out.println(partida.gerarDescricao());

            // Passo 3: Serializacao binaria
            System.out.println("[3/3] Salvando em: " + arquivoBin);
            SerializadorPartida serializador = new SerializadorPartida(arquivoBin);
            serializador.salvar(partida);
            System.out.println("      OK - Arquivo binario gerado com sucesso.\n");

            System.out.println("P1 concluido. Execute P2 para jogar!");

        } catch (ConfiguracaoInvalidaException e) {
            System.err.println("\n[ERRO DE CONFIGURACAO] " + e.getMessage());
            System.err.println("Verifique o arquivo: " + arquivoTxt);
            System.exit(1);

        } catch (PosicionamentoInvalidoException e) {
            System.err.println("\n[ERRO DE POSICIONAMENTO] " + e.getMessage());
            System.err.println("Corrija as posicoes no arquivo: " + arquivoTxt);
            System.exit(2);

        } catch (IOException e) {
            System.err.println("\n[ERRO DE ARQUIVO] " + e.getMessage());
            System.err.println("Certifique-se de que o arquivo existe no caminho: " + arquivoTxt);
            System.exit(3);
        }
    }
}

package persistencia;

import modelo.Jogador;

import java.io.FileWriter;
import java.io.IOException;

public class ResultadoFinal {

    private static final String ARQUIVO =
            "dados/resultado_final.txt";

    public static void salvar(Jogador vencedor,
                              Jogador perdedor) {

        try (FileWriter fw = new FileWriter(ARQUIVO)) {

            fw.write("Resultado Final \n\n");
            fw.write("Vencedor: "
                    + vencedor.getNome()
                    + "\n");
            fw.write("Perdedor: "
                    + perdedor.getNome()
                    + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
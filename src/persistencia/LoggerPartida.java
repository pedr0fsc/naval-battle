package persistencia;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerPartida {

    private static final String ARQUIVO = "dados/log.txt";

    public static void registrar(String mensagem) {

        try (FileWriter fw = new FileWriter(ARQUIVO, true)) {

            String horario =
                    LocalDateTime.now()
                            .format(
                                    DateTimeFormatter.ofPattern("HH:mm:ss")
                            );

            fw.write(horario + " - " + mensagem + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
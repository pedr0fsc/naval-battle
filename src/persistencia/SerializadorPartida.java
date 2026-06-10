package persistencia;

import excecoes.ConfiguracaoInvalidaException;
import modelo.Partida;

import java.io.*;

/**
 * Salva e carrega o objeto Partida em formato binário (serialização Java).
 *
 * Ambos os métodos repassam exceções ao chamador sem try-catch (requisito RA2).
 */
public class SerializadorPartida {

    private final String caminhoBinario;

    public SerializadorPartida(String caminhoBinario) {
        this.caminhoBinario = caminhoBinario;
    }

    /**
     * Salva a Partida em arquivo binário.
     * Repassa IOException ao chamador — requisito RA2 (throws sem try-catch).
     */
    public void salvar(Partida partida) throws IOException {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(caminhoBinario))) {
            oos.writeObject(partida);
        }
    }

    /**
     * Carrega a Partida de um arquivo binário.
     * Usado pelo P2 para restaurar a configuração inicial.
     * Repassa exceções ao chamador — requisito RA2.
     *
     * @throws IOException              se o arquivo não puder ser lido
     * @throws ClassNotFoundException   se a classe Partida não for reconhecida
     * @throws ConfiguracaoInvalidaException se o objeto no arquivo não for uma Partida
     */
    public Partida carregar()
            throws IOException, ClassNotFoundException, ConfiguracaoInvalidaException {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(caminhoBinario))) {
            Object obj = ois.readObject();
            if (!(obj instanceof Partida))
                throw new ConfiguracaoInvalidaException(
                        "Arquivo binário não contém um objeto Partida válido: " + caminhoBinario);
            return (Partida) obj;
        }
    }
}

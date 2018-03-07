package blockchain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author Will
 */
public class messageInHandler implements Runnable {

    private final Socket socket;

    public messageInHandler(Socket newSocket) {
        this.socket = newSocket;
    }

    @Override
    public void run() {
        final InputStream is;
        try {
            is = socket.getInputStream();
        } catch (IOException ex) {
            return;
        }
        final InputStreamReader isr = new InputStreamReader(is);
        final BufferedReader br = new BufferedReader(isr);

        while (socket.isConnected()) {
            try {
                if (br.ready()) {
                    System.out.println("Reading");
                    System.out.println(br.read() + "from " + socket.toString());
                }
            } catch (IOException ex) {
                return;
            }
        }
    }
}


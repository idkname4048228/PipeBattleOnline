import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class InThread extends Thread {
    Socket socket;
    String receiveStr = "";

    InThread(Socket sk) {
        socket = sk;
    }

    public void run() {
        try {
            BufferedInputStream in = new java.io.BufferedInputStream(socket.getInputStream());
            byte[] b = new byte[1024];
            String data = "";

            while (true) {
                int length = in.read(b);
                data = new String(b, 0, length);
                if (data.equals("EXIT")) {
                    break;
                }
                System.out.println("receive: " + data);
                receiveStr += data;
            }
            System.out.println("stop receive");
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("inThread socket had been closed");
        } catch (IOException e) {
            System.out.println("InThread IO has problem");
            e.printStackTrace();
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("string error");
        }

    }
}

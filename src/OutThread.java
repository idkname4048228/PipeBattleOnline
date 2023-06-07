import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class OutThread extends Thread {
    Socket socket;
    String sendStr = "";

    OutThread(Socket sk) {
        socket = sk;
    }

    public void run() {
        try {
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

            while (true) {
                if (sendStr.length() != 0) {
                    System.out.println("send: " + sendStr);
                    out.write(sendStr.getBytes());
                    out.flush();
                    sendStr = "";
                }
                if (sendStr.equals("EXIT"))
                    break;
                Thread.sleep(500);
            }
            System.out.println("stop send");

        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("outThread socket had been closed");
        } catch (IOException e) {
            System.out.println("OutThread IO has problem");
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}

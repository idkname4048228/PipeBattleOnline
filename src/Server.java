import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.SwingWorker;
import javax.swing.Timer;

public class Server extends java.lang.Thread {

    private ServerSocket server;
    private final int ServerPort = 8765;

    InThread in;
    OutThread out;

    String sendStr = "";
    String receiveStr = "";

    public Server() {
        try {
            server = new ServerSocket(ServerPort);

        } catch (java.io.IOException e) {
            System.out.println("Socket has problem !");
            e.printStackTrace();
        }
    }

    private void initWorker() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                listenConnect();
                return null;
            }
        };
        worker.execute();
    }

    private void listenConnect() {
        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sendStr.length() != 0) {
                    out.sendStr += sendStr;
                    sendStr = "";
                }
                if (in.receiveStr.length() != 0) {
                    receiveStr += in.receiveStr;
                    in.receiveStr = "";
                }
            }
        });
        timer.start();
    }

    public void run() {
        Socket socket;

        System.out.println("Server active !");
        socket = null;
        try {
            synchronized (server) {
                socket = server.accept();
            }
            in = new InThread(socket);
            out = new OutThread(socket);
            in.start();
            out.start();
            initWorker();

            in.join();
            out.join();

            socket.close();
            System.out.println("server socket closed");

        } catch (IOException e) {
            System.out.println("Server IO has problem");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Server thread has problem");
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        (new Server()).start();
    }

}
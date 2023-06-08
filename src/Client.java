import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.io.IOException;

import javax.swing.SwingWorker;
import javax.swing.Timer;

public class Client {
    private String address = "127.0.0.1";
    private int port = 8765;
    private Socket client;

    InThread in;
    OutThread out;

    String sendStr = "";
    String receiveStr = "";

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

                    System.out.println("client out " + sendStr);
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

    public Client() {

        Scanner sc = new Scanner(System.in);
        address = sc.nextLine();
        sc.close();

        client = new Socket();
        InetSocketAddress isa = new InetSocketAddress(this.address, this.port);
        try {
            client.connect(isa, 10000);
        } catch (IOException e) {
            System.out.println("Client IO has promblem");
            e.printStackTrace();
        }
    }

    public void start() {

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    in = new InThread(client);
                    out = new OutThread(client);
                    in.start();
                    out.start();
                    initWorker();

                    out.join();
                    in.join();

                    client.close();
                    System.out.println("client socket closed");
                } catch (IOException e) {
                    System.out.println("Client IO has promblem");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    System.out.println("Client thread has problem");
                    e.printStackTrace();
                }
                return null;
            }
        };
        worker.execute();

    }

    public static void main(String args[]) {
        new Client();
    }
}

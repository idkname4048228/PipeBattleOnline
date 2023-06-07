import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

public class App {
    Server server;
    Client client;
    GameDemo demo;
    Game game;

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
                if (server != null) {
                    if (demo.game.sendStr.length() != 0) {
                        server.sendStr += demo.game.sendStr;
                        demo.game.sendStr = "";
                    }
                    if (server.receiveStr.length() != 0) {
                        demo.game.receiveStr = server.receiveStr;
                        server.receiveStr = "";
                    }
                } else {
                    if (demo.game.sendStr.length() != 0) {
                        client.sendStr += demo.game.sendStr;
                        demo.game.sendStr = "";
                    }
                    if (client.receiveStr.length() != 0) {
                        demo.game.receiveStr = client.receiveStr;
                        client.receiveStr = "";
                    }
                }

            }
        });
        timer.start();
    }

    public App(String[] args) {

        if (args[0].equals("server")) {
            server = new Server();
            server.start();
        } else {
            client = new Client();
            client.start();
        }
        this.demo = new GameDemo();
        frameStart();
        initWorker();
    }

    private void frameStart() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    demo.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String args[]) {
        new App(args);
    }

}
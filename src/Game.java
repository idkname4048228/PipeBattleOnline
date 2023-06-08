import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.Border;

public class Game {
    String receiveStr = "";
    String sendStr = "";

    GameControlPanel controlPanel;
    Shop shop;
    Round round;

    JPanel mainPanel;
    MapStorage storage;
    GameMap gameMap;

    boolean gameOver = false;
    boolean flowing = false;
    boolean enemyReady = false;
    boolean friendReady = false;

    String selectPipeCode = "";

    boolean needListen = false;

    private void listenRemainSecond() {
        Timer timer;
        timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {// 使用 BFS 進行每次水管流向的確認
                if (needListen && round.remainSecond <= 0) {
                    needListen = false;
                    ready();
                }
            }
        });
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                timer.start();// 開始計時器
                return null;
            }
        };
        worker.execute();
    }

    private void listenConnect() {
        Timer connectTimer;
        connectTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {// 使用 BFS 進行每次水管流向的確認
                if (gameMap.sendStr.length() != 0) {
                    System.out.println("accept map");
                    sendStr += gameMap.sendStr;
                    gameMap.sendStr = "";
                }
                while (receiveStr.length() != 0) {
                    if (receiveStr.substring(0, 1).equals("r")) {
                        enemyReady = true;
                        receiveStr = receiveStr.substring(6);
                    } else {
                        System.out.println(receiveStr);
                        String row = receiveStr.substring(0, 1);
                        if (row.equals("1")) {
                            row += "0";
                            receiveStr = receiveStr.substring(3);
                        } else {
                            receiveStr = receiveStr.substring(2);
                        }
                        System.out.println(receiveStr);
                        String col = receiveStr.substring(0, 1);
                        receiveStr = receiveStr.substring(2);
                        System.out.println(receiveStr);
                        String sourceCode = receiveStr.substring(0, 2);
                        receiveStr = receiveStr.substring(3);
                        System.out.println(receiveStr);

                        int rowIndex = 5 - (Integer.valueOf(row) - 5);
                        int colIndex = 4 - (Integer.valueOf(col) - 4);

                        String code = sourceCode.substring(0, 1);
                        int degreeUnit = Integer.valueOf(sourceCode.substring(1, 2));
                        degreeUnit -= 1;
                        degreeUnit += 2;
                        degreeUnit %= 4;
                        degreeUnit += 1;

                        System.out.println("modify: " + rowIndex + " " + colIndex + " " + code + degreeUnit);
                        if (rowIndex == 0 && code.equals("T")) {
                            gameMap.pipeMap.get(rowIndex).get(colIndex).setToT(3);
                        } else if (rowIndex == 0 && code.equals("S")) {
                            gameMap.pipeMap.get(rowIndex).get(colIndex).setToS();
                        } else {
                            gameMap.pipeMap.get(rowIndex).get(colIndex).setSourceCode(code, degreeUnit);
                        }

                        gameMap.pipeMap.get(rowIndex).get(colIndex).init();
                    }
                }

            }
        });

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                connectTimer.start();
                return null;
            }
        };
        worker.execute();
    }

    Game() {
        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBounds(0, 0, 576, 704);
        mainPanel.setBackground(new Color(255, 255, 255));
        storage = new MapStorage();
        gameMap = new GameMap(storage.getMapFile());
        bindPanel(gameMap, mainPanel);
        listenRemainSecond();
        listenConnect();
        listenReady();
    }

    // 回傳遊戲面板
    public JPanel getPanel() {
        return mainPanel;
    }

    private void bindPanel(GameMap gameMap, JPanel panel) {
        panel.removeAll();// 把面板上的 JLabl 全部移除
        panel.revalidate();// 並讓它重新可用

        // 計算圖片的寬及高
        int elementWidth = panel.getWidth() / gameMap.width;
        int elementHeight = panel.getHeight() / gameMap.height;

        ArrayList<ArrayList<Pipe>> pipes = gameMap.getPipes();

        // 遍歷 nowMap 內的每個單位
        for (int row = 0; row < gameMap.height; row++) {
            for (int col = 0; col < gameMap.width; col++) {
                Pipe pipe = pipes.get(row).get(col);
                pipe.setSize(elementWidth, elementHeight);

                JLabel element = new JLabel();// 創建新的 JLabel
                pipe.bindLabel(element);
                element.setBounds(elementWidth * col, elementHeight * row, elementWidth, elementHeight);// 計算 JLabel
                                                                                                        // 在面板應所處的座標，及給定寬度及高度
                panel.add(element);// 把 JLabel 加進面板
                pipe.setFriendly(row > 5);
                if (!pipe.empty && pipe.pipeCode.equals("w"))
                    continue;

                final int nowCol = col;
                final int nowRow = row;
                final Pipe nowPipe = pipe;
                element.addMouseListener(new MouseAdapter() {
                    JLabel nowLabel = element;

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        nowLabel.setBounds(elementWidth * nowCol, elementHeight * nowRow, elementWidth, elementHeight);
                        // 创建绿色边框
                        Border border = BorderFactory.createLineBorder(Color.decode("#89FF2E"), 1, true);
                        nowLabel.setBorder(border);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        element.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (nowPipe.flowing || !round.canMove || !nowPipe.friendly) {
                            return;
                        }
                        if (nowPipe.withWater) {
                            if (nowPipe.pipeCode.equals("T")) {
                                nowPipe.setToS();
                            } else {
                                nowPipe.setToT(nowRow == 0 ? 3 : 1);
                            }
                            sendStr += nowRow + " " + nowCol + " " + nowPipe.sourceCode + " ";
                            return;
                        }

                        if (selectPipeCode.length() != 0) {
                            int shopIndex = shop.search(selectPipeCode);
                            if (shop.pipeAmount[shopIndex] < 1) {
                                JOptionPane.showMessageDialog(mainPanel,
                                        "No enough amount.",
                                        "Can\'t buy",
                                        JOptionPane.PLAIN_MESSAGE);
                                return;
                            }
                            shop.use(selectPipeCode);
                            controlPanel.updateCommodity();

                            nowPipe.setSourceCode(selectPipeCode, 1);
                            nowPipe.setSize(elementWidth, elementHeight);
                            nowLabel.setIcon(nowPipe.getImage());
                            sendStr += nowRow + " " + nowCol + " " + nowPipe.sourceCode + " ";
                        }
                    }
                });
            }
        }

    }

    public boolean check() {
        flowing = true;

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                gameMap.check();

                int amount = gameMap.checkWin();
                System.out.println("Amount: " + amount);
                if (amount == 0) {
                    JOptionPane.showMessageDialog(mainPanel, "平手", "Notify", JOptionPane.PLAIN_MESSAGE);
                } else if (amount > 0) {
                    JOptionPane.showMessageDialog(mainPanel, "你贏了", "Notify", JOptionPane.PLAIN_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "你輸了", "Notify", JOptionPane.PLAIN_MESSAGE);
                }
                return null;
            }
        };
        worker.execute();

        return !gameMap.waste;
    }

    public void selectPipe(String pipeCode) {
        selectPipeCode = pipeCode;
    }

    public void cancelSelect() {
        selectPipeCode = "";
    }

    public void ready() {
        round.stop();
        if (friendReady)
            return;
        friendReady = true;
        this.sendStr += "ready ";
        controlPanel.updateRound(11);
    }

    public String getSendStr() {
        return this.sendStr;
    }

    public void start() {
        if (round.nowRound >= 9) {
            controlPanel.updateRound(10);
            check();
            return;
        }

        JOptionPane.showMessageDialog(mainPanel, "start", "Start", JOptionPane.PLAIN_MESSAGE);

        if (round.nowRound != 0) {
            shop.gainMoney(round.nowRound);
        }
        controlPanel.updateCommodity();
        controlPanel.updateRound(round.nowRound + 1);
        needListen = true;
        round.start();
    }

    private void listenReady() {
        Timer connectTimer;
        connectTimer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {// 使用 BFS 進行每次水管流向的確認
                if (friendReady && enemyReady) {
                    friendReady = false;
                    enemyReady = false;
                    start();
                }
            }
        });

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                connectTimer.start();
                return null;
            }
        };
        worker.execute();
    }

}

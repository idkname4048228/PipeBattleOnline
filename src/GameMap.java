import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import javax.swing.SwingWorker;
import javax.swing.Timer;

public class GameMap {
    String sendStr = "";

    ArrayList<ArrayList<Pipe>> pipeMap = new ArrayList<>();
    int width;
    int height;
    ArrayList<ArrayList<String>> originalMapFile = null;// 當前地圖(文字檔)
    ArrayList<int[]> friend = new ArrayList<>();// 友方目前的水管
    ArrayList<int[]> enemy = new ArrayList<>();// 敵方目前的水管
    ArrayList<int[]> end = new ArrayList<>();// 地圖的終點(沒水的水庫)

    boolean waste = false;
    boolean finish = false;
    ArrayList<int[]> nextCoordinates;

    GameMap(ArrayList<ArrayList<String>> mapFile) {
        originalMapFile = mapFile;// 記住當前地圖檔
        // 初始化
        width = originalMapFile.get(0).size();// 取得寬度
        height = originalMapFile.size();// 取得高度

        for (int row = 0; row < height; row++) {
            ArrayList<Pipe> pipes = new ArrayList<>();
            for (int col = 0; col < width; col++) {
                Pipe tmpPipe = new Pipe(originalMapFile.get(row).get(col));
                pipes.add(tmpPipe);

                if (tmpPipe.empty)
                    continue;
            }
            pipeMap.add(pipes);
        }
        listenChange();
    }

    public ArrayList<ArrayList<Pipe>> getPipes() {
        return pipeMap;
    }

    public void startFlow() {
        initStart();

        Timer timer = null;
        CountDownLatch latch = new CountDownLatch(1);// 等待計時器執行完畢，才能回傳玩家是否成功
        timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {// 使用 BFS 進行每次水管流向的確認
                BFS();
                if (finish) {
                    ((Timer) e.getSource()).stop();// 停止計時器
                    latch.countDown();
                }
            }
        });
        timer.start();// 開始計時器
        try {
            latch.await();// 等待計時器結束
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initStart() {
        friend = new ArrayList<>();// 友方目前的水管
        enemy = new ArrayList<>();// 敵方目前的水管
        for (int col = 0; col < 9; col++) {
            if (pipeMap.get(0).get(col).pipeCode.equals("T")) {
                enemy.add(new int[] { 0, col });
            }
            if (pipeMap.get(10).get(col).pipeCode.equals(("T"))) {
                friend.add(new int[] { 10, col });
            }
        }
    }

    private void BFS() {
        // System.out.println("bfs");

        nextCoordinates = new ArrayList<>();
        for (int[] coordinate : friend) {// 遍歷 waterPipe 內的每個水管，裡面的每個水管都是有水流過的
            handlePipe(coordinate);
        }
        friend = nextCoordinates;
        nextCoordinates = new ArrayList<>();
        for (int[] coordinate : enemy) {// 遍歷 waterPipe 內的每個水管，裡面的每個水管都是有水流過的
            handlePipe(coordinate);
        }
        enemy = nextCoordinates;

        finish = friend.size() == 0 && enemy.size() == 0;
    }

    private void handlePipe(int[] coordinate) {
        // 取得座標
        int pipeRow = coordinate[0];
        int pipeCol = coordinate[1];

        Pipe nowPipe = pipeMap.get(pipeRow).get(pipeCol);
        // System.out.println(pipeRow + " " + pipeCol);
        if (nowPipe.empty)
            return;

        nowPipe.countdown();
        if (nowPipe.flowCount > 0) {
            nextCoordinates.add(new int[] { pipeRow, pipeCol });
            return;
        }
        System.out.println(nowPipe.flowCount);

        boolean[] flowDirections = nowPipe.nextDirection();

        int[] fromDirection = new int[] { 3, 4, 1, 2 };
        boolean[] conditions = new boolean[] { pipeRow != 0, pipeCol != width - 1, pipeRow != height - 1,
                pipeCol != 0 };
        int[] rowDiff = new int[] { -1, 0, 1, 0 };
        int[] colDiff = new int[] { 0, 1, 0, -1 };

        for (int i = 0; i < 4; i++) {
            if (!flowDirections[i]) { // 如果現在這個水管不能流這個方向
                continue; // 跳過這個座標
            }

            if (!conditions[i]) { // 如果超出邊界
                waste = true; // 如果超出邊界 浪費水
                continue;// 跳過這個座標
            }

            int[] nextPipeCoordinate = new int[] { pipeRow + rowDiff[i], pipeCol + colDiff[i] }; // 取得下個座標
            Pipe nextPipe = pipeMap.get(nextPipeCoordinate[0]).get(nextPipeCoordinate[1]);
            System.out.println(nextPipeCoordinate[0] + " " + nextPipeCoordinate[1]);

            if (isInNextCoordinates(nextPipeCoordinate)) {// 如果座標在下次要看的座標陣列裡面
                continue;// 跳過這個座標
            }

            if (nextPipe.empty) {// 如果 pipe 是空的
                waste = true;// 如果 pipe 是空的 浪費水
                continue;// 跳過這個方向
            }

            nextPipe.waterPast(fromDirection[i], nowPipe.deep, nowPipe.friendly); // 讓水流過 nextPipe
            if (nextPipe.withWater) { // 如果 nextPipe 是可以流過去的
                nextCoordinates.add(nextPipeCoordinate); // 加進下次要看的 陣列
            } else
                waste = true;// 不然就是浪費水
        }

    }

    private boolean isInNextCoordinates(int[] coordinate) {
        for (int[] innerCoordinate : nextCoordinates) {
            if (coordinate[0] == innerCoordinate[0] && coordinate[1] == innerCoordinate[1])
                return true;
        }
        return false;
    }

    public void stopClick() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Pipe pipe = pipeMap.get(row).get(col);
                pipe.flowing = true;
                pipe.label.setCursor(null);// 設定畫數在水管圖片上的樣式
            }
        }
    }

    public void check() {
        stopClick();
        System.out.println("checking");
        startFlow();
    }

    public int checkWin() {
        int amount = 0;
        for (int col = 0; col < 9; col++) {
            if (pipeMap.get(5).get(col).color == Color.WHITE
                    || pipeMap.get(5).get(col).color.equals(Color.decode("#89FF2E")))
                continue;
            if (pipeMap.get(5).get(col).friendly) {
                amount += 1;
            } else {
                amount -= 1;
            }
        }
        return amount;
    }

    private void listenChange() {
        Timer changetTimer;
        changetTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {// 使用 BFS 進行每次水管流向的確認
                for (int row = 0; row < height; row++) {
                    for (int col = 0; col < width; col++) {
                        Pipe visitPipe = pipeMap.get(row).get(col);
                        if (visitPipe.change) {
                            sendStr += row + " " + col + " " + visitPipe.getSourceCode() + " ";
                            visitPipe.change = false;
                        }
                    }
                }
            }
        });
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                changetTimer.start();// 開始計時器
                return null;
            }
        };
        worker.execute();
    }

}
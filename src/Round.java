import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

import javax.swing.SwingWorker;
import javax.swing.Timer;

public class Round {
    CountDownLatch latch;
    Timer timer;
    int remainSecond = 60;

    int nowRound = 0;
    boolean canMove = false;

    Game game;
    GameControlPanel controlPanel;

    Round(Game game, GameControlPanel panel) {
        this.game = game;
        controlPanel = panel;
    }

    private void initTimer() {
        latch = new CountDownLatch(1);// 等待計時器執行完畢，才能回傳玩家是否成功
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {// 使用 BFS 進行每次水管流向的確認
                canMove = true;
                remainSecond -= 1;
                String nowTime = (remainSecond / 60) + ":" + ((remainSecond % 60) > 9 ? "" : "0")
                        + (remainSecond % 60);
                controlPanel.setTime(nowTime);
                if (remainSecond <= 0) {
                    ((Timer) e.getSource()).stop();// 停止計時器
                    canMove = false;
                    latch.countDown();
                }
            }
        });
    }

    public void start() {
        if (nowRound >= 5)
            return;
        remainSecond += nowRound * 3;
        nowRound++;
        initTimer();
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                timer.start();// 開始計時器
                try {
                    latch.await();// 等待計時器結束
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        worker.execute();
    }

    public void stop() {
        if (!(timer == null))
            timer.stop();
    }

}
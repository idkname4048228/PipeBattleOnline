import java.awt.Color;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class GameControlPanel extends JPanel {
    JPanel ControlPanel = new JPanel();
    Game game;
    Shop shop;
    Round round;

    Label roundLabel = new Label();

    Font biggerFont = new Font("微軟正黑體", Font.BOLD, 28);
    Font smallerFont = new Font("微軟正黑體", Font.BOLD, 24);

    Label nowTime = new Label();
    Label nowMoney = new Label();

    JLabel strightPipe = new JLabel();
    Label strightPipePrice = new Label();
    Label strightPipeAmount = new Label();

    JLabel bentPipe = new JLabel();
    Label bentPipePrice = new Label();
    Label bentPipeAmount = new Label();

    JLabel tPipe = new JLabel();
    Label tPipePrice = new Label();
    Label tPipeAmount = new Label();

    JLabel crossPipe = new JLabel();
    Label crossPipePrice = new Label();
    Label crossPipeAmount = new Label();

    JButton readyButton = new JButton();

    JLabel[] pipeLabels = { strightPipe, bentPipe, tPipe, crossPipe };
    PIPEIMAGE[] images = { PIPEIMAGE.STRAIGHT_PIPE, PIPEIMAGE.BENT_PIPE, PIPEIMAGE.T_PIPE, PIPEIMAGE.CROSS_PIPE };
    String[] pipeCodes = { "s", "b", "t", "c" };
    int nowSelectIndex = -1;

    Label[] priceLabels = { strightPipePrice, bentPipePrice, tPipePrice, crossPipePrice };
    Label[] amountLabels = { strightPipeAmount, bentPipeAmount, tPipeAmount, crossPipeAmount };

    private void setEventLister() {
        for (int i = 0; i < 4; i++) {
            JLabel element = pipeLabels[i];
            int index = i;
            element.addMouseListener(new MouseAdapter() {
                int nowIndex = index;

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!round.canMove) {
                        return;
                    }
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (nowIndex == nowSelectIndex) {
                            game.cancelSelect();
                            selectPipe(-1);
                        } else {

                            game.selectPipe(pipeCodes[nowIndex]);
                            selectPipe(nowIndex);
                        }
                    }
                    if (SwingUtilities.isRightMouseButton(e)) {
                        if (shop.pipePrice[nowIndex] > shop.currentMoney) {
                            JOptionPane.showMessageDialog(ControlPanel,
                                    "No enough money.",
                                    "Can\'t buy",
                                    JOptionPane.PLAIN_MESSAGE);
                            return;
                        }
                        shop.buy(pipeCodes[nowIndex]);
                        updateCommodity();
                    }
                }
            });
        }

        readyButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                game.ready();
            }

        });

    }

    private void selectPipe(int index) {
        JLabel element;
        if (nowSelectIndex >= 0) {
            element = (JLabel) pipeLabels[nowSelectIndex];
            element.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        }
        nowSelectIndex = index;
        if (nowSelectIndex >= 0) {
            element = (JLabel) pipeLabels[nowSelectIndex];
            element.setBorder(BorderFactory.createLineBorder(Color.decode("#89FF2E"), 1));
        }
    }

    public GameControlPanel(Game game, Shop shop) {
        this.game = game;
        this.shop = shop;
        updateCommodity();

        ControlPanel.setLayout(null);
        ControlPanel.setBounds(0, 0, 400, 704);
        ControlPanel.setBackground(new Color(255, 255, 255));
        Border border = BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK);
        ControlPanel.setBorder(border);

        nowTime.setBounds(25, 50, 150, 75);
        nowTime.setFont(biggerFont);
        nowTime.setAlignment(Label.LEFT);
        nowTime.setBackground(Color.GRAY);
        nowTime.setText("1:00");
        ControlPanel.add(nowTime);

        nowMoney.setBounds(225, 50, 150, 75);
        nowMoney.setFont(biggerFont);
        nowMoney.setBackground(Color.BLACK);
        nowMoney.setAlignment(Label.RIGHT);
        nowMoney.setForeground(Color.decode("#E8E73F"));

        ControlPanel.add(nowMoney);

        for (int i = 0; i < 4; i++) {
            JLabel pipeLabel = pipeLabels[i];
            Label priceLabel = priceLabels[i];
            Label amountLabel = amountLabels[i];

            pipeLabel.setBounds(50 + 200 * (i % 2), 150 + 175 * (i / 2), 100, 100);
            pipeLabel.setIcon(images[i].getImage(0, 100, 100));

            priceLabel.setBounds(50 + 200 * (i % 2), 250 + 175 * (i / 2), 100, 30);
            priceLabel.setAlignment(Label.CENTER);
            priceLabel.setFont(smallerFont);
            priceLabel.setForeground(Color.decode("#E8E73F"));

            amountLabel.setBounds(50 + 200 * (i % 2), 280 + 175 * (i / 2), 100, 30);
            amountLabel.setAlignment(Label.CENTER);
            amountLabel.setFont(smallerFont);

            ControlPanel.add(pipeLabel);
            ControlPanel.add(priceLabel);
            ControlPanel.add(amountLabel);
        }

        roundLabel.setBounds(25, 475, 350, 75);
        roundLabel.setText("Round: 0");
        roundLabel.setAlignment(Label.CENTER);
        roundLabel.setFont(biggerFont);
        ControlPanel.add(roundLabel);

        readyButton.setBounds(25, 575, 350, 75);
        readyButton.setText("Ready");
        readyButton.setFont(biggerFont);
        ControlPanel.add(readyButton);

        setEventLister();
    }

    public void updateRound(int round) {
        if (round == -1) {
            roundLabel.setText("Waiting enemy ready");
        } else if (round == 0) {
            roundLabel.setText("Checking");
        } else {
            roundLabel.setText("Round: " + round);
        }
    }

    public void updateNowMoney() {
        nowMoney.setText(Integer.toString(shop.currentMoney));
    }

    public void setTime(String time) {
        nowTime.setText(time);
    }

    public void updatePrice() {
        for (int i = 0; i < 4; i++) {
            priceLabels[i].setText(Integer.toString(shop.pipePrice[i]));
        }
    }

    public void updateAmount() {
        for (int i = 0; i < 4; i++) {
            amountLabels[i].setText(Integer.toString(shop.pipeAmount[i]));
        }
    }

    public void updateCommodity() {
        updateNowMoney();
        updatePrice();
        updateAmount();
    }

}
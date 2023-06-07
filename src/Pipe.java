
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Pipe {
    // 遊戲邏輯所需的 attribute
    boolean empty = false;
    String sourceCode;
    String pipeCode;
    int degreeUnit;
    boolean[] flowDirections = new boolean[] { false, false, false, false };// 預設無法向四周流動
    boolean withWater = false;
    int flowCount;
    int deep = 0;

    // 顯示圖片所需的 attribute
    int width;
    int height;
    PIPEIMAGE pipeImage;

    // 水管的 JLabel
    JLabel label;
    boolean flowing;
    boolean change;

    // 水管顏色由 label 背景顏色決定
    boolean friendly = true; // 是否為友方
    Color color = Color.WHITE;

    // 水管圖片需要更新

    Pipe(String code, boolean friendly) {
        sourceCode = code;
        this.friendly = friendly;
        init();
    }

    Pipe(String code) {
        // 把原本的 Code String 記下來，重新開始 (restart) 時可以用到
        sourceCode = code;
        init();
    }

    // 讓水管根據 sourceCode 做相關的動作
    public void init() {
        empty = false;
        // 如果 sourceCode 是 -- ，那代表是空的
        if (sourceCode.substring(0, 1).equals("-")) {
            empty = true;
            return;
        }
        // 初始化沒有水，但是需要更換圖片(可能原本有水)
        withWater = false;

        // 取得水管代號以及旋轉角度
        pipeCode = sourceCode.substring(0, 1);
        degreeUnit = Integer.valueOf(sourceCode.substring(1));

        flowDirections = new boolean[] { false, false, false, false };
        // 根據不同的水管代號指定不同的 PIPEIMAGE 的 enum
        switch (pipeCode) {
            // 直的
            case "s":
                flowCount = 1;
                pipeImage = PIPEIMAGE.STRAIGHT_PIPE;
                break;

            case "S":
                flowCount = -1;
                pipeImage = PIPEIMAGE.STRAIGHT_PIPE;
                withWater = true;
                break;

            // 彎的
            case "b":
                flowCount = 1;
                pipeImage = PIPEIMAGE.BENT_PIPE;
                break;
            // T 字
            case "t":
                flowCount = 2;
                pipeImage = PIPEIMAGE.T_PIPE;
                break;

            case "T":
                flowCount = -1;
                pipeImage = PIPEIMAGE.T_PIPE;
                withWater = true;
                flowDirections = new boolean[] { false, false, false, false };
                flowDirections[degreeUnit - 1] = true;
                break;
            // 交叉
            case "c":
                flowCount = 3;
                degreeUnit = 1;
                pipeImage = PIPEIMAGE.CROSS_PIPE;
                break;
            case "w":
                flowCount = -1;
                degreeUnit = 1;
                pipeImage = PIPEIMAGE.WATER_STORE;
                break;
        }
        checkColor();
        checkLabel();
    }

    // 回傳是否為水庫，不管有沒有水
    public boolean isWaterStore() {
        return pipeCode.equals("w");
    }

    // 設定該顯示的大小，之後需要回傳圖片時會用到
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void bindLabel(JLabel bindLabel) {
        this.label = bindLabel;

        label.setOpaque(true); // 设置JLabel为不透明
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));// 設定滑鼠在水管圖片上的樣式
        label.setBackground(color);
        if (!empty) {
            label.setIcon(getImage());// 設定水管圖片為 JLabel 的 icon
            if (isWaterStore())
                label.setCursor(null);
        }

        if (withWater || !friendly)
            return;

        label.addMouseListener(new MouseAdapter() {// 對每個 JLabel 設定點擊的事件監聽器
            @Override
            public void mouseClicked(MouseEvent e) {
                if (flowing) {// 如果水正在流，不做任何事
                    return;
                }
                if (empty) {
                    return;
                }
                int clickX = e.getX();
                rotate(clickX >= width / 2);
                label.setIcon(getImage());
                change = true;
            }
        });
    }

    // 回傳所屬的 PIPEIMAGE 的 imageIcon
    public ImageIcon getImage() {
        // 那圖片需要根據相關的角度及大小旋轉及縮放，水庫及交叉永遠是 1
        return pipeImage.getImage(90 * (degreeUnit - 1), width, height);
    }

    // 水管旋轉
    public boolean rotate(boolean right) {
        // 如果是交叉水管或者水庫
        if (pipeCode.equals("c") || isWaterStore())
            // 不做任何事
            return false;

        // 如果向右轉
        if (right) {
            // degreeUnit 在 1-4 的範圍裡加 1
            degreeUnit = (degreeUnit % 4) + 1;
            // 如果向左轉
        } else {
            // degreeUnit 在 1-4 的範圍裡減 1
            degreeUnit = (degreeUnit + 2) % 4 + 1;
        }

        sourceCode = pipeCode + degreeUnit;

        // 圖片動過了，下次渲染須更新

        return true;
    }

    // 水流過去的 method，參數是從哪裡流過來的角度代號
    public void waterPast(int from, int lastDeep, boolean lastFriendly) {
        if (deep == 0)
            deep = lastDeep;

        if (withWater) {
            if (isWaterStore()) {
                if (deep == lastDeep && friendly != lastFriendly) {
                    block();
                }
            }
            flowDirections[from - 1] = false;
            return;
        }

        switch (pipeCode) {
            // 直的
            case ("s"):
                // 更新水流方向
                flowDirections[degreeUnit - 1] = true;
                flowDirections[(degreeUnit + 1) % 4] = true;
                break;

            // 彎的
            case ("b"):
                // 更新水流方向
                flowDirections[degreeUnit - 1] = true;
                flowDirections[(degreeUnit % 4)] = true;
                break;

            // T 字
            case ("t"):
                // 更新水流方向
                flowDirections[degreeUnit - 1] = true;
                flowDirections[(degreeUnit % 4)] = true;
                flowDirections[(degreeUnit + 2) % 4] = true;
                break;

            // 交叉
            case ("c"):
                // 更新水流方向
                flowDirections = new boolean[] { true, true, true, true };
                break;

            case ("w"):
                // 更新水流方向
                flowDirections[from - 1] = true;
                friendly = lastFriendly;
                if (from == 1) {
                    pipeImage = PIPEIMAGE.UP_IN_WATER_STORE;
                } else {
                    pipeImage = PIPEIMAGE.DOWN_IN_WATER_STORE;
                }
                label.setIcon(getImage());
                break;
        }

        // 如果水是可以從 來自(from) 的方向流過來的
        if (flowDirections[from - 1]) {
            // 設為有水的
            withWater = true;
            // 更新圖片
            checkColor();
            checkLabel();
        }
        // 圖片更新過了

        // 水不會往回流
        flowDirections[from - 1] = false;
    }

    // 回傳自己可以讓水流動的方向
    public boolean[] nextDirection() {
        return flowDirections;
    }

    public void setSourceCode(String code, int degreeCode) {
        sourceCode = code + Integer.toString(degreeCode);
        init();
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setFriendly(boolean friendly) {
        this.friendly = friendly;
        checkColor();
        checkLabel();
    }

    private void checkColor() {
        if (withWater) {
            color = friendly ? Color.decode("#5ce1e6") : Color.decode("#FFB114");
        } else {
            color = Color.WHITE;
        }
    }

    private void checkLabel() {
        if (label != null && !empty) {
            label.setIcon(getImage());
            label.setBackground(color);
        }
    }

    public void block() {
        pipeImage = PIPEIMAGE.WATER_STORE;
        label.setIcon(getImage());
        color = Color.decode("#89FF2E");
        checkLabel();
    }

    public void setToT(int from) {
        sourceCode = "T" + Integer.toString(from == 3 ? 3 : 1);
        init();
        label.setIcon(getImage());
        label.setBackground(color);

    }

    public void setToS() {
        sourceCode = "S2";
        init();
        label.setIcon(getImage());
        label.setBackground(color);

    }

    public void countdown() {
        if (flowCount <= 0)
            return;
        flowCount -= 1;
        deep += 1;

        // 修不好的 bug

        // int rgb = this.color.getRGB(); // 解码RGB值
        // int alpha = (int) Math.round((1 - (0.5 * flowCount)) * 255); // 计算透明度值
        // int rgba = (rgb & 0x00FFFFFF) | (alpha << 24); // 将透明度添加到RGB值

        // Color newColor = new Color(rgba, true); // 创建具有透明度的颜色

        // this.label.setBackground(newColor);
    }

}

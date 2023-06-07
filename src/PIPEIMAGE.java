import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public enum PIPEIMAGE {
    // 以下是直接用路徑拿圖，這樣就可以只拿一次
    STRAIGHT_PIPE(new ImageIcon("src/img/straightPipe.png")),
    BENT_PIPE(new ImageIcon("src/img/bentPipe.png")),
    T_PIPE(new ImageIcon("src/img/tPipe.png")),
    CROSS_PIPE(new ImageIcon("src/img/crossPipe.png")),
    WATER_STORE(new ImageIcon("src/img/waterStore.png")),
    UP_IN_WATER_STORE(new ImageIcon("src/img/upInWaterStore.png")),
    DOWN_IN_WATER_STORE(new ImageIcon("src/img/downInWaterStore.png"));

    // 這個 enum 會有一個 imageIcon 的 attribute
    private ImageIcon image;

    // enum 的建構子，參數就直接是所屬的圖片了
    private PIPEIMAGE(ImageIcon image) {
        this.image = image;
    }

    // 根據需要的角度、大小，進行旋轉和縮放再回傳圖片
    public ImageIcon getImage(int angle, int width, int height) {
        return scaledIcon(rotateIcon(this.image, angle), width, height);
    }

    // 旋轉圖片直到特定角度
    private ImageIcon rotateIcon(ImageIcon icon, int angle) {
        // 創建一個 BufferedImage 來儲存旋轉後的圖像
        BufferedImage rotatedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);

        // 創建一個 Graphics2D 對象，用於繪製旋轉後的圖像
        Graphics2D g2d = rotatedImage.createGraphics();

        // 設置繪圖質量，以達到更好的旋轉效果
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // 計算旋轉的中心點
        int centerX = icon.getIconWidth() / 2;
        int centerY = icon.getIconHeight() / 2;

        // 創建一個 AffineTransform 對象，用於旋轉圖像
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(angle), centerX, centerY);

        // 繪製旋轉後的圖像
        g2d.drawImage(icon.getImage(), transform, null);
        g2d.dispose();

        // 返回旋轉後的 ImageIcon
        return new ImageIcon(rotatedImage);
    }

    // 縮放圖片直到特定大小
    private ImageIcon scaledIcon(ImageIcon icon, int width, int height) {
        // 從ImageIcon對象中獲取原始圖像
        Image img = icon.getImage();

        // 創建一個具有所需尺寸的新圖像
        Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        // 創建一個新的ImageIcon對象，使用新的圖像作為參數
        return new ImageIcon(newImg);
    }
}
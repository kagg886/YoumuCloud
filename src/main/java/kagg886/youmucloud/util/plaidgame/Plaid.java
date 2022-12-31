package kagg886.youmucloud.util.plaidgame;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Plaid {
    private final BufferedImage image;
    private final int x,y,margin;

    public Plaid(int x,int y,int marginLength,Color background) {
        //x:横多少格子,y:纵多少格子
        //margin:格子宽度,background:填充色
        this.x = x;
        this.y = y;
        this.margin = marginLength;

        image = new BufferedImage(marginLength * x + 1, marginLength * y + 1, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = image.createGraphics();
        for (int xIndex = 0; xIndex < image.getWidth(); xIndex += marginLength) {
            for (int yIndex = 0; yIndex < image.getHeight(); yIndex += marginLength) {
                g2d.setColor(background);
                g2d.fillRect(xIndex, yIndex, marginLength + 3, marginLength + 3);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(xIndex, yIndex, marginLength, marginLength);
            }
        }
        g2d.dispose();
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getWidth() {
        return x;
    }

    public int getHeight() {
        return y;
    }

    public void setMargin(int x, int y, Color color) {
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(color);
        int m = margin - 1;
        g2d.fillRect(margin * x - m,margin * y - m,m ,m);
        g2d.dispose();
    }
}

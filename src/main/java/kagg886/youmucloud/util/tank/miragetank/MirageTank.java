package kagg886.youmucloud.util.tank.miragetank;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

/**
 * @author kurisu9
 * @description 幻影坦克
 * @date 2022/2/11 10:40
 **/
public class MirageTank {

    private static class RgbData {
        double alpha;

        double red;

        double green;

        double blue;

        public RgbData(double red, double green, double blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public RgbData(double alpha, double red, double green, double blue) {
            this.alpha = alpha;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public int toRGB() {
            int newPixel = 0;
            newPixel += (int) alpha;
            newPixel = newPixel << 8;
            newPixel += (int) red;
            newPixel = newPixel << 8;
            newPixel += (int) green;
            newPixel = newPixel << 8;
            newPixel += (int) blue;

            return newPixel;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedImage outside = ImageIO.read(new File("pic/outside.jpg"));
        BufferedImage inside = ImageIO.read(new File("pic/inside.jpg"));

        RgbData[][] outsidePixels = getPixels(outside, MirageTank::gray);

        RgbData[][] insidePixels = getPixels(inside, rgbData -> {
            RgbData gray = gray(rgbData);
            gray.red *= 0.3;
            gray.green *= 0.3;
            gray.blue *= 0.3;

            return gray;
        });

        RgbData[][] resultPixels = new RgbData[outsidePixels.length][outsidePixels[0].length];
        for (int i = 0; i < resultPixels.length; i++) {
            for (int j = 0; j < resultPixels[i].length; j++) {
                /*
                double a = 255.0 - insidePixels[i][j] + outsidePixels[i][j];
                a = Math.max(0, Math.min(a, 255.0));
                double r = outsidePixels[i][j] / a * 255.0;
                r = Math.min(r, 255.0);
                int rInt = (int) r;
                resultPixels[i][j] = colorToRGB((int) a, rInt, rInt, rInt);

                 */
                /*
                double a = 1 - insidePixels[i][j].red + outsidePixels[i][j].red;
                double r = outsidePixels[i][j].red / a;
                resultPixels[i][j] = new RgbData(a, r, r, r);

                 */
                resultPixels[i][j] = mix(insidePixels[i][j], outsidePixels[i][j]);
            }
        }

        BufferedImage result = toImage(resultPixels);
        ImageIO.write(result, "png", new File("pic/result.png"));

    }

    private static RgbData mix(RgbData inside, RgbData outside) {
        double a = 255.0 - outside.red + inside.red;
        a = Math.min(a, 255);
        a = Math.max(0, a);

        double r = inside.red / a * 255.0;
        r = Math.min(r, 255);
        return new RgbData(a, r, r, r);
    }

    private static RgbData gray(RgbData rgbData) {
        final double r = rgbData.red;
        final double g = rgbData.green;
        final double b = rgbData.blue;

        double gray = 0.222 * r + 0.707 * g + 0.071 * b;

        return new RgbData(0, gray, gray, gray);
    }

    /**
     *  颜色分量转换为RGB值
     */
    private static int colorToRGB(int alpha, int red, int green, int blue) {

        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red;
        newPixel = newPixel << 8;
        newPixel += green;
        newPixel = newPixel << 8;
        newPixel += blue;

        return newPixel;

    }

    private static RgbData[][] getPixels(BufferedImage image, Function<RgbData, RgbData> rgbHandler) {
        int w = image.getWidth();
        int h = image.getHeight();

        RgbData[][] pixels = new RgbData[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int color = image.getRGB(j, i);
                final int r = (color >> 16) & 0xff;
                final int g = (color >> 8) & 0xff;
                final int b = color & 0xff;
                RgbData rgbData = new RgbData(r, g, b);
                pixels[i][j] = rgbHandler.apply(rgbData);
            }
        }

        return pixels;
    }

    private static BufferedImage toImage(RgbData[][] pixels) {
        int h = pixels.length;
        int w = pixels[0].length;

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                image.setRGB(j, i, pixels[i][j].toRGB());
            }
        }

        return image;
    }

    private static void toGrey(BufferedImage whiteImage, float whiteLight, BufferedImage blackImage, float blackLight) {


    }


}


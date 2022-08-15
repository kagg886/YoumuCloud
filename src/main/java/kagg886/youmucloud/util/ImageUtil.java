package kagg886.youmucloud.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

public class ImageUtil {

    public static BufferedImage scaleImg(BufferedImage image, int width, int height) {
        Image scaledImg = image.getScaledInstance(width, height, 2);
        BufferedImage img = new BufferedImage(width, height, 1);
        img.getGraphics().drawImage(scaledImg, 0, 0, (ImageObserver)null);
        return img;
    }

    public static float[][][][] imageToMatrix(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width * height];
        PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);

        try {
            pg.grabPixels();
        } catch (InterruptedException var9) {
            var9.printStackTrace();
        }

        float[][][][] ret = new float[1][pg.getHeight()][pg.getWidth()][3];
        int row = 0;
        int col = 0;

        while(row * width + col < pixels.length) {
            int pixel = row * width + col;
            ret[0][row][col][2] = (float)(pixels[pixel] & 255) / 255.0F;
            ret[0][row][col][1] = (float)(pixels[pixel] >> 8 & 255) / 255.0F;
            ret[0][row][col][0] = (float)(pixels[pixel] >> 16 & 255) / 255.0F;
            ++col;
            if (col == width - 1) {
                col = 0;
                ++row;
            }
        }

        return ret;
    }

    public static String ImageToLink(BufferedImage image,String name,String suffix) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(Statics.data_dir + "imgcache/").append(name);
        if (suffix != null) {
            builder.append("_");
            builder.append(suffix);
        }
        builder.append(".jpg");
        ImageIO.write(image,"PNG",new File(builder.toString()));
        if (suffix != null) {
            return "http://" + Statics.ip + "/youmu/Image?id=" + name + "_" + suffix;
        } else {
            return "http://" + Statics.ip +  "/youmu/Image?id=" + name;
        }
    }

    public static String ImageToLink(BufferedImage image,String prefix) throws IOException {
        return ImageToLink(image,prefix,String.valueOf(Utils.random.nextInt()));
    }

    public static BufferedImage rotate(BufferedImage image,double xita) {
        int rx0 = image.getWidth() / 2;
        int ry0 = image.getHeight() / 2;
        //在直角坐标系里求一个坐标(x,y)以定点(a,b)为圆心顺时针旋转θ度后的坐标
        BufferedImage blank = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < 640; x++) {
            for (int y = 0; y < 640; y++) {
                int x0 = (int) ((x - rx0) * Math.cos(xita) - (y - ry0) * Math.sin(xita) + rx0);
                int y0 = (int) ((x - rx0) * Math.sin(xita) + (y - ry0) * Math.cos(xita) + ry0);
                try {
                    blank.setRGB(x0,y0,image.getRGB(x,y));
                } catch (Exception ignored) {

                }
            }
        }
        return blank;
    }

    public static BufferedImage SquareToCircle(BufferedImage image) {
        int rx0 = image.getWidth() / 2;
        int ry0 = image.getHeight() / 2;
        int r2 = (int) Math.pow(rx0,2);
        //计算正方形的内切圆圆心坐标和半径
        BufferedImage blank = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (Math.pow(x - rx0,2) + Math.pow(y - ry0,2) <= r2) {
                    try {
                        blank.setRGB(x,y,image.getRGB(x,y));
                    } catch (Exception ignored) {

                    }
                }
            }
        }

        return blank;
    }
}

package kagg886.youmucloud.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtil {
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

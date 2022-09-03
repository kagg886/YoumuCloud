package kagg886.youmucloud.util.tank;

import java.awt.image.BufferedImage;

/**
 * 幻影坦克入口类
 *
 * @author knine
 * @date 2022/3/29 21:53
 **/
public class MirageTank {

    private final GreyMirageTank greyMirageTank = new GreyMirageTank();

    /**
     * 输出灰度版本的幻影坦克
     * */
    public BufferedImage outputGrey(BufferedImage outside, BufferedImage inside, GreyParams greyParams) {
        return greyMirageTank.output(outside, inside, greyParams);
    }
}

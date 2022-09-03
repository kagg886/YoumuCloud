package kagg886.youmucloud.util.tank;

import java.awt.image.BufferedImage;

/**
 *
 * 灰色版本的幻影坦克
 * @author knine
 * @date 2022/3/28 22:43
 **/
public class GreyMirageTank {

    /**
     * 输出幻影坦克合成图
     *
     * @param inside 预处理过的里图
     * @param outside 预处理过的表图
     * @param greyParams 灰色版本的相关参数
     * @return 幻影坦克合成图
     * */
    public BufferedImage output(BufferedImage outside, BufferedImage inside, GreyParams greyParams) {
        RgbData[][] outsidePixels = TankUtil.getPixels(outside,
                rgbData -> {
                    TankUtil.gray(rgbData);
                    rgbData.red *= greyParams.outsideLight;
                    rgbData.green *= greyParams.outsideLight;
                    rgbData.blue *= greyParams.outsideLight;
                    return rgbData;
                });

        RgbData[][] insidePixels = TankUtil.getPixels(inside,
                rgbData -> {
                    TankUtil.gray(rgbData);
                    rgbData.red *= greyParams.insideLight;
                    rgbData.green *= greyParams.insideLight;
                    rgbData.blue *= greyParams.insideLight;
                    return rgbData;
                });

        RgbData[][] resultPixels = new RgbData[outsidePixels.length][outsidePixels[0].length];
        for (int i = 0; i < resultPixels.length; i++) {
            for (int j = 0; j < resultPixels[i].length; j++) {
                resultPixels[i][j] = mix(insidePixels[i][j], outsidePixels[i][j]);
            }
        }

        return TankUtil.toImage(resultPixels);
    }

    private static RgbData mix(RgbData inside, RgbData outside) {
        double a = 255.0 - outside.red + inside.red;
        a = Math.min(a, 255);
        a = Math.max(0, a);

        double r = inside.red / a * 255.0;
        r = Math.min(r, 255);
        return new RgbData(a, r, r, r);
    }
}
















































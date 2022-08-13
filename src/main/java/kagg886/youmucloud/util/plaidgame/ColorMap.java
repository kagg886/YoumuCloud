package kagg886.youmucloud.util.plaidgame;

import kagg886.youmucloud.util.Utils;

import java.awt.*;
import java.util.Random;

public class ColorMap {
    private final int[] answers = new int[2];
    private final Plaid plaid;

    public ColorMap(int rank) {
        //rank为10-64
        //则格子为3-13
        //minPY为3,30
        //maxPY为10,50
        if (rank > 64) {
            rank = 64;
        }

        int r,g,b,plaids,minPY,maxPY,r1,g1,b1;
        //随机取色
        r = Utils.random.nextInt(255);
        g = Utils.random.nextInt(255);
        b = Utils.random.nextInt(255);
        //根据难度选格子数
        plaids = (int) (-5 * Math.sin(0.0581776417331 * rank + 0.314814814815 * Math.PI) + 8);

        //画图并规划颜色偏移范围
        plaid = new Plaid(plaids,plaids,30,new Color(r,g,b));
        minPY = (int) (0.00925925925926 * Math.pow(rank - 64,2) + 3);
        maxPY = (int) (0.0137174211248 * Math.pow(rank - 64,2) + 10);
        r1 = Float(r,minPY,maxPY);
        g1 = Float(g,minPY,maxPY);
        b1 = Float(b,minPY,maxPY);

        //填充对应颜色
        answers[0]= Utils.random.nextInt(plaid.getWidth()) + 1;
        answers[1] = Utils.random.nextInt(plaid.getHeight()) + 1;
        plaid.setMargin(answers[0],answers[1],new Color(r1,g1,b1));
    }

    public int[] getAnswers() {
        return answers;
    }

    public Plaid getPlaid() {
        return plaid;
    }

    private int Float(int r, int minPY, int maxPY) {
        int r1 = r + (Utils.random.nextInt(maxPY) + minPY) * (int) Math.pow(-1,Utils.random.nextInt());
        if (r1 > 255 || r1 < 0) {
            return Float(r,minPY,maxPY);
        }
        return r1;
    }
}

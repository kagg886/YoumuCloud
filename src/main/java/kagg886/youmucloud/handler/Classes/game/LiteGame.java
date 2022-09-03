package kagg886.youmucloud.handler.Classes.game;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.Message.MsgSpawner;
import kagg886.youmucloud.handler.MsgHandle;
import kagg886.youmucloud.util.ImageUtil;
import kagg886.youmucloud.util.Statics;
import kagg886.youmucloud.util.Utils;
import kagg886.youmucloud.util.WaitService;
import kagg886.youmucloud.util.plaidgame.ColorMap;
import org.json.JSONException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LiteGame extends MsgHandle {

    interface GameRunnable extends Runnable {
        long getOwner();
    }

    private BufferedImage red,blue,green;

    public LiteGame() {
        try {
            red = ImageIO.read(new File(Statics.data_dir + "/res/ufo/red.png"));
            blue = ImageIO.read(new File(Statics.data_dir + "/res/ufo/blue.png"));
            green = ImageIO.read(new File(Statics.data_dir + "/res/ufo/green.png"));
        } catch (IOException ignored) {}
    }

    @Override
    public void handle(GroupMsgPack pack) throws Exception {
        String text = pack.getMessage().getTexts();
        final long qq = pack.getMember().getUin();

        if (WaitService.hasKey(qq + "_plaid")) {
            if (text.matches("\\d+,\\d+")) { //5,4
                if (WaitService.addCall(qq + "_plaid",text)) {
                    sendMsg(pack,"选择成功,选项为:" + text,"\n等待游戏结算...");
                } else {
                    sendMsg(pack,"选择失败");
                }
            }
        }

        if (text.equals(".gm ufo")) {
            if (ScoreStatis.exps.optInt(String.valueOf(qq)) < 30) {
                sendMsg(pack,"至少拥有30exp才可使用此小游戏!");
                return;
            }

            if (ScoreStatis.exps.optInt(String.valueOf(qq)) > 3000) {
                sendMsg(pack,"[防内卷小贴士]:检测到您的积分数大于3000,已自动关闭游戏!");
                return;
            }

            MsgCollection c = MsgSpawner.newAtToast(qq,"抽取结果为:\n");
            int score = 0;
            for (int i = 0; i < 3; i++) {
                int type = Utils.random.nextInt(3);
                switch (type) {
                    case 0:
                        c.putImage(ImageUtil.ImageToLink(red,"red",null));
                        score += 10;
                        break;
                    case 2:
                        c.putImage(ImageUtil.ImageToLink(blue,"blue",null));
                        score -= 15;
                        break;
                    case 1:
                        c.putImage(ImageUtil.ImageToLink(green,"green",null));
                        score += 5;
                        break;
                }
            }
            //避免无用IO
            if (score != 0) {
                ScoreStatis.exps.put(String.valueOf(qq),ScoreStatis.exps.optInt(String.valueOf(qq)) + score);
                ScoreStatis.exps.save();
            }
            c.putText("\n获得");
            c.putText(score + "点exp!");
            pack.getGroup().sendMsg(c);
        }

        if (text.startsWith(".gm plaid")) {
            for (Runnable r : Utils.service.getQueue()) {
                try {
                    GameRunnable rb = (GameRunnable) r;
                    if (rb.getOwner() == pack.getMember().getUin()) {
                        sendMsg(pack,"你已在游戏中,请勿重复加入!");
                        return;
                    }
                } catch (Exception ignored) {}
            }

            if (ScoreStatis.exps.optInt(String.valueOf(qq)) > 2000) {
                sendMsg(pack,"[防内卷小贴士]:检测到您的积分数大于2000,已自动关闭游戏!");
                return;
            }
            Utils.service.submit(new GameRunnable() {

                @Override
                public long getOwner() {
                    return pack.getMember().getUin();
                }

                @Override
                public void run() {
                    int round = 0;
                    int rank = 0;
                    while (true) {
                        round++;
                        rank += (Utils.random.nextInt(3) + 1);
                        int sec = (int)Math.ceil(19 + 6.36619772368 * Math.atan(0.3 * rank - 8));
                        ColorMap color = new ColorMap(rank);
                        MsgCollection col = MsgSpawner.newAtToast(qq,
                                "第" + round + "轮:(rank:",rank + ")",
                                "\n找出与周围颜色不同的色块坐标(行先列后),然后直接发送坐标即可",
                                "\n例如:5,3(英文逗号不是中文的,不然无法识别)",
                                "\n你有", sec + "s的时间选择");
                        try {
                            col.putImage(ImageUtil.ImageToLink(color.getPlaid().getImage(), "pl"));
                        } catch (IOException ignored) {}
                        pack.getGroup().sendMsg(col);


                        String call = WaitService.wait(qq + "_plaid",sec);
                        if (call == null) {
                            sendMsg(pack,"超时发送,游戏结束!");
                            return;
                        }

                        int[] result = new int[2]; //玩家输入
                        result[0] = Integer.parseInt(call.split(",")[0]);
                        result[1] = Integer.parseInt(call.split(",")[1]);

                        if (result[0] == color.getAnswers()[0] && result[1] == color.getAnswers()[1]) {
                            int add = (int) (1 / (0.0833333333333 + Math.pow(Math.E,-0.0699301512293 * rank - 1.38629436112)));
                            int score = ScoreStatis.exps.optInt(String.valueOf(qq));
                            score += add;
                            try {
                                ScoreStatis.exps.put(String.valueOf(qq),score);
                            } catch (JSONException ignored) {}
                            ScoreStatis.exps.save();

                            //胜利,进入下一局
                            sendMsg(pack,"答案正确~\n","奖励" + add + "积分\n请稍等片刻...");

                            if (score > 2000) {
                                sendMsg(pack,"[防内卷小贴士]:检测到您的积分数大于2000,已自动关闭游戏!");
                                break;
                            }
                            continue;
                        }
                        sendMsg(pack,"肥肠抱歉,您猜错了~\n","正确答案应为:",color.getAnswers()[0] + "," + color.getAnswers()[1],"\n本局游戏结束!您坚持了",round + "轮");
                        break;
                    }
                }
            });
        }
    }
}

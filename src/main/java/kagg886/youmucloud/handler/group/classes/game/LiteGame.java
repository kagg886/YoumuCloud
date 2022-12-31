package kagg886.youmucloud.handler.group.classes.game;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.Message.MsgSpawner;
import kagg886.youmucloud.handler.group.GroupMsgHandle;
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
import java.util.concurrent.ArrayBlockingQueue;

public class LiteGame extends GroupMsgHandle {

    interface GameRunnable extends Runnable {
        long getOwner();
    }

    private BufferedImage red, blue, green;

    private ArrayBlockingQueue<GroupMsgPack> packs = new ArrayBlockingQueue<>(10);


    public LiteGame() {
        try {
            red = ImageIO.read(new File(Statics.data_dir + "/res/ufo/red.png"));
            blue = ImageIO.read(new File(Statics.data_dir + "/res/ufo/blue.png"));
            green = ImageIO.read(new File(Statics.data_dir + "/res/ufo/green.png"));
        } catch (IOException ignored) {
        }
        Utils.service.execute(() -> {
            Utils.log("debug", "老婆服务已启动");
            while (true) {
                try {
                    GroupMsgPack pack1 = packs.take();
                    GroupMsgPack pack2 = packs.take();
                    sendWife(pack1, pack2);
                    sendWife(pack2, pack1);
                } catch (InterruptedException ignored) {
                }
            }
        });
    }

    public void sendWife(GroupMsgPack pack1, GroupMsgPack pack2) {
        MsgCollection col1 = MsgSpawner.newAtToast(pack1.getMember().getUin(), "你的老婆是:");
        col1.putImage("https://q1.qlogo.cn/g?b=qq&nk=" + pack2.getMember().getUin() + "&s=640");
        col1.putText(String.format("群 %s(%d) 的 %s(%d)", pack2.getGroup().getName(), pack2.getGroup().getId(), pack2.getMember().getNick(), pack2.getMember().getUin()));
        pack1.getGroup().sendMsg(col1);
    }

    @Override
    public void handle(GroupMsgPack pack) throws Exception {
        String text = pack.getMessage().getTexts();
        final long qq = pack.getMember().getUin();

        if (WaitService.hasKey(qq + "_plaid")) {
            if (text.matches("\\d+,\\d+")) { //5,4
                if (WaitService.addCall(qq + "_plaid", text)) {
                    sendMsg(pack, "选择成功,选项为:" + text, "\n等待游戏结算...");
                } else {
                    sendMsg(pack, "选择失败");
                }
            }
        }

        if (text.startsWith(".gm wife")) {
            for (GroupMsgPack pack1 : packs) {
                if (pack1.getMember().getUin() == pack.getMember().getUin()) {
                    sendMsg(pack, "请耐心等待自己的只因");
                    return;
                }
            }
            sendMsg(pack, "已插入队列!\n当有另一个人使用此指令时bot将会通知你");
            packs.offer(pack);
        }

        if (text.equals(".gm ufo")) {

            if (ScoreStatis.exps.optInt(String.valueOf(qq)) < 30) {
                sendMsg(pack, "至少拥有30exp才可使用此小游戏!");
                return;
            }

            if (ScoreStatis.exps.optInt(String.valueOf(qq)) > 3000) {
                sendMsg(pack, "[防土块小贴士]:检测到您的积分数大于3000,已自动关闭游戏!");
                return;
            }

            MsgCollection c = MsgSpawner.newAtToast(qq, "抽取结果为:\n");
            int score = 0;
            for (int i = 0; i < 3; i++) {
                int type = Utils.random.nextInt(3);
                switch (type) {
                    case 0:
                        c.putImage(ImageUtil.ImageToLink(red, "red", null));
                        score += 10;
                        break;
                    case 2:
                        c.putImage(ImageUtil.ImageToLink(blue, "blue", null));
                        score -= 15;
                        break;
                    case 1:
                        c.putImage(ImageUtil.ImageToLink(green, "green", null));
                        score += 5;
                        break;
                }
            }

            int lucky = Utils.random.nextInt(100);
            if (lucky == 1) {
                score -= 30;
                c.putText("\n[突发事件]:你被1up撅了，积分-30");
            } else if (lucky == 2) {
                score += 30;
                c.putText("\n[突发事件]:你把1up撅了，积分+30");
            } else if (lucky == 3) {
                score = 0;
                c.putText("\n[突发事件]:你开出的碟跑了，本次积分清0");
            } else if (lucky == 4) {
                c.putText("\n[突发事件]:你碟开错了，然而你靠着你的无敌底力躲过去了");
            } else if (lucky == 5) {
                score -= 15;
                c.putText("\n[突发事件]:你一不小心撞到飞碟了，积分-15");
            } else if (lucky == 6) {
                score += 15;
                c.putText("\n[突发事件]:你开的碟炸了，积分+15");
            } else if (lucky == 7) {
                c.putText("[突发事件]:你获得了114514点积分，可惜是假的");
            }

            //避免无用IO
            if (score != 0) {
                ScoreStatis.exps.put(String.valueOf(qq), ScoreStatis.exps.optInt(String.valueOf(qq)) + score);
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
                        sendMsg(pack, "你已在游戏中,请勿重复加入!");
                        return;
                    }
                } catch (Exception ignored) {
                }
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
                        int sec = (int) Math.ceil(19 + 6.36619772368 * Math.atan(0.3 * rank - 8));
                        ColorMap color = new ColorMap(rank);
                        MsgCollection col = MsgSpawner.newAtToast(qq,
                                "第" + round + "轮:(rank:", rank + ")",
                                "\n找出与周围颜色不同的色块坐标(行先列后),然后直接发送坐标即可",
                                "\n例如:5,3(英文逗号不是中文的,不然无法识别)",
                                "\n你有", sec + "s的时间选择");
                        try {
                            col.putImage(ImageUtil.ImageToLink(color.getPlaid().getImage(), "pl"));
                        } catch (IOException ignored) {
                        }
                        pack.getGroup().sendMsg(col);


                        String call = WaitService.wait(qq + "_plaid", sec);
                        if (call == null) {
                            sendMsg(pack, "超时发送,游戏结束!");
                            return;
                        }

                        int[] result = new int[2]; //玩家输入
                        result[0] = Integer.parseInt(call.split(",")[0]);
                        result[1] = Integer.parseInt(call.split(",")[1]);

                        if (result[0] == color.getAnswers()[0] && result[1] == color.getAnswers()[1]) {
                            int score = ScoreStatis.exps.optInt(String.valueOf(qq));
                            int add = (int) (1 / (0.0833333333333 + Math.pow(Math.E, -0.0699301512293 * rank - 1.38629436112)));
                            if (add > 2000) {
                                add = 0;
                            }
                            score += add;
                            try {
                                ScoreStatis.exps.put(String.valueOf(qq), score);
                            } catch (JSONException ignored) {
                            }
                            ScoreStatis.exps.save();

                            //胜利,进入下一局
                            sendMsg(pack, "答案正确~\n", "奖励" + add + "积分\n请稍等片刻...");
                            continue;
                        }
                        sendMsg(pack, "肥肠抱歉,您猜错了~\n", "正确答案应为:", color.getAnswers()[0] + "," + color.getAnswers()[1], "\n本局游戏结束!您坚持了", round + "轮");
                        break;
                    }
                }
            });
        }
    }
}

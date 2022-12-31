package kagg886.youmucloud.handler.group.classes;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.Message.MsgSpawner;
import kagg886.youmucloud.handler.group.GroupMsgHandle;
import kagg886.youmucloud.util.Mail;
import kagg886.youmucloud.util.Statics;
import kagg886.youmucloud.util.Utils;
import kagg886.youmucloud.util.WaitService;
import kagg886.youmucloud.util.cache.JSONArrayStorage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Help extends GroupMsgHandle {

    private String[] rootCommands;

    private ArrayList<Long> xpStatus = new ArrayList<>();

    private JSONArrayStorage chara, xp;

    public Help() {
        rootCommands = new File(Statics.data_dir + "/static/c").list();
        chara = JSONArrayStorage.obtain("res/xps/chara.json");
        xp = JSONArrayStorage.obtain("res/xps/xp.json");
    }

    @Override
    public void handle(GroupMsgPack pack) {
        String text = pack.getMessage().getTexts();
        long qq = pack.getMember().getUin();
        if (WaitService.hasKey(qq + "_commit")) {
            if (WaitService.addCall(qq + "_commit", pack.toString())) {
                sendMsg(pack, "反馈正在提交，请稍等...");
            }
        }

        if (text.equals(".hso 2022")) {
            if (Calendar.getInstance().get(Calendar.DAY_OF_YEAR) != 365) {
                sendMsg(pack, "此命令未开放");
                return;
            }

            if (xpStatus.contains(pack.getMember().getUin())) {
                sendMsg(pack, "你已经生成过了哦(");
                return;
            }
            xpStatus.add(pack.getMember().getUin());
            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "2022年，是一个特别的一年。\n");
            long k = Utils.random.nextInt(1000000);
            col.putText("你一共被中出了" + k + "次，每天肉棒会进出你的身体" + (k / 365) + "次。\n");
            col.putText("您一共打了" + Utils.random.nextInt(10000) + "次胶，超越了全国87%的人\n");
            col.putText("共看了" + Utils.random.nextInt(100000000) + "张涩图，常出现的角色有");
            for (int i = 0; i < 5; i++) {
                col.putText(chara.optString(Utils.random.nextInt(chara.length())));
                if (i != 4) {
                    col.putText(",");
                }
            }
            col.putText("\n");
            col.putText("共玩了" + Utils.random.nextInt(1000) + "部黄油，平均每部时长" + Utils.random.nextInt(24) + "." + Utils.random.nextInt(10) + "h\n");
            col.putText("您一共有0次性生活，超越了全国99.999999%的同龄人，哈哈哈哈。\n");
            k = Utils.random.nextInt(10) + 1;
            col.putText("你热衷于援交，喜欢" + k + "-" + (k + Utils.random.nextInt(1000)) + "cm左右的肉棒，最喜欢的温度是" + (Utils.random.nextInt(10) + 35) + "." + Utils.random.nextInt(10) + "℃\n");
            col.putText("你热爱发送自己的福利照片到各种平台上，人们总是称你菩萨。今年你的照片收获了" + Utils.random.nextInt(10000) + "万次的收藏，获得了" + Utils.random.nextInt(10000) + "w的订阅\n");
            k = (long) (System.currentTimeMillis() - (31536000000L * Math.random()));
            SimpleDateFormat f = new SimpleDateFormat("MM.dd");
            col.putText(f.format(k) + "大概是很特别的一天。\n");
            col.putText("在这一天里你找了" + Utils.random.nextInt(19) + 1 + "个黄毛一起开银趴，全身抹满了脱氧核糖。第二天你走路脚都是崴的。\n");
            k = (long) (System.currentTimeMillis() - (31536000000L * Math.random()));
            col.putText(f.format(k) + "这天你睡得很晚。\n");
            col.putText((Utils.random.nextInt(6) + 1) + "." + (Utils.random.nextInt(58) + 1) + "分还在被后入，那一刻你就像是魅魔在世一样完全停不下来。\n");
            col.putText("你的年度标签是");
            for (int i = 0; i < 5; i++) {
                col.putText(xp.optString(Utils.random.nextInt(xp.length())));
                if (i != 4) {
                    col.putText(",");
                }
            }
            col.putText("\n");
            col.putText("这一年你努力工作了365天，大家都很喜欢你，明年要更加努力哦!");
            pack.getGroup().sendMsg(col);
        }

        if (text.equals(".commit")) {
            sendMsg(pack, "请在一分钟内将你要反馈给作者的消息发送出去\n妖梦云感谢您的反馈");
            Utils.service.execute(() -> {
                GroupMsgPack recv = null;
                try {
                    recv = new GroupMsgPack(WaitService.wait(qq + "_commit", 60));
                    MsgCollection p = recv.getMessage();
                    Mail.sendMessage("iveour@163.com", String.format("%s(%d)——%s(%d)", recv.getGroup().getName(), recv.getGroup().getId(), recv.getMember().getUinName(), recv.getMember().getUin()), p);
                    sendMsg(recv, "反馈成功!");
                } catch (Exception ignored) {
                    sendMsg(recv, "反馈时发生错误!");
                }
            });
            return;
        }

        if (text.equals(".menu")) {
            sendMsg(pack, "指令集已迁移,请打开下列网址查看:\nhttp://" + Statics.ip + "/youmu/text?path=commandList");
            return;
        }

        if (text.equals(".help")) {
            sendMsg(pack, "综合性工具类机器人,立志为使用者带来绝妙的体验\n" + "官网:http://" + Statics.ip + "/youmu/HomePage\n" + "\n反馈请使用\".commit命令\"" + "插件问答群:572360632\n" + "东方群(有音游浓度):973510746");
            return;
        }

        if (text.startsWith(".") && text.split(" ").length == 1) {
            for (String name : rootCommands) {
                if (text.substring(1).startsWith(name)) {
                    sendMsg(pack, "指令集已迁移,请打开下列网址查看:\nhttp://" + Statics.ip + "/youmu/text?path=c/" + text.substring(1));
                }
            }
        }
    }
}

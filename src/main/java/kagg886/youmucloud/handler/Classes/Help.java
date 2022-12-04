package kagg886.youmucloud.handler.Classes;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.youmucloud.handler.MsgHandle;
import kagg886.youmucloud.util.Mail;
import kagg886.youmucloud.util.Statics;
import kagg886.youmucloud.util.Utils;
import kagg886.youmucloud.util.WaitService;

import java.io.File;

public class Help extends MsgHandle {

    private String[] rootCommands;

    public Help() {
        rootCommands = new File(Statics.data_dir + "/static/c").list();
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

        if (text.equals(".commit")) {
            sendMsg(pack, "请在一分钟内将你要反馈给作者的消息发送出去\n妖梦云感谢您的反馈");
            Utils.service.execute(new Runnable() {
                @Override
                public void run() {
                    GroupMsgPack recv = null;
                    try {
                        recv = new GroupMsgPack(WaitService.wait(qq + "_commit", 60));
                        MsgCollection p = recv.getMessage();
                        Mail.sendMessage("iveour@163.com", String.format("%s(%d)——%s(%d)", recv.getGroup().getName(), recv.getGroup().getId(), recv.getMember().getUinName(), recv.getMember().getUin()), p);
                        sendMsg(recv, "反馈成功!");
                    } catch (Exception ignored) {
                        sendMsg(recv, "反馈时发生错误!");
                    }
                }
            });
            return;
        }

        if (text.equals(".menu")) {
            sendMsg(pack, "指令集已迁移,请打开下列网址查看:\nhttp://" + Statics.ip + "/youmu/text?path=commandList");
            return;
        }

        if (text.equals(".help")) {
            sendMsg(pack, "综合性工具类机器人,立志为使用者带来绝妙的体验\n"
                    + "官网:http://" + Statics.ip + "/youmu/HomePage\n"
                    + "\n反馈请使用\".commit命令\""
                    + "插件问答群:572360632\n"
                    + "东方群(有音游浓度):973510746");
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

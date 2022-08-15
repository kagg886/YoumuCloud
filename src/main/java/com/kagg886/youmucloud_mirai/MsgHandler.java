package com.kagg886.youmucloud_mirai;

import com.kagg886.youmucloud_mirai.QI.SessionBot;
import com.kagg886.youmucloud_mirai.websocket.Action;
import com.kagg886.youmucloud_mirai.websocket.Connection;
import com.kagg886.youmucloud_mirai.websocket.MessageCenter;
import kagg886.qinternet.Content.Group;
import kagg886.qinternet.Content.Member;
import kagg886.qinternet.Content.Person;
import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.QInternet;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.java_websocket.WebSocket;
import org.java_websocket.enums.ReadyState;
import org.json.JSONObject;
import java.net.URI;
import java.util.HashMap;
import java.util.Objects;

public class MsgHandler implements ListenerHost {

    @EventHandler
    public void onGroupMessage(GroupMessageEvent event) {
        SessionBot bot = ((SessionBot) QInternet.findBot(event.getBot().getId()));
        if (bot == null) {
            return;
        }
        Group g = new Group(bot, event.getGroup().getId(), event.getGroup().getName());
        Member m = new Member(bot,
                event.getGroup().getId(),
                event.getSender().getId(),
                event.getSenderName(), 0, Person.Sex.BOY, "幻想乡", event.getSenderName(), Member.Permission.MEMBER);
        MsgCollection c = MessageCenter.MIRAITOQI(event.getMessage());
        GroupMsgPack gp = new GroupMsgPack(g, m, c);
        Action o = Action.newAction("onGroupMsg");
        o.setMsg(gp.toString());
        bot.getConnection().send(o.toString());
    }


    @EventHandler
    public void onBotOnline(BotOnlineEvent event) {
        MessageCenter.sendLog(MessageCenter.Logger.Client, event.getBot().getId() + "准备连接...");
        initServer(true, event.getBot().getId());
    }

    @EventHandler
    public void onBotOffline(BotOfflineEvent event) {
        if (event instanceof BotOfflineEvent.Dropped) {
            return;
        }
        SessionBot bot = ((SessionBot) QInternet.findBot(event.getBot().getId()));
        Objects.requireNonNull(bot).getConnection().close();
        QInternet.removeBot(bot);
        MessageCenter.sendLog(MessageCenter.Logger.Client, event.getBot().getId() + "自动下线,连接已断开");
    }

    public synchronized static void initServer(boolean isFirst, long id) {
        if (!isFirst) {
            MessageCenter.sendLog(MessageCenter.Logger.Client, "正在重连...");
        }
        HashMap<String, String> map = new HashMap<>();
        JSONObject object = new JSONObject(YoumuConfig.INSTANCE.getHeader());
        try {
            object.put("ver", PluginInstance.vercode);
            object.put("platform", "Mirai_console");
        } catch (Exception ignored) {
        }
        map.put("json", object.toString());

        Connection server = new Connection(URI.create(YoumuConfig.INSTANCE.getServerAddress() + id), id, map);
        server.setConnectionLostTimeout(0);
        server.connect();
        int a = 0;
        while (server.getReadyState() != ReadyState.OPEN && a < 30) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
            a++;
            MessageCenter.sendLog(MessageCenter.Logger.Client, "连接中...(" + a + "秒)");
        }

        if (a == 30) {
            server.close();
            MessageCenter.sendLog(MessageCenter.Logger.Client, "重连失败!");
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {
                }
                initServer(false, id);
            }).start();
            return;
        }
        if (QInternet.findBot(id) != null) {
            ((SessionBot) Objects.requireNonNull(QInternet.findBot(id))).setConnection(server);
        } else {
            QInternet.addBot(new SessionBot(id, server));
        }
        MessageCenter.sendLog(MessageCenter.Logger.Client, "YoumuCloud初始化完成！");
    }
}

package com.kagg886.youmucloud_mirai;

import com.kagg886.youmucloud_mirai.QI.SessionBot;
import com.kagg886.youmucloud_mirai.websocket.Action;
import com.kagg886.youmucloud_mirai.websocket.Connection;
import com.kagg886.youmucloud_mirai.websocket.MessageCenter;
import kagg886.qinternet.Content.Group;
import kagg886.qinternet.Content.Member;
import kagg886.qinternet.Content.Person;
import kagg886.qinternet.Content.QQBot;
import kagg886.qinternet.Message.GroupMemberPack;
import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.QInternet;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Stranger;
import net.mamoe.mirai.contact.announcement.Announcement;
import net.mamoe.mirai.contact.announcement.AnnouncementParameters;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import org.java_websocket.WebSocket;
import org.java_websocket.enums.ReadyState;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Objects;

public class MsgHandler implements ListenerHost {

    @EventHandler
    public void onMemberEnter(MemberJoinEvent event) {
        SessionBot bot = ((SessionBot) QInternet.findBot(event.getBot().getId()));
        if (bot == null) {
            MessageCenter.sendLog(MessageCenter.Logger.Client, event.getBot().getId() + "准备连接...");
            initServer(event.getBot().getId());
            return;
        }

        if (bot.getConnection().getReadyState() != ReadyState.OPEN) {
            QInternet.removeBot(bot);
            MessageCenter.sendLog(MessageCenter.Logger.Client, event.getBot().getId() + "检测到未连接的bot，尝试重连ing...");
            initServer(event.getBot().getId());
            return;
        }
        Group g = new Group(bot, event.getGroup().getId(), event.getGroup().getName());
        Member m = new Member(bot,
                event.getGroup().getId(),
                event.getMember().getId(),
                event.getMember().getNameCard(), 0, Person.Sex.BOY, "幻想乡", event.getMember().getNick(), Member.Permission.MEMBER);

        GroupMemberPack pack = new GroupMemberPack(g, GroupMemberPack.Type.enter,m);
        Action o = Action.newAction("onMember");
        o.setMsg(pack.toString());
        bot.getConnection().send(o.toString());
    }

    @EventHandler
    public void onGroupMessage(GroupMessageEvent event) {
        SessionBot bot = ((SessionBot) QInternet.findBot(event.getBot().getId()));
        if (bot == null) {
            MessageCenter.sendLog(MessageCenter.Logger.Client, event.getBot().getId() + "准备连接...");
            initServer(event.getBot().getId());
            return;
        }
        if (bot.getConnection().getReadyState() != ReadyState.OPEN) {
            QInternet.removeBot(bot);
            MessageCenter.sendLog(MessageCenter.Logger.Client, event.getBot().getId() + "检测到未连接的bot，尝试重连ing...");
            initServer(event.getBot().getId());
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
    public void onBotOffline(BotOfflineEvent event) {
        if (event instanceof BotOfflineEvent.Dropped) {
            return;
        }
        SessionBot bot = ((SessionBot) QInternet.findBot(event.getBot().getId()));
        Objects.requireNonNull(bot).getConnection().close();
        QInternet.removeBot(bot);
        MessageCenter.sendLog(MessageCenter.Logger.Client, event.getBot().getId() + "自动下线,连接已断开");
    }

    public static void initServer(long id) {
        HashMap<String, String> map = new HashMap<>();
        JSONObject object = new JSONObject(YoumuConfig.INSTANCE.getHeader());
        try {
            object.put("ver", PluginInstance.vercode);
            object.put("platform", "Mirai_console");
        } catch (Exception ignored) {
        }
        map.put("json", Base64.getEncoder().encodeToString(object.toString().getBytes(StandardCharsets.UTF_8)));

        Connection server = new Connection(URI.create(YoumuConfig.INSTANCE.getServerAddress() + id), id, map);
        server.setConnectionLostTimeout(0);
        server.connect();
        int a = 0;
        while (server.getReadyState() != ReadyState.OPEN && a < 30) {
            for (QQBot p : QInternet.getList()) {
                SessionBot bot = (SessionBot) p;
                if (bot.getConnection().getReadyState() == ReadyState.OPEN && bot.getId() == id) {
                    return;
                }
            }
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

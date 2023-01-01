package com.kagg886.youmucloud.bot.secluded;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import androidx.preference.PreferenceManager;
import com.kagg886.youmucloud.R;
import com.kagg886.youmucloud.bot.AbstractMessageCenter;
import com.kagg886.youmucloud.bot.Action;
import com.kagg886.youmucloud.bot.BotConnection;
import com.kagg886.youmucloud.bot.SessionBot;
import com.kagg886.youmucloud.core.activity.MainActivity;
import com.kagg886.youmucloud.util.Constant;
import com.kagg886.youmucloud.util.DynamicNotification;
import kagg886.qinternet.Content.Group;
import kagg886.qinternet.Content.Member;
import kagg886.qinternet.Content.Person;
import kagg886.qinternet.Content.QQBot;
import kagg886.qinternet.Message.GroupMemberPack;
import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.QInternet;
import mcsq.nxa.secluded.msg.Messenger;
import mcsq.nxa.secluded.msg.Msg;
import mcsq.nxa.secluded.plugin.PluginBinder;
import mcsq.nxa.secluded.plugin.PluginBinderHandler;

public class PluginService extends PluginBinder implements PluginBinderHandler {
    public static PluginService INSTANCE;

    private DynamicNotification notification;

    private int receive;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Constant.VERSION = getPackageManager().getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("keepAlive",true)) {
            notification = new DynamicNotification(this, DynamicNotification.Platform.Secluded);
            startForeground(notification.getPlatform().getId(), notification.build("YoumuCloud已运行\n平台:Secluded"));
        }
    }

    @Override
    public IBinder onBind(Intent i) {
        return super.newBinder(i, this);
    }

    @Override
    public void onLoad() throws RemoteException {//插件服务创建    子线程
        INSTANCE = this;
    }

    @Override
    public void onUnLoad() throws RemoteException {//插件服务断开   主线程
        for (QQBot p : QInternet.getList()) {
            SessionBot b = (SessionBot) p;
            if (b.getConn().getCenter().getPlatform().startsWith("Secluded")) {
                b.getConn().close();
            }
        }
        System.exit(0);//关闭虚拟机
    }

    @Override
    public void onMsgHandler(final Messenger msg) throws RemoteException {//消息处理 子线程
        if (notification != null) {
            receive++;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    notification.show("YoumuCloud已运行\n已接收" + receive + "条消息");
                }
            });
        }
        long bot = msg.getLong(Msg.Account);
        if (QInternet.findBot(bot) == null) {
            Connection conn = new Connection(bot, SecludedMessageCenter.INSTANCE, PreferenceManager.getDefaultSharedPreferences(this));
            conn.getCenter().sendLog(AbstractMessageCenter.LoggerLevel.Client, "监听到bot" + bot + ",准备启动连接...");
            BotConnection.init(conn, PreferenceManager.getDefaultSharedPreferences(this));
        }

        if (msg.hasMsg(Msg.GroupNewMember)) {
            SessionBot findBot2 = (SessionBot) QInternet.findBot(bot);
            Group group = new Group(findBot2, msg.getLong("GroupId"), "unknown");
            Member member = new Member(findBot2, msg.getLong("GroupId"), msg.getLong("Uin"), msg.getString("UinName"), 10, Person.Sex.BOY, "幻想乡", msg.getString("UinName"), Member.Permission.MEMBER);
            GroupMemberPack pack = new GroupMemberPack(group, GroupMemberPack.Type.enter, member);
            Action newAction = Action.newAction("onMember");
            newAction.setMsg(pack.toString());
            findBot2.getConn().send(newAction.toString());
            return;
        }

        if (msg.hasMsg(Msg.Offline)) {
            SessionBot off = (SessionBot) QInternet.findBot(bot);
            off.getConn().close();
            QInternet.removeBot(off);
            SecludedMessageCenter.INSTANCE.sendLog(AbstractMessageCenter.LoggerLevel.Client, bot + "下线,连接自动断开");
            return;
        }

        if (msg.hasMsg("Group") && msg.getLong("Account") != msg.getLong("Uin")) {
            SessionBot findBot2 = (SessionBot) QInternet.findBot(bot);
            GroupMsgPack groupMsgPack = new GroupMsgPack(new Group(findBot2, msg.getLong("GroupId"), msg.getString("GroupName")), new Member(findBot2, msg.getLong("GroupId"), msg.getLong("Uin"), msg.getString("UinName"), 10, Person.Sex.BOY, "幻想乡", msg.getString("UinName"), Member.Permission.MEMBER), SecludedMessageCenter.INSTANCE.NativeToQI(msg));
            Action newAction = Action.newAction("onGroupMsg");
            newAction.setMsg(groupMsgPack.toString());
            findBot2.getConn().send(newAction.toString());
        }
    }

    @Override
    public Bitmap icon() throws RemoteException {//插件图片 UI线程禁止耗时操作 图片格式PNG 分辨率不高于256*256 大小不超过128KB 否则跨进程传输会卡
        return BitmapFactory.decodeResource(super.getResources(), R.drawable.ic_launcher);
    }

    @Override
    public String name() throws RemoteException {//插件名称 UI线程禁止耗时操作
        return "YoumuCloud";
    }

    @Override
    public String info() throws RemoteException {//插件简介 UI线程禁止耗时操作
        return Constant.INFO;
    }

    @Override
    public String author() throws RemoteException {//插件作者 UI线程禁止耗时操作
        return Constant.AUTHOR;
    }

    @Override
    public String version() throws RemoteException {//插件版本 UI线程禁止耗时操作
        return Constant.VERSION.versionName;
    }

    @Override
    public String activity() throws RemoteException {//插件跳转
        return MainActivity.class.getName();
    }
}
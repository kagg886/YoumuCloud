package com.kagg886.youmucloud.bot.qrspeed;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.os.*;
import androidx.preference.PreferenceManager;
import com.QR.MsgApi.*;
import com.QR.aidl.AppInterface.*;
import com.kagg886.youmucloud.R;
import com.kagg886.youmucloud.bot.AbstractMessageCenter;
import com.kagg886.youmucloud.bot.Action;
import com.kagg886.youmucloud.bot.BotConnection;
import com.kagg886.youmucloud.bot.SessionBot;
import com.kagg886.youmucloud.bot.secluded.SecludedMessageCenter;
import com.kagg886.youmucloud.util.Constant;
import kagg886.qinternet.Content.Group;
import kagg886.qinternet.Content.Member;
import kagg886.qinternet.Content.Person;
import kagg886.qinternet.Content.QQBot;
import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.QInternet;

import java.io.*;
import java.util.*;

public class PluginService extends Service {

    public static PluginService INSTANCE;

    //AIDL接口实现
    private final Stub stub = new Stub() {
        @Override
        public void onMessageHandler(PluginMsg msg) throws RemoteException {
            if (msg.type == PluginMsg.TYPE_GROUP_MSG) {
                for (QQBot a : QInternet.getList()) {
                    SessionBot c = (SessionBot) a;
                    if (c.getConn() instanceof Connection) {
                        GroupMsgPack groupMsgPack = new GroupMsgPack(new Group(a,msg.groupid ,msg.groupName), new Member(a, msg.groupid,msg.uin, msg.uinName, 10, Person.Sex.BOY, "幻想乡", msg.uinName, Member.Permission.MEMBER), c.getConn().getCenter().NativeToQI(msg));
                        Action newAction = Action.newAction("onGroupMsg");
                        newAction.setMsg(groupMsgPack.toString());
                        c.getConn().send(newAction.toString());
                    }
                }
            }
        }

        /**
         * 插件相关简要信息说明
         */
        @Override
        public String info() throws RemoteException {
            return Constant.INFO;
        }

        /**
         * 插件图标
         */
        @Override
        public byte[] icon() throws RemoteException {
            Bitmap bit = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bit.compress(Bitmap.CompressFormat.PNG, 100, out);
            return out.toByteArray();
        }

        /**
         * 插件作者信息
         */
        @Override
        public String author() throws RemoteException {
            return Constant.AUTHOR;
        }
    };

    private QRConnection connection;

    public QRConnection getConnection() {
        return connection;
    }

    @Override
    public IBinder onBind(Intent intent) {
        List<ResolveInfo> ResolveInfoList = this.getPackageManager().queryIntentServices(new Intent("com.QR.Speed.service").setPackage("com.QR.Speed"), 0);
        ResolveInfo resolveInfo = ResolveInfoList.get(0);
        Intent i = new Intent();
        ComponentName component = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
        i.setComponent(component);
        connection = new QRConnection();
        try {
            stopService(i);
            startService(i);
        } catch (java.lang.IllegalStateException e) {
        }
        bindService(i, connection, Context.BIND_AUTO_CREATE);
        try {
            Constant.VERSION = getPackageManager().getPackageInfo(Constant.PKG_NAME,0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            String stat;
            long bot;

            PluginMsg pluginMsg = new PluginMsg();
            pluginMsg.type = 15;
            PluginMsg send = null;
            try {
                send = connection.service.handlerMessage(pluginMsg);
            } catch (RemoteException ignored) {}
            if (send != null) {
                bot = send.uin;
                if (send.value == 0) {
                    stat = "unVip";
                } else {
                    stat = "Vip";
                }

                if (QInternet.findBot(bot) == null) {
                    for (QQBot a : QInternet.getList()) {
                        SessionBot c = (SessionBot) a;
                        if (c.getConn() instanceof Connection) {
                            //保证只存在一个Connection
                            QInternet.removeBot(c);
                            c.getConn().close();
                        }
                    }
                    Connection conn = new Connection(bot,new QRMessageCenter(stat),PreferenceManager.getDefaultSharedPreferences(this));
                    conn.getCenter().sendLog(AbstractMessageCenter.LoggerLevel.Client, "监听到bot" + bot + ",准备启动连接...");
                    BotConnection.init(conn,PreferenceManager.getDefaultSharedPreferences(this));
                }
            }
        }).start();

        return stub;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        for (QQBot a : QInternet.getList()) {
            SessionBot c = (SessionBot) a;
            if (c.getConn() instanceof Connection) {
                //保证只存在一个Connection
                QInternet.removeBot(c);
                c.getConn().close();
            }
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        INSTANCE = this;

    }
}


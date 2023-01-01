package com.kagg886.youmucloud.bot.sqvz;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.preference.PreferenceManager;
import com.kagg886.youmucloud.R;
import com.kagg886.youmucloud.bot.AbstractMessageCenter;
import com.kagg886.youmucloud.bot.Action;
import com.kagg886.youmucloud.bot.BotConnection;
import com.kagg886.youmucloud.bot.SessionBot;
import com.kagg886.youmucloud.core.activity.MainActivity;
import com.kagg886.youmucloud.util.Constant;
import com.kagg886.youmucloud.util.DynamicNotification;
import com.setqq.script.Msg;
import com.setqq.script.sdk.IPlugin;
import com.setqq.script.sdk.PluginApiInterface;
import kagg886.qinternet.Content.Group;
import kagg886.qinternet.Content.Member;
import kagg886.qinternet.Content.Person;
import kagg886.qinternet.Content.QQBot;
import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.QInternet;

public class PluginLoader implements IPlugin {

    @Override
    public void onMessageHandler(Msg msg) {
        for (QQBot a : QInternet.getList()) {
            SessionBot c = (SessionBot) a;
            if (c.getConn() instanceof Connection) {
                GroupMsgPack groupMsgPack = new GroupMsgPack(new Group(a, msg.groupid, msg.groupName), new Member(a, msg.groupid, msg.uin, msg.uinName, 10, Person.Sex.BOY, "幻想乡", msg.uinName, Member.Permission.MEMBER), c.getConn().getCenter().NativeToQI(msg));
                Action newAction = Action.newAction("onGroupMsg");
                newAction.setMsg(groupMsgPack.toString());
                c.getConn().send(newAction.toString());
                return;
            }
        }
    }

    @Override
    public Intent getActivity(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    public Drawable getIcon(Context context) {
        return context.getDrawable(R.drawable.ic_launcher);
    }


    @Override
    public void onLoad(Context context, PluginApiInterface vzSendAPI) {
        new Thread(() -> {
            for (QQBot a : QInternet.getList()) {
                SessionBot c = (SessionBot) a;
                if (c.getConn() instanceof Connection) {
                    QInternet.removeBot(c);
                    c.getConn().close();
                }
            }
            try {
                Constant.VERSION = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
            Msg p = new Msg();
            p.type = Msg.TYPE_GET_LOGIN_ACCOUNT;
            long bot = vzSendAPI.send(p).uin;
            Connection conn = new Connection(bot, new VZMessageCenter(vzSendAPI), PreferenceManager.getDefaultSharedPreferences(context));
            conn.getCenter().sendLog(AbstractMessageCenter.LoggerLevel.Client, "监听到bot" + bot + ",准备启动连接...");
            BotConnection.init(conn, PreferenceManager.getDefaultSharedPreferences(context));
        }).start();
    }
}

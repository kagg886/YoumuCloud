package com.kagg886.youmucloud.bot.secluded;

import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.kagg886.youmucloud.bot.AbstractMessageCenter;
import com.kagg886.youmucloud.bot.Action;
import com.kagg886.youmucloud.bot.BotConnection;
import kagg886.qinternet.Message.MsgCollection;
import mcsq.nxa.secluded.msg.Messenger;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Connection extends BotConnection<Messenger,Messenger.Builder> {

    @Override
    public void onMessage(String str) {
        if (PreferenceManager.getDefaultSharedPreferences(PluginService.INSTANCE).getBoolean("outputLogs",false)) {
            center.sendLog(AbstractMessageCenter.LoggerLevel.Server, "recv->:" + str);
        }
        try {
            Action decodeAction = Action.decodeAction(str);
            String action =decodeAction.getAction();

            if (action.equals("sendGroupMsg")) {
                center.sendMsg(center.QIToNative(decodeAction.optLong("groupid"),new MsgCollection(decodeAction.optJSONArray("msg").toString())));
            }

            if (action.startsWith("getGroups")) {
                Matcher matcher = Pattern.compile("GroupId=[0-9]+").matcher(center.sendMsg(messenger -> messenger.addMsg("GroupListGet")).toString());
                JSONArray jSONArray = new JSONArray();
                while (matcher.find()) {
                    jSONArray.put(Long.parseLong(matcher.group().split("=")[1]));
                }
                decodeAction.put("callback",jSONArray.toString());
                send(decodeAction.toString());
                return;
            }

            if (action.equals("memberMute")) {
                center.sendMsg(messenger -> {
                    messenger.addMsg("GroupProhibitMember");
                    messenger.addMsg("GroupId", decodeAction.optLong("groupid"));
                    messenger.addMsg("Uin", decodeAction.optLong("person"));
                    messenger.addMsg("Time", decodeAction.optInt("time"));
                });
            }

            if (action.equals("log")) {
                center.sendLog(AbstractMessageCenter.LoggerLevel.Server, decodeAction.getMsg());
            }
        } catch (JSONException e) {
        }
    }

    public Connection(long j,AbstractMessageCenter<Messenger,Messenger.Builder> bridge,SharedPreferences sp) {
        super(j,bridge,sp);
    }
}
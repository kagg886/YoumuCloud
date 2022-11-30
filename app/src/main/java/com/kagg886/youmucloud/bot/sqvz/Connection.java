package com.kagg886.youmucloud.bot.sqvz;

import android.content.SharedPreferences;
import com.kagg886.youmucloud.bot.AbstractMessageCenter;
import com.kagg886.youmucloud.bot.Action;
import com.kagg886.youmucloud.bot.BotConnection;
import com.setqq.script.Msg;
import kagg886.qinternet.Message.MsgCollection;
import org.json.JSONException;

public class Connection extends BotConnection<Msg,Msg> {

    public Connection(long j, AbstractMessageCenter<Msg, Msg> bridge, SharedPreferences sp) {
        super(j, bridge, sp);
    }

    @Override
    public void onMessage(String str) {
        center.sendLog(AbstractMessageCenter.LoggerLevel.Client, "recv<-:" + str);
        try {
            Action decodeAction = Action.decodeAction(str);
            String action = decodeAction.getAction();
            if (action.equals("sendGroupMsg")) {
                Msg msg = center.QIToNative(decodeAction.optLong("groupid"),new MsgCollection(decodeAction.optJSONArray("msg").toString()));
                center.sendLog(AbstractMessageCenter.LoggerLevel.Client,msg.toString());
                center.sendMsg(msg);
            }
            if (action.startsWith("getGroups")) {
                Msg pluginMsg = new Msg();
                pluginMsg.type = Msg.TYPE_GET_GROUP_LIST;
                decodeAction.put("callback", center.sendMsg(pluginMsg).getMsg("troop").toString());
                send(decodeAction.toString());
            }
            if (action.startsWith("memberMute")) {
                Msg pluginMsg = new Msg();
                pluginMsg.type = Msg.TYPE_SET_MEMBER_SHUTUP;
                pluginMsg.groupid = decodeAction.optLong("groupid");
                pluginMsg.uin = decodeAction.optLong("person");
                pluginMsg.time =  decodeAction.optInt("time") * 60000L;
                center.sendMsg(pluginMsg);
            }
            if (action.equals("log")) {
                center.sendLog(AbstractMessageCenter.LoggerLevel.Server, decodeAction.getMsg());
            }
        } catch (JSONException e) {
            center.sendLog(AbstractMessageCenter.LoggerLevel.Client,e.getMessage());
        }
    }
}

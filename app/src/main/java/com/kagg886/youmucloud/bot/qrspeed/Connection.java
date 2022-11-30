package com.kagg886.youmucloud.bot.qrspeed;

import android.content.SharedPreferences;
import com.QR.MsgApi.PluginMsg;
import com.kagg886.youmucloud.bot.AbstractMessageCenter;
import com.kagg886.youmucloud.bot.Action;
import com.kagg886.youmucloud.bot.BotConnection;
import kagg886.qinternet.Message.MsgCollection;
import org.json.JSONException;

public class Connection extends BotConnection<PluginMsg,PluginMsg> {

    public Connection(long j, AbstractMessageCenter<PluginMsg, PluginMsg> bridge, SharedPreferences sp) {
        super(j, bridge, sp);
    }

    @Override
    public void onMessage(String str) {
        if (sp.getBoolean("outputLogs", false)) {
            center.sendLog(AbstractMessageCenter.LoggerLevel.Client, "recv<-:" + str);
        }
        try {
            Action decodeAction = Action.decodeAction(str);
            String action = decodeAction.getAction();
            if (action.equals("sendGroupMsg")) {
                PluginMsg msg = center.QIToNative(decodeAction.optLong("groupid"),new MsgCollection(decodeAction.optJSONArray("msg").toString()));
                center.sendMsg(msg);
            }
            if (action.startsWith("getGroups")) {
                PluginMsg pluginMsg = new PluginMsg();
                pluginMsg.type = 5;
                decodeAction.put("callback", center.sendMsg(pluginMsg).getMsg("troop").toString());
                send(decodeAction.toString());
            }
            if (action.startsWith("memberMute")) {
                PluginMsg pluginMsg = new PluginMsg();
                pluginMsg.type = 10;
                pluginMsg.groupid = decodeAction.optLong("groupid");
                pluginMsg.uin = decodeAction.optLong("person");
                pluginMsg.time =  decodeAction.optInt("time") * 60000L;
                center.sendMsg(pluginMsg);
            }
            if (action.equals("log")) {
                center.sendLog(AbstractMessageCenter.LoggerLevel.Server, decodeAction.getMsg());
            }
        } catch (JSONException e) {
        }
    }

    @Override
    public AbstractMessageCenter<PluginMsg, PluginMsg> getCenter() {
        return super.getCenter();
    }
}

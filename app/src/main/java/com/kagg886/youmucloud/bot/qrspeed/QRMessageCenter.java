package com.kagg886.youmucloud.bot.qrspeed;

import android.os.RemoteException;
import com.QR.MsgApi.PluginMsg;
import com.kagg886.youmucloud.bot.AbstractMessageCenter;
import kagg886.qinternet.Interface.MsgIterator;
import kagg886.qinternet.Message.MsgCollection;

import java.util.ArrayList;
import java.util.Map;

public class QRMessageCenter extends AbstractMessageCenter<PluginMsg, PluginMsg> {
    private String stat;

    public QRMessageCenter(String stat) {
        this.stat = stat;
    }

    @Override
    public MsgCollection NativeToQI(PluginMsg msg) {
        MsgCollection msgCollection = new MsgCollection();
        for (Map<String, ArrayList<String>> map : msg.data) {
            for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
                if (entry.getKey().equals("msg")) {
                    msgCollection.putText(entry.getValue().get(0));
                }
                if (entry.getKey().equals("img")) {
                    msgCollection.putImage("http://gchat.qpic.cn/gchatpic_new/0/0-0-" + entry.getValue().get(0).split("\\.")[0].replace("{", "").replace("}", "").replace("-", "") + "/0?term=2");
                }
                if (entry.getKey().equals("xml")) {
                    msgCollection.putxml(entry.getValue().get(0));
                }
                if (entry.getKey().equals("json")) {
                    msgCollection.putJson(entry.getValue().get(0));
                }
                if (entry.getKey().equals("face")) {
                    msgCollection.putText("[表情id]:" + entry.getValue().get(0));
                }
                if (entry.getKey().equals("at")) {
                    msgCollection.putAt(Long.parseLong(entry.getValue().get(0)));
                }
            }
        }
        return msgCollection;
    }

    @Override
    public PluginMsg QIToNative(long gid, MsgCollection msg) {
        PluginMsg pluginMsg = new PluginMsg();
        pluginMsg.type = 0;
        pluginMsg.groupid = gid;
        msg.iterator(new MsgIterator() {
            @Override
            public void onText(String s) {
                pluginMsg.addMsg("msg", s);
            }

            public void onImage(String str) {
                pluginMsg.addMsg("img", str);
            }

            public void onXml(String str) {
                pluginMsg.addMsg("xml", str);
            }

            public void onJson(String str) {
                pluginMsg.addMsg("json", str);
            }

            @Override
            public void onPtt(String s) {

            }

            public void onAt(long j) {
                pluginMsg.addMsg("at", String.valueOf(j));
                pluginMsg.addMsg("msg", "@" + j);
            }
        });
        return pluginMsg;
    }

    @Override
    public PluginMsg sendMsg(PluginMsg t) {
        if (PluginService.INSTANCE != null && PluginService.INSTANCE.getConnection() != null) {
            try {
                return PluginService.INSTANCE.getConnection().service.handlerMessage(t);
            } catch (RemoteException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public void sendLog(LoggerLevel level, String msg) {
        PluginMsg m = new PluginMsg();
        m.type = -1;
        m.addMsg("msg",String.format("[%s]:%s", level.toString(), msg));
        sendMsg(m);
    }

    @Override
    public String getPlatform() {
        return "QRSpeed_" + stat;
    }
}

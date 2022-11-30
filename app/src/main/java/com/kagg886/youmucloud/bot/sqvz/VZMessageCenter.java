package com.kagg886.youmucloud.bot.sqvz;

import com.kagg886.youmucloud.bot.AbstractMessageCenter;
import com.setqq.script.Msg;
import com.setqq.script.sdk.PluginApiInterface;
import kagg886.qinternet.Interface.MsgIterator;
import kagg886.qinternet.Message.MsgCollection;

import java.util.ArrayList;
import java.util.HashMap;

public class VZMessageCenter extends AbstractMessageCenter<Msg, Msg> {
    private PluginApiInterface api;

    public VZMessageCenter(PluginApiInterface api) {
        this.api = api;
    }

    @Override
    public MsgCollection NativeToQI(Msg msg) {
        MsgCollection col = new MsgCollection();
        col.putText(msg.getTextMsg());
        HashMap data = msg.getData();

        for (Object k : data.keySet()) {
            String key = (String) k;
            if (key.equals("at")) {
                ArrayList p = (ArrayList) data.get(k);
                for (Object n : p) {
                    col.putAt(Long.parseLong(n.toString().split("@")[0]));
                }
            }

            if (key.equals("img")) {
                ArrayList p = (ArrayList) data.get(k);
                for (Object n : p) {
                    //E7300FB253AD357BCE73895C436C9103.jpg
                    col.putImage("http://gchat.qpic.cn/gchatpic_new/0/0-0-" + n.toString().split("\\.")[0] + "/0?term=2");
                }
            }
        }

        return col;
    }

    @Override
    public Msg QIToNative(long gid, MsgCollection msg) {
        Msg b = new Msg();
        b.type = Msg.TYPE_GROUP_MSG;
        b.groupid = gid;
        msg.iterator(new MsgIterator() {
            @Override
            public void onText(String s) {
            }

            @Override
            public void onImage(String s) {
                b.addMsg("img",s);
            }

            @Override
            public void onXml(String s) {

            }

            @Override
            public void onJson(String s) {
                b.addMsg("json",s);
            }

            @Override
            public void onPtt(String s) {
                b.addMsg("ptt",s);
            }

            @Override
            public void onAt(long l) {
                b.addMsg("msg","[QQ]:" + l + "\n");
            }
        });
        b.addMsg("msg",msg.getTexts());
        return b;
    }

    @Override
    public Msg sendMsg(Msg t) {
        return api.send(t);
    }

    @Override
    public void sendLog(LoggerLevel level, String msg) {
        api.log(0, String.format("[%s]:%s", level.toString(), msg));
    }

    @Override
    public String getPlatform() {
        return "SQVZ_Vip";
    }
}

package com.kagg886.youmucloud.bot.secluded;

import com.kagg886.youmucloud.bot.AbstractMessageCenter;
import kagg886.qinternet.Interface.MsgIterator;
import kagg886.qinternet.Message.MsgCollection;
import mcsq.nxa.secluded.msg.Messenger;
import mcsq.nxa.secluded.msg.Msg;

import java.util.HashMap;

public class SecludedMessageCenter extends AbstractMessageCenter<Messenger, Messenger.Builder> {

    public static final SecludedMessageCenter INSTANCE = new SecludedMessageCenter();

    @Override
    public MsgCollection NativeToQI(Messenger msg) {
        MsgCollection msgCollection = new MsgCollection();
        msgCollection.putText(msg.getString("Text"));
        for (String str : msg.getList("AtUin")) {
            msgCollection.putAt(Long.parseLong(str));
        }
        for (HashMap hashMap : msg.getList()) {
            msgCollection.putImage((String) hashMap.get("Url"));
        }
        if (msg.hasMsg("Json")) {
            msgCollection.putJson(msg.getString("Json"));
        }
        if (msg.hasMsg("Xml")) {
            msgCollection.putJson(msg.getString("Xml"));
        }
        return msgCollection;
    }

    @Override
    public Messenger.Builder QIToNative(long gid, MsgCollection msg) {
        return messenger -> {
            messenger.addMsg("Group");
            messenger.addMsg("GroupId", gid);
            messenger.addMsg(Msg.Text, msg.getTexts());
            msg.iterator(new MsgIterator() {

                public void onPtt(String str) {
                }

                public void onText(String str) {
                }

                public void onAt(long j) {
                    String valueOf = String.valueOf(j);
                    messenger.addMsg("AtName", "@", valueOf);
                    messenger.addMsg("AtUin", valueOf);
                }

                public void onImage(String str) {
                    messenger.addMsg("Img", str);
                }

                public void onJson(String str) {
                    messenger.addMsg("Json", str);
                }

                public void onXml(String str) {
                    messenger.addMsg("Xml", str);
                }
            });
        };
    }

    @Override
    public Messenger sendMsg(Messenger.Builder messenger) {
        return PluginService.INSTANCE.send(messenger);
    }

    @Override
    public void sendLog(LoggerLevel level, String msg) {
        PluginService.INSTANCE.printI(String.format("[%s]:%s", level.toString(), msg));
    }

    @Override
    public String getPlatform() {
        return "Secluded_Android";
    }
}

package com.kagg886.youmucloud_mirai.websocket;

import com.kagg886.youmucloud_mirai.PluginInstance;
import com.kagg886.youmucloud_mirai.YoumuConfig;
import kagg886.qinternet.Interface.MsgIterator;
import kagg886.qinternet.Message.MsgCollection;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

public class MessageCenter {
    public static void sendLog(Logger logger, String text) {
        PluginInstance.logger.info("[" + logger.name() + "]:" + text);
    }

    public static MessageChain QITOMIRAIFORGROUP(MsgCollection c, Group g) {
        MessageChainBuilder chain = new MessageChainBuilder();
        c.iterator(new MsgIterator() {
            public void onXml(String arg0) {

            }

            public void onText(String arg0) {
                chain.add(arg0);

            }

            public void onPtt(String arg0) {
            }

            public void onJson(String arg0) {
                JSONObject share = new JSONObject(arg0).optJSONObject("meta").optJSONObject("music");
                chain.add(new MusicShare(
                        MusicKind.NeteaseCloudMusic,
                        share.optString("title"),
                        share.optString("desc"),
                        share.optString("jumpUrl"),
                        share.optString("preview"),
                        share.optString("musicUrl")));
            }

            public void onImage(String arg0) {
                Connection.Response conn = null;
                try {
                    conn = Jsoup.connect(arg0).ignoreContentType(true).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                chain.add(ExternalResource.uploadAsImage(Objects.requireNonNull(conn).bodyStream(),g));
            }

            public void onAt(long arg0) {
                chain.add(new At(arg0));
            }
        });
        return chain.build();
    }

    public static MsgCollection MIRAITOQI(MessageChain c) {
        MsgCollection p = new MsgCollection();

        c.stream().filter(PlainText.class::isInstance).forEach(singleMessage -> {
            PlainText t = (PlainText) singleMessage;
            p.putText(t.getContent());
        });

        c.stream().filter(At.class::isInstance).forEach(t -> {
            At at = (At) t;
            p.putAt(at.getTarget());
        });



        c.stream().filter(Image.class::isInstance).forEach(t -> {
            Image i = (Image) t;
            String url = "http://gchat.qpic.cn/gchatpic_new/0/0-0-" + i.getImageId().split("\\.")[0].replace("{","").replace("}","").replace("-","") + "/0?term=2";
            p.putImage(url);
            //p.putImage(Image.queryUrl(i));
        });

        c.stream().filter(LightApp.class::isInstance).forEach(t -> {
            LightApp a = (LightApp) t;
            p.putJson(a.getContent());
         });
        return p;
    }


    public enum Logger {
        Client,Server
    }
}

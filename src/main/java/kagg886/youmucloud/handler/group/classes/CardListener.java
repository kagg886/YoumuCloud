package kagg886.youmucloud.handler.group.classes;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.Message.MsgSpawner;
import kagg886.youmucloud.handler.group.GroupMsgHandle;
import kagg886.youmucloud.util.MsgIterator;
import kagg886.youmucloud.util.cache.JSONObjectStorage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

public class CardListener extends GroupMsgHandle {

    @Override
    public void handle(GroupMsgPack pack) throws Exception {
        JSONObjectStorage cfg = JSONObjectStorage.obtain("data/" + pack.getGroup().getId() + "/Config.json");
        if (pack.getMessage().containMsgType(MsgCollection.MsgType.xml) || pack.getMessage().containMsgType(MsgCollection.MsgType.json) || pack.getMessage().containMsgType(MsgCollection.MsgType.img)) {
            if (cfg.optString("videoformat", "on").equals("on")) {
                bliDecode(pack);
                return;
            }
        }
        String s = pack.getMessage().getTexts();
        if (s.contains("http") && s.contains("youtube")) {
            if (cfg.optString("YtVideoFormat","on").equals("on")) {
                ytbDecode(pack);
            }
        }
    }

    private void ytbDecode(GroupMsgPack pack) throws Exception {
        Document c;
        try {
           c = Jsoup.connect(pack.getMessage().getTexts()).get();
        } catch (Exception e) {
            sendMsg(pack,"解析油管视频链接时发生错误!\n",e.getMessage());
            return;
        }
        for (Element e : c.getElementsByTag("script")) {
            if (e.toString().contains("var ytInitialData = ")) {
                JSONObject rtn  = new JSONObject(e.toString().split("var ytInitialData = ")[1].replace(";</script>",""));
                rtn = rtn.optJSONObject("contents").optJSONObject("twoColumnWatchNextResults");
                JSONArray msg = rtn.optJSONObject("results").optJSONObject("results").optJSONArray("contents");
                String title = msg.optJSONObject(0) //标题
                        .optJSONObject("videoPrimaryInfoRenderer")
                        .optJSONObject("title").optJSONArray("runs").optJSONObject(0).optString("text");
                String date = msg.optJSONObject(0) //投稿日期
                        .optJSONObject("videoPrimaryInfoRenderer")
                        .optJSONObject("dateText").optString("simpleText");
                String play;
                boolean isLive;
                JSONObject playInfo = msg.optJSONObject(0) //播放量
                        .optJSONObject("videoPrimaryInfoRenderer")
                        .optJSONObject("viewCount").optJSONObject("videoViewCountRenderer");
                isLive = playInfo.isNull("isLive");
                if (!isLive) {
                    play = playInfo.optJSONObject("viewCount")
                            .optJSONArray("runs").optJSONObject(0)
                            .optString("text");
                } else {
                    play = playInfo.optJSONObject("shortViewCount").optString("simpleText");
                }

                String description = msg.optJSONObject(1) //描述
                        .optJSONObject("videoSecondaryInfoRenderer")
                        .optJSONObject("description").optJSONArray("runs")
                        .optJSONObject(0).optString("text");
                String authorName = msg.optJSONObject(1) //作者名字
                        .optJSONObject("videoSecondaryInfoRenderer")
                        .optJSONObject("owner").optJSONObject("videoOwnerRenderer")
                        .optJSONObject("title").optJSONArray("runs")
                        .optJSONObject(0).optString("text");
                String bkn;
                if (!isLive) {
                    bkn = "直播";
                } else {
                    bkn = "视频";
                }
                sendMsg(pack,"-Youtube",bkn,"解析 By kagg886-",
                        "\n标题:",title,
                        "\nUP:",authorName,
                        "\n描述:",description,
                        "\n播放:",play,
                        "\n投稿日期:",date,
                        "\ntips:此工具正在更新,若无故报错请发送.help加群联系我"
                );
            }
        }
    }


    private void bliDecode(final GroupMsgPack pack) {
        pack.getMessage().iterator(new MsgIterator() {

            @Override
            public void onXml(String arg0) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(arg0.getBytes());

                try {
                    org.w3c.dom.Document documents = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
                    NodeList list = documents.getElementsByTagName("msg");
                    if (list.item(0).getAttributes().getNamedItem("brief").getTextContent().equals("[QQ小程序]哔哩哔哩")) {

                        String BV;
                        Document document = Jsoup.connect(list.item(0).getAttributes().getNamedItem("url").getTextContent())
                                .ignoreContentType(true).get();

                        for (Element string : document.getElementsByTag("meta")) {
                            if (string.attr("itemprop").equals("url")) {
                                String[] c = string.attr("content").split("/");
                                BV = c[c.length - 1];
                                pack.getGroup().sendMsg(BliVideoInfo(BV));
                                return;
                            }
                        }
                    }
                } catch (Exception r) {
                    r.printStackTrace();
                }
            }
            @Override
            public void onJson(String arg0) {
                try {
                    JSONObject source = new JSONObject(arg0);
                    if (source.optString("desc").equals("哔哩哔哩")) {
                        String BV;
                        Document document = Jsoup
                                .connect(source.optJSONObject("meta").optJSONObject("detail_1").optString("qqdocurl"))
                                .ignoreContentType(true).get();
                        for (Element string : document.getElementsByTag("meta")) {
                            if (string.attr("itemprop").equals("url")) {
                                String[] c = string.attr("content").split("/");
                                BV = c[c.length - 1];
                                pack.getGroup().sendMsg(BliVideoInfo(BV));
                                return;
                            }
                        }

                    }
                } catch (Exception e) {
                    pack.getGroup().sendMsg(MsgSpawner.newPlainText("解析b站视频时出错!"));
                }
            }
        });
    }

    public MsgCollection BliVideoInfo(String BV) throws Exception {
        MsgCollection co = new MsgCollection();
        JSONObject output = new JSONObject(Jsoup.connect("https://api.bilibili.com/x/web-interface/view?bvid=" + BV)
                .ignoreContentType(true).method(Connection.Method.GET).execute().body());
        if (output.optInt("code") == -400) {
            co.putText("解析失败!可能这个小程序消息是人为构造或视频已消失!");
            return co;
        }
        output = output.optJSONObject("data");
        co.putText("-Bli视频解析 By YoumuCloud-");
        co.putImage(output.optString("pic"));
        co.putText(output.optString("title") + "(" + BV + ")");
        co.putText("\nUP:" + output.optJSONObject("owner").optString("name"));
        co.putText("\n简介:" + output.optString("desc"));

        output = output.optJSONObject("stat");
        co.putText("\n互动:" + String.format("观看:%d,点赞/不喜欢:%s,投币:%d,弹幕:%d", output.optInt("view"),
                output.getInt("like") + "/" + output.getInt("dislike"), output.optInt("coin"),
                output.optInt("danmaku")));

        return co;
    }
}

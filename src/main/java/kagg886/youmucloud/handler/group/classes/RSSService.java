package kagg886.youmucloud.handler.group.classes;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.Message.MsgSpawner;
import kagg886.youmucloud.handler.group.GroupMsgHandle;
import kagg886.youmucloud.util.Mail;
import kagg886.youmucloud.util.Utils;
import kagg886.youmucloud.util.cache.JSONArrayStorage;
import kagg886.youmucloud.util.cache.JSONObjectStorage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class RSSService extends GroupMsgHandle {
    @Override
    public void handle(GroupMsgPack pack) throws Exception {
        String text = pack.getMessage().getTexts();

        if (!text.equals(".rss") && text.startsWith(".rss")) {
            JSONArrayStorage links = JSONArrayStorage.obtain("data/" + pack.getGroup().getId() + "/RSS.json");
            JSONObjectStorage options = JSONObjectStorage.obtain("data/" + pack.getGroup().getId() + "/RSSSetting.json");

            //[{"title":"xxx","url":"xxx"}]

            if (text.startsWith(".rss setmail")) {
                String opt = null;
                try {
                    opt = text.split(" ")[2];
                    if (!opt.equals("on") && !opt.equals("off")) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    sendMsg(pack, "请输入on或off");
                }
                if (options.optString(pack.getMember().getUin() + "_mail", "on").equals(opt)) {
                    sendMsg(pack, "请勿重复设置!");
                    return;
                }

                options.put(pack.getMember().getUin() + "_mail", opt);
                if (options.save()) {
                    sendMsg(pack, "设置成功!");
                } else {
                    sendMsg(pack, "设置失败!");
                }
            }

            if (text.startsWith(".rss get")) {
                String[] v = text.split(" ");
                JSONObject o;
                if (v.length <= 2) {
                    //每条链接取一条
                    MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "订阅消息如下:");
                    for (int i = 0; i < links.length(); i++) {
                        o = XML.toJSONObject(Jsoup.connect(links.optJSONObject(i).optString("url")).ignoreContentType(true).execute().body().replace("\n", ""));
                        o = o.optJSONObject("rss").optJSONObject("channel").optJSONArray("item").optJSONObject(0);
                        col.putText("\n");
                        col.putText(o.optString("title"));
                        col.putText("\n");
                        col.putText(cut(o.optString("description")));
                        col.putText("\n详情请戳->");
                        col.putText(o.optString("link"));
                        col.putText("\n");
                        col.putText("————————————");
                    }

                    if (options.optString(pack.getMember().getUin() + "_mail", "on").equals("on")) {
                        Mail.sendMessage(pack.getMember().getUin() + "@qq.com", "RSS推送", col);
                        sendMsg(pack, "已将推送链接发送到您的QQ邮件当中\n若未接收到邮件,可能是您未注册QQ邮箱或其他原因");
                        return;
                    }
                    sendMsg(pack, col);
                    return;
                } else {
                    //一条链接取5条
                    int index = Integer.parseInt(v[2]);
                    if (index >= links.length()) {
                        sendMsg(pack, "序号不允许超过订阅列表数目!");
                        return;
                    }
                    o = XML.toJSONObject(Jsoup.connect(links.optJSONObject(index).optString("url")).ignoreContentType(true).execute().body().replace("\n", ""));
                    o = o.optJSONObject("rss").optJSONObject("channel");
                    MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), o.optString("title"), "\n更新日期:", o.optString("lastBuildDate"));
                    JSONArray ary = o.optJSONArray("item");
                    index = ary.length();
                    if (index > 4) {
                        index = 4;
                    }
                    for (int i = 0; i < index; i++) {
                        o = ary.optJSONObject(i);
                        col.putText("\n");
                        col.putText(o.optString("title"));
                        col.putText("\n");
                        col.putText(cut(o.optString("description")));
                        col.putText("\n详情请戳->");
                        col.putText(o.optString("link"));
                        col.putText("\n");
                        col.putText("————————————");
                    }
                    if (options.optString(pack.getMember().getUin() + "_mail", "on").equals("on")) {
                        Mail.sendMessage(pack.getMember().getUin() + "@qq.com", "RSS推送", col);
                        sendMsg(pack, "已将推送链接发送到您的QQ邮件当中\n若未接收到邮件,可能是您未注册QQ邮箱或其他原因");
                        return;
                    }
                    sendMsg(pack, col);
                }
            }

            if (text.startsWith(".rss remove")) {
                String[] v = text.split(" ");
                if (v.length <= 2) {
                    sendMsg(pack, "请输入序号!");
                    return;
                }
                int index = Integer.parseInt(v[2]);
                if (index >= links.length()) {
                    sendMsg(pack, "序号不允许超过订阅列表数目!");
                    return;
                }
                links.remove(index);
                if (links.save()) {
                    sendMsg(pack, "移除成功!");
                } else {
                    sendMsg(pack, "移除失败!请加入插件问答群反馈!");
                }
            }

            if (text.equals(".rss check")) {
                JSONObject info;
                for (int i = 0; i < links.length(); i++) {
                    info = links.optJSONObject(i);
                    Connection.Response resp = Jsoup.connect(info.optString("url")).ignoreContentType(true).execute();
                    if (resp.statusCode() != 200) {
                        links.remove(i);
                        return;
                    }
                    if (!resp.body().startsWith("<?xml")) {
                        links.remove(i);
                        return;
                    }
                }
                if (links.save()) {
                    sendMsg(pack, "已自动清除无效RSS订阅链接!");
                } else {
                    sendMsg(pack, "清理失败!请加入插件问答群反馈!");
                }

            }

            if (text.startsWith(".rss suc")) {
                String[] v = text.split(" ");
                if (v.length <= 2) {
                    sendMsg(pack, "请输入链接!");
                    return;
                }
                for (int i = 0; i < links.length(); i++) {
                    if (v[2].equals(links.optJSONObject(i).optString("url"))) {
                        sendMsg(pack, "链接已添加!");
                        return;
                    }
                }
                Connection.Response resp;
                try {
                    resp = Jsoup.connect(v[2]).ignoreContentType(true).timeout(10000).execute();
                } catch (Exception e) {
                    sendMsg(pack, "连接建立失败!" + e.getMessage());
                    return;
                }
                if (resp.statusCode() != 200) {
                    sendMsg(pack, "RSS链接订阅失败!code:" + resp.statusCode());
                    return;
                }
                if (!resp.body().startsWith("<?xml")) {
                    sendMsg(pack, "这不是一条RSS订阅链接!");
                    return;
                }
                try {
                    JSONObject o = XML.toJSONObject(resp.body());
                    o = o.optJSONObject("rss").optJSONObject("channel");
                    JSONObject source = new JSONObject();
                    source.put("title", o.optString("title"));
                    source.put("url", v[2]);
                    links.put(source);
                    if (links.save()) {
                        sendMsg(pack, "添加成功!\n订阅描述:", o.optString("title"));
                    } else {
                        sendMsg(pack, "添加失败!请加入插件问答群反馈!");
                    }
                } catch (Exception e) {
                    sendMsg(pack, "订阅链接校验失败,原因:", e.getMessage(), "\n请前去插件问答群详细反馈!");
                    return;
                }
            }

            if (text.equals(".rss list")) {
                if (links.length() == 0) {
                    sendMsg(pack, "本群没有RSS订阅链接!");
                    return;
                }
                JSONObject info;
                MsgCollection msg = MsgSpawner.newAtToast(pack.getMember().getUin(), "本群RSS订阅列表如下:");
                for (int i = 0; i < links.length(); i++) {
                    info = links.optJSONObject(i);
                    msg.putText("\n");
                    msg.putText(i + ":" + info.optString("title"));
                    msg.putText("\n" + info.optString("url"));
                    msg.putText("\n——————");
                }
                pack.getGroup().sendMsg(msg);
            }
        }
    }

    public String cut(String h5) {
        h5 = Utils.h5fliter(h5);
        if (h5.length() <= 50) {
            return h5;
        }
        return h5.substring(0, 50) + "...";
    }
}

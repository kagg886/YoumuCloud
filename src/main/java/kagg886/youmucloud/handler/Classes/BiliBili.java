package kagg886.youmucloud.handler.Classes;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.Message.MsgSpawner;
import kagg886.youmucloud.handler.MsgHandle;
import kagg886.youmucloud.util.Mail;
import kagg886.youmucloud.util.ScoreUtil;
import kagg886.youmucloud.util.cache.JSONObjectStorage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;


public class BiliBili extends MsgHandle {
    private JSONArray zones;

    public BiliBili() {
        try {
            JSONObject object = new JSONObject(Jsoup.connect("https://api.live.bilibili.com/room/v1/Area/getList?show_pinyin=1").ignoreContentType(true).execute().body());
            zones = object.optJSONArray("data");
        } catch (Exception ignored) {
        }
    }

    @Override
    public void handle(final GroupMsgPack pack) throws Exception {
        String text = pack.getMessage().getTexts();

        if (text.startsWith(".blive")) {
            String cookie = getParam(pack, "bli_cookie", "");
            if (cookie.equals("")) {
                sendMsg(pack, "Cookie未填充!请联系bot主人以解决问题!");
                return;
            }

            String roomid, csrf, csrf_token, uid;
            csrf = csrf_token = uid = null;

            for (String coo : cookie.split(";")) {
                if (coo.startsWith(" bili_jct=")) {
                    csrf = coo.replace(" bili_jct=", "");
                    csrf_token = csrf;
                    continue;
                }

                if (coo.startsWith(" DedeUserID=")) {
                    uid = coo.replace(" DedeUserID=", "");
                }
            }
            if (uid == null) {
                sendMsg(pack, "cookie设置错误!");
                return;
            }
            Connection.Response conn;
            conn = Jsoup.connect("https://api.bilibili.com/x/space/acc/info?mid=" + uid + "&jsonp=jsonp")
                    .ignoreContentType(true).header("cookie", cookie).execute();
            roomid = String.valueOf(new JSONObject(conn.body()).optJSONObject("data").optJSONObject("live_room").optInt("roomid"));

            if (text.startsWith(".blive stop")) {
                conn = Jsoup.connect("https://api.live.bilibili.com/room/v1/Room/stopLive").ignoreContentType(true)
                        .header("cookie", cookie)
						.data("room_id", roomid)
                        .data("platform", "pc")
                        .data("csrf_token", csrf_token)
                        .data("csrf", csrf)
                        .method(Connection.Method.POST).execute();

                JSONObject rtn = new JSONObject(conn.body());
                if (rtn.optInt("code") != 0) {
                    sendMsg(pack,"参数错误!,原因:",rtn.optString("msg"));
                    return;
                }

				if (rtn.optJSONObject("data").optInt("change") == 0) {
					sendMsg(pack,"无需重复关闭直播间");
					return;
				}

				sendMsg(pack,"关闭直播间成功");
            }

            if (text.startsWith(".blive start ")) {
                conn = Jsoup.connect("https://www.bejson.com/Bejson/Api/HttpRequest/curl_request").ignoreContentType(true).
                        userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36").
                        data("protocol", "https://").
                        data("url", "api.live.bilibili.com/room/v1/Room/startLive").
                        data("type", "POST").
                        data("code", "utf-8").
                        data("checked[httpOptionBox]", "true").
                        data("checked[httpHeaderBox]", "false").
                        data("checked[httpCookieBox]", "true").
                        data("checked[httpProxyBox]", "false").
                        data("paramSwitch[]", "true").
                        data("paramSwitch[]", "false").
                        data("paramSwitch[]", "false").
                        data("param1[room_id]", roomid).
                        data("param1[platform]", "pc").
                        data("param1[area_v2]", text.split(" ")[2]).
                        data("param1[csrf_token]", csrf_token).
                        data("param1[csrf]", csrf).
                        data("param2", "").
                        data("param3", "").
                        data("contentType", "application/x-www-form-urlencoded;charset=utf-8").
                        data("cookie", cookie).
                        data("proxy[proxy]", "").
                        data("proxy[port]", "").method(Connection.Method.POST).execute();

                JSONObject rtn = new JSONObject(conn.body());
                if (rtn.optInt("code") != 200) {
                    //POST的服务器发生了一些错误
                    sendMsg(pack, "代理的服务器发生了一些错误");
                    return;
                }
                rtn = rtn.optJSONObject("data");
                if (rtn.optJSONObject("hearder").optInt("http_code") != 200) {
                    //B站服务器错误
                    sendMsg(pack, "无法连接至B站服务器");
                    return;
                }

                rtn = new JSONObject(rtn.optString("result"));

                if (rtn.optInt("code") != 0) {
                    //参数出错
                    sendMsg(pack, "传输参数错误,请联系BOT的维护者以解决问题!");
                    return;
                }

                if (rtn.optString("msg").equals("重复开播")) {
                    //不允许重复开播
                    sendMsg(pack, "已经有人开播了....");
                    return;
                }

                rtn = rtn.optJSONObject("data").optJSONObject("rtmp");


                if (text.contains("-nomail")) {
                    sendMsg(pack,"服务器地址:", rtn.optString("addr"), "\n串流密钥:", rtn.optString("code"));
                    return;
                }
                Mail.sendMessage(pack.getMember().getUin() + "@qq.com","您的直播推流码", "服务器地址:", rtn.optString("addr"), "<br>串流密钥:", rtn.optString("code"));
                sendMsg(pack,"推流码已发送到您的QQ邮箱中\n若您未收到邮件,请再试一次或在末尾添加\" -nomail\"(一定要在分区id后加一个空格)");
            }

            if (text.startsWith(".blive zone")) {
                String[] var = text.split(" ");
                if (var.length == 2) {
                    sendMsg(pack, "请输入要查询的分区!");
                    return;
                }

                JSONArray zone;
                JSONObject details;
                StringBuilder builder = new StringBuilder().append("Zone——id");
                for (int i = 0; i < zones.length(); i++) {
                    zone = zones.optJSONObject(i).optJSONArray("list");
                    for (int j = 0; j < zone.length(); j++) {
                        details = zone.optJSONObject(j);
                        String s = zones.optJSONObject(i).optString("name") + "_" + details.optString("name");
                        if (s.contains(var[2])) {
                            builder.append("\n").append(s).append("-").append(details.optString("id"));
							//builder.append("\n").append(s).append("_").append(zones.optJSONObject(i).optString("id")).append("-").append(details.optString("id"));
						}
                    }
                }

                sendMsg(pack, builder.toString());
            }

            if (text.startsWith(".blive title")) {
                String[] var = text.split(" ");
                if (var.length == 2) {
                    sendMsg(pack, "请输入修改的直播标题!");
                    return;
                }
                conn = Jsoup.connect("https://api.live.bilibili.com/room/v1/Room/update")
                        .ignoreContentType(true)
                        .data("room_id", roomid)
                        .data("title", text.replace(".blive title ", ""))
                        .data("csrf", csrf)
                        .data("csrf_token", csrf_token)
                        .header("cookie", cookie)
                        .method(Connection.Method.POST).execute();
                JSONObject ret = new JSONObject(conn.body());
                if (ret.optInt("code") != 0) {
                    sendMsg(pack, "修改直播标题失败!\n错误原因:" + ret.optString("message"));
                    return;
                }

                sendMsg(pack, "修改完毕");

            }

            if (text.startsWith(".blive status")) {
                JSONObject obj = new JSONObject(Jsoup.connect("https://api.live.bilibili.com/room/v1/Room/get_info?id=" + roomid).ignoreContentType(true).execute().body());
                if (obj.getInt("code") == 1) {
                    sendMsg(pack, obj.optString("message"));
                    return;
                }
                obj = obj.optJSONObject("data");
                MsgCollection msg = MsgSpawner.newAtToast(pack.getMember().getUin(), "--RoomStatus--");
                msg.putImage(obj.optString("user_cover"));
                msg.putText("\n开播状态:");
                if (obj.optInt("live_status") == 0) {
                    msg.putText("未开播");
                } else {
                    msg.putText("已开播");
                }
                msg.putText("\n标题:" + obj.optString("title"));
                msg.putText("\n在线:" + obj.optInt("online"));
                msg.putText("\n直播地址:https://live.bilibili.com/" + roomid);
                pack.getGroup().sendMsg(msg);
            }
        }

        if (text.startsWith(".bli articleimg")) {
            String[] var = text.split(" ");
            if (var.length == 2) {
                pack.getGroup().sendMsg(MsgSpawner.newAtToast(pack.getMember().getUin(), "请输入cvid!"));
                return;
            }

            if (ScoreUtil.checkCoin(this,pack,2)) {
                return;
            }

            String cvid = var[2].replace("cv", "");
            MsgCollection collection = new MsgCollection();
            Connection.Response resp = Jsoup.connect("https://www.bilibili.com/read/cv" + cvid).execute();
            if (resp.statusCode() == 404) {
                sendMsg(pack, "专栏不存在!");
                return;
            }

            for (Element img : Jsoup.parse(resp.body()).getElementsByTag("figure")) {
                String link = img.getElementsByTag("img").get(0).attr("data-src");
                if (link.startsWith("//")) {
                    link = "http:" + link;
                }
                collection.putImage(link);
            }

            pack.getGroup().sendMsg(collection);
        }

        if (text.startsWith(".bli videoformat")) {
            String[] var = text.split(" ");
            if (var.length == 2 || (var.length == 3 & (!var[2].equals("on") & !var[2].equals("off")))) {
                pack.getGroup().sendMsg(MsgSpawner.newAtToast(pack.getMember().getUin(), "请输入开关状态!(on/off)"));
                return;
            }
            JSONObjectStorage config = JSONObjectStorage.obtain("data/" + pack.getGroup().getId() + "/Config.json");
            if (config.optString("videoformat", "on").equals(var[2])) {
                sendMsg(pack, "请勿重复设置!");
                return;
            }

            config.put("videoformat", var[2]);
            if (config.save()) {
                sendMsg(pack, "设置成功!");
            } else {
                sendMsg(pack, "设置失败!");
            }
        }

        if (text.startsWith(".ytb videoformat")) {
            String[] var = text.split(" ");
            if (var.length == 2 || (var.length == 3 & (!var[2].equals("on") & !var[2].equals("off")))) {
                pack.getGroup().sendMsg(MsgSpawner.newAtToast(pack.getMember().getUin(), "请输入开关状态!(on/off)"));
                return;
            }
            JSONObjectStorage config = JSONObjectStorage.obtain("data/" + pack.getGroup().getId() + "/Config.json");
            if (config.optString("YtVideoFormat", "on").equals(var[2])) {
                sendMsg(pack, "请勿重复设置!");
                return;
            }

            config.put("YtVideoFormat", var[2]);
            if (config.save()) {
                sendMsg(pack, "设置成功!");
            } else {
                sendMsg(pack, "设置失败!");
            }
        }
    }
}

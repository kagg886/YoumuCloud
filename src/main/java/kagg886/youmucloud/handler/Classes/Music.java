package kagg886.youmucloud.handler.Classes;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.Message.MsgSpawner;
import kagg886.youmucloud.util.ScoreUtil;
import kagg886.youmucloud.handler.MsgHandle;
import kagg886.youmucloud.util.MusicFactory;
import kagg886.youmucloud.util.Utils;
import kagg886.youmucloud.util.WaitService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class Music extends MsgHandle {

	@Override
	public void handle(final GroupMsgPack pack) throws Exception {
		final String text = pack.getMessage().getTexts();
		final long qq = pack.getMember().getUin();

		if (WaitService.hasKey(qq + "_songs")) {
			if (WaitService.addCall(qq + "_songs",text)) {
				sendMsg(pack,"选歌成功,选项:" + text);
			} else {
				sendMsg(pack,"选歌失败");
			}
		}

		if (text.startsWith(".ms qq ")) {
			String[] vars = text.split(" ");
			if (vars.length == 2) {
				sendMsg(pack, "请输入要搜索的歌曲名称!");
				return;
			}

			JSONArray list = getQQMusicJSONData(text.replace(".ms qq ",""),0);

			if (ScoreUtil.checkCoin(this,pack,3)) {
				return;
			}
			ArrayList<MusicInfo<String>> infos = new ArrayList<>();
			JSONObject data;
			MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(),text.replace(".ms qq ",""),"的搜索结果如下:\n");
			for (int i = 0; i < list.length(); i++) {
				data = list.optJSONObject(i);
				String songName = data.optString("songname");
				String author = data.optJSONArray("singer").optJSONObject(0).optString("name");
				String id = data.optString("songmid");
				infos.add(new MusicInfo<>(songName,author,id));
				col.putText(i + ":" + songName);
				col.putText("---" + author);
				col.putText("\n");
			}
			col.putText("发送序号面前的数字以进行点歌，您只有一次机会\n发送-1停止点歌");
			sendMsg(pack,col);

			Utils.service.execute(() -> {
				int choice = -2;
				try {
					String a = WaitService.wait(qq + "_songs");
					if (a == null) {
						throw new RuntimeException("null");
					}
					choice = Integer.parseInt(a);
				} catch (Exception e) {
					sendMsg(pack, "发生错误!自动停止点歌!");
					return;
				}

				switch (choice) {
					case -2:
						sendMsg(pack, "超时停止点歌~");
						return;
					case -1:
						sendMsg(pack, "点歌已手动停止。");
						return;
				}
				MusicInfo<String> target = infos.get(choice);
				Document document;
				try {
					Connection.Response conn = Jsoup.connect("https://www.bejson.com/Bejson/Api/HttpRequest/curl_request").ignoreContentType(true).
							userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36").
							data("protocol", "https://").
							data("url", "i.y.qq.com/v8/playsong.html?ADTAG=ryqq.songDetail&songmid=" + target.id).
							data("type", "GET").
							data("code", "utf-8").
							data("checked[httpOptionBox]", "false").
							data("checked[httpHeaderBox]", "true").
							data("checked[httpCookieBox]", "false").
							data("checked[httpProxyBox]", "false").
							data("paramSwitch[]", "false").
							data("paramSwitch[]", "true").
							data("paramSwitch[]", "false").
							data("param2","").
							data("param3","").
							data("contentType", "application/x-www-form-urlencoded;charset=utf-8")
							.method(Connection.Method.POST).execute();
					document = Jsoup.parse(new JSONObject(conn.body()).optJSONObject("data").optString("result"));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				String picUrl = document.getElementsByAttributeValue("class","bg__img").get(0).attr("src"); //插图

				for (Element s : document.getElementsByTag("script")) {
					if (s.html().startsWith("window.__ssrFirstPageData__ =")) {
						JSONObject songInfo;
						try {
							songInfo = new JSONObject(s.html().replace("window.__ssrFirstPageData__ =", ""));
						} catch (JSONException e) {
							throw new RuntimeException(e);
						}
						songInfo = songInfo.optJSONArray("songList").optJSONObject(0);
						String link = Utils.unicodeToString(songInfo.optString("url")); //播放链接

						if (getParam(pack,"platform","").equals("QRSpeed_Vip")) {
							MsgCollection c = new MsgCollection();
							c.putJson(MusicFactory.spawnMusic("QQ音乐",target.songName,target.author,picUrl,link,"https://i.y.qq.com/v8/playsong.html?ADTAG=ryqq.songDetail&songmid=" + target.id + "&songid=0&songtype=0#webchat_redirect"));
							pack.getGroup().sendMsg(c);
						} else {
							sendMsg(pack,
									"歌名:",target.songName,"\n",
									"作者:",target.author,"\n",
									"播放链接:",link
							);
						}
					}
				}
			});
		}

		if (text.startsWith(".ms nes ")) {
			String[] vars = text.split(" ");
			if (vars.length == 2) {
				sendMsg(pack, "请输入要搜索的歌曲名称!");
				return;
			}

			if (ScoreUtil.checkCoin(this,pack,3)) {
				return;
			}

			//获取数据
			String call = Jsoup.connect("http://music.eleuu.com/search?keywords=" + text.replace(".ms nes ",""))
					.ignoreContentType(true)
					.execute().body();
			JSONArray musicList = new JSONObject(call).optJSONObject("result").optJSONArray("songs");

			//准备基本参数
			MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(),text.replace(".ms nes ",""),"的搜索结果如下:\n");
			JSONObject details;
			ArrayList<MusicInfo<Long>> infos = new ArrayList<>();
			//循环
			for (int i = 0; i < musicList.length(); i++) {
				details = musicList.optJSONObject(i);

				String art = details.optJSONArray("artists").optJSONObject(0).optString("name");
				//记录歌曲id
				infos.add(new MusicInfo<>(details.optString("name"), art, details.optLong("id")));

				//加入消息集合
				col.putText(i + ":" + details.optString("name"));
				col.putText("---");
				col.putText(art);
				col.putText("\n");
			}
			col.putText("发送序号面前的数字以进行点歌，您只有一次机会\n发送-1停止点歌");
			sendMsg(pack,col);

			Utils.service.execute(() -> {
				int choice = -2;
				try {
					String a = WaitService.wait(qq + "_songs");
					if (a == null) {
						throw new RuntimeException("null");
					}
					choice = Integer.parseInt(a);
				} catch (Exception e) {
					sendMsg(pack,"发生错误!自动停止点歌!");
					return;
				}

				switch (choice) {
					case -2:
						sendMsg(pack,"超时停止点歌~");
						return;
					case -1:
						sendMsg(pack,"点歌已手动停止。");
						return;
				}

				MusicInfo<Long> target = infos.get(choice);


				Document s;
				try {
					s = Jsoup.connect("https://music.163.com/song?id=" + target.id).ignoreContentType(true).get();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				String imgLink = s.getElementsByAttributeValue("property","og:image").get(0).attr("content");

				String musicLink ="http://music.163.com/song/media/outer/url?id=" + target.id + ".mp3";
				if (getParam(pack,"platform","").equals("QRSpeed_Vip")) {
					MsgCollection c = new MsgCollection();
					c.putJson(MusicFactory.spawnMusic("网易云音乐",target.songName,target.author,imgLink,musicLink,"https://music.163.com/song?id=" + target.id));
					pack.getGroup().sendMsg(c);
				} else {
					sendMsg(pack,
							"歌名:",target.songName,"\n",
							"作者:",target.author,"\n",
							"播放链接:",musicLink
					);
				}
			});
		}
	}

	public static JSONArray getQQMusicJSONData(String key,int repeat) throws Exception {
		if (repeat == 4) {
			throw new Exception("服务器拒绝请求!");
		}
		Connection.Response conn = Jsoup.connect("https://www.bejson.com/Bejson/Api/HttpRequest/curl_request").ignoreContentType(true).
				userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36").
				data("protocol", "https://").
				data("url", "c.y.qq.com/soso/fcgi-bin/client_search_cp?aggr=1&cr=1&flag_qc=0&p=1&n=10&format=json&w=" + key).
				data("type", "GET").
				data("code", "utf-8").
				data("checked[httpOptionBox]", "false").
				data("checked[httpHeaderBox]", "false").
				data("checked[httpCookieBox]", "false").
				data("checked[httpProxyBox]", "false").
				data("paramSwitch[]", "true").
				data("paramSwitch[]", "false").
				data("paramSwitch[]", "false").
				data("param2","").
				data("param3","false").
				data("contentType", "application/x-www-form-urlencoded;charset=utf-8")
				.method(Connection.Method.POST).execute();
		JSONObject data = new JSONObject(conn.body());
		data = data.optJSONObject("data");
		if (data.optJSONObject("hearder").optInt("http_code") != 200) {
			return getQQMusicJSONData(key,++repeat);
		}
		data = new JSONObject(data.optString("result"));
		return data.optJSONObject("data").optJSONObject("song").optJSONArray("list");
	}

	static class MusicInfo<T> {
		public String songName;
		public String author;
		public T id;

		public MusicInfo(String songName,String author,T id) {
			this.songName = songName;
			this.author = author;
			this.id = id;
		}
	}
}

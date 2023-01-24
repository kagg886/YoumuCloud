package kagg886.youmucloud.handler.group.classes;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.Message.MsgSpawner;
import kagg886.youmucloud.handler.group.GroupMsgHandle;
import kagg886.youmucloud.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

public class Music extends GroupMsgHandle {

    @Override
    public void handle(final GroupMsgPack pack) throws Exception {
        final String text = pack.getMessage().getTexts();
        final long qq = pack.getMember().getUin();

        if (WaitService.hasKey(qq + "_songs")) {
			try {
				Integer.parseInt(text);
			} catch (NumberFormatException e) {
				return;
			}
			WaitService.addCall(qq + "_songs", text);
			sendMsg(pack, "选歌成功,选项:" + text);
		}

		if (text.startsWith(".ms nes ")) {
			String[] vars = text.split(" ");
			if (vars.length == 2) {
				sendMsg(pack, "请输入要搜索的歌曲名称!");
				return;
			}

			if (ScoreUtil.checkCoin(this, pack, 3)) {
				return;
			}

			String param = "{\"s\":\"" + text.replace(".ms nes ", "") + "\",\"offset\":\"0\",\"csrf_token\":\"\",\"limit\":\"10\",\"type\":\"1\"}";


			//必须借助代理
			Connection conn = Jsoup.connect("https://www.bejson.com/Bejson/Api/HttpRequest/curl_request").ignoreContentType(true).
					userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36");
			conn.data("protocol", "https://");
			conn.data("url", "music.163.com/weapi/cloudsearch/get/web?csrf_token=");
			conn.data("type", "POST");
			conn.data("code", "utf-8");
			conn.data("checked[httpOptionBox]", "true");
			conn.data("checked[httpHeaderBox]", "true");
			conn.data("checked[httpCookieBox]", "false");
			conn.data("checked[httpProxyBox]", "false");
			conn.data("paramSwitch[]", "false");
			conn.data("paramSwitch[]", "true");
			conn.data("paramSwitch[]", "false");
			conn.data("param2", Music163EncyptTool.generateToken(param));
			conn.data("param3", "");
			conn.data("headers[referer]", "https://music.163.com/search/");
			conn.data("headers[accept]", "*/*");
			conn.data("headers[accept-language]", "zh-CN,zh;q=0.8");
			conn.data("headers[origin]", "https://music.163.com");
			conn.data("contentType", "application/json;charset=utf-8");
			conn.data("cookie", "");
			conn.data("proxy[proxy]", "");
			conn.data("proxy[port]", "");

			JSONObject rtn = new JSONObject(conn.execute().body());
			if (rtn.optInt("code") != 200) {
				//POST的服务器发生了一些错误
				sendMsg(pack, "代理的服务器发生了一些错误");
				return;
			}
			rtn = rtn.optJSONObject("data");
			if (rtn.optJSONObject("hearder").optInt("http_code") != 200) {
				sendMsg(pack, "无法连接至网易云服务器");
				return;
			}

			JSONObject fallBack = new JSONObject(rtn.optString("result")).optJSONObject("result");

			int count = fallBack.optInt("songCount");
			if (count == 0) {
				sendMsg(pack, "未搜索到你想要的作品");
				return;
			}
			count = Math.min(10, count);
			JSONArray songsSource = fallBack.optJSONArray("songs");
			ArrayList<MusicInfo<Long>> infos = new ArrayList<>();
			MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "点歌结果如下:\n");
			for (int i = 0; i < count; i++) {
				JSONObject song = songsSource.optJSONObject(i);
				String name = song.optString("name"); //歌名
				JSONArray ar_source = song.optJSONArray("ar");
				StringBuilder author = new StringBuilder(); //作者
				for (int j = 0; j < ar_source.length(); j++) {
					author.append(ar_source.optJSONObject(j).optString("name"));
					author.append("/");
				}
				String reallyAuthor = author.substring(0, author.length() - 1);
				long id = song.optLong("id"); //id
				infos.add(new MusicInfo<>(name, reallyAuthor, id));
				col.putText(String.format("%d:%s(%s)", i, name, reallyAuthor));
				col.putText("\n");
			}
			col.putText("发送曲名前代表的序号来点歌");
			pack.getGroup().sendMsg(col);

//以下为旧版代码
//			//获取数据
//            String call = Jsoup.connect("http://cloud-music.pl-fe.cn/search?keywords=" + text.replace(".ms nes ", ""))
//                    .ignoreContentType(true)
//                    .execute().body();
//            JSONArray musicList = new JSONObject(call).optJSONObject("result").optJSONArray("songs");
//
//            //准备基本参数
//            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), text.replace(".ms nes ", ""), "的搜索结果如下:\n");
//            JSONObject details;
//            ArrayList<MusicInfo<Long>> infos = new ArrayList<>();
//            //循环
//            int i = 0;
//            int skip = 0;
//            while (i < Math.min(musicList.length(), 9 + skip)) {
//                details = musicList.optJSONObject(i);
//
//                String art = details.optJSONArray("artists").optJSONObject(0).optString("name");
//                //记录歌曲id
//                infos.add(new MusicInfo<>(details.optString("name"), art, details.optLong("id")));
//
//                //加入消息集合
//                col.putText(i + ":" + details.optString("name"));
//                col.putText("---");
//                col.putText(art);
//                col.putText("\n");
//
//                //滤过失效的歌曲
//                // TODO: 2022/9/10  待测试
//                if (Jsoup.connect("http://music.163.com/song/media/outer/url?id=" + details.optLong("id") + ".mp3").ignoreContentType(true).execute().statusCode() == 200) {
//                    i++;
//                    continue;
//                }
//                skip++;
//            }
//            col.putText("请在十秒内发送序号面前的数字以进行点歌，您只有一次机会\n发送-1停止点歌");
//            if (skip != 0) {
//                col.putText("\n本次点歌排除了" + i + "首失效歌曲");
//            }
//            sendMsg(pack, col);

			Utils.service.execute(() -> {
				int choice;
				try {
					String a = WaitService.wait(qq + "_songs");
					if (a == null) {
						sendMsg(pack, "超时停止点歌~");
						return;
					}
					choice = Integer.parseInt(a);
					if (choice == -1) {
						sendMsg(pack, "点歌已手动停止。");
						return;
					}
				} catch (Exception e) {
					sendMsg(pack, "点歌遇到错误,点歌失败!");
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
				if (!getParam(pack,"platform","").equals("QRSpeed_unVip") && !getParam(pack,"platform","").startsWith("Secluded")) {
					MsgCollection co = new MsgCollection();
					co.putJson(MusicFactory.spawnMusic("网易云音乐", target.songName, target.author, imgLink, musicLink, "https://music.163.com/song?id=" + target.id));
					pack.getGroup().sendMsg(co);
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

	static class MusicInfo<T> {
		public String songName; //歌名
		public String author; //作者
		public T id; //歌曲id

		public MusicInfo(String songName, String author, T id) {
			this.songName = songName;
			this.author = author;
			this.id = id;
		}
	}
}

package kagg886.youmucloud.handler.Classes;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.Message.MsgSpawner;
import kagg886.youmucloud.handler.MsgHandle;
import kagg886.youmucloud.util.MusicFactory;
import kagg886.youmucloud.util.ScoreUtil;
import kagg886.youmucloud.util.Utils;
import kagg886.youmucloud.util.WaitService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
			String call = Jsoup.connect("https://music.cyrilstudio.top/search?keywords=" + text.replace(".ms nes ",""))
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
				int choice;
				try {
					String a = WaitService.wait(qq + "_songs");
					if (a == null) {
						throw new RuntimeException("-2");
					}
					choice = Integer.parseInt(a);
					if (choice == -1) {
						throw new RuntimeException("-1");
					}
				} catch (Exception e) {
					switch (e.getMessage()) {
						case "-2":
							sendMsg(pack,"超时停止点歌~");
							return;
						case "-1":
							sendMsg(pack,"点歌已手动停止。");
							return;
						default:
							sendMsg(pack,"点歌遇到错误,点歌失败!");
							return;
					}
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

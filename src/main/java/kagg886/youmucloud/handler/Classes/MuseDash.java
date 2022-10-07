package kagg886.youmucloud.handler.Classes;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.Message.MsgSpawner;
import kagg886.youmucloud.handler.MsgHandle;
import kagg886.youmucloud.util.cache.JSONObjectStorage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.Iterator;

public class MuseDash extends MsgHandle {
	private JSONObject source;

	public MuseDash() {
		try {
			source = JSONObjectStorage.obtain("res/MuseMusic.json").optJSONObject("fullAlbums");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handle(GroupMsgPack pack) {
		String text = pack.getMessage().getTexts();

		if (text.startsWith(".md music")) {
			String[] var = text.split(" ");
			MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "");

			if (var.length == 2) {
				sendMsg(pack,"请输入曲名!");
				return;
			}

			Iterator<String> albums = source.keys();

			while (albums.hasNext()) {
				JSONObject album = source.optJSONObject(albums.next());
				if (album == null) {
					continue;
				}
				JSONObject songs = album.optJSONObject("music");
				Iterator<String> songkey = songs.keys();
				while (songkey.hasNext()) {
					JSONObject musicdetail = songs.optJSONObject(songkey.next());
					String name = musicdetail.optString("name");

					if (name.toLowerCase().contains(var[2].toLowerCase())) {

						col.putText("\n曲包：" + album.optJSONObject("ChineseS").optString("title"));
						col.putText("\n曲名：" + name);
						col.putText("\n作者：" + musicdetail.optString("author"));
						col.putText("\nbpm：" + musicdetail.optString("bpm"));
						col.putText("\n谱师：");
						JSONArray temp = musicdetail.optJSONArray("levelDesigner");
						for (int i = 0; i < temp.length(); i++) {
							if (temp.optString(i).equals("null")) {
								continue;
							}
							col.putText(temp.optString(i) + "，");

						}
						col.putText("\n难度：");
						temp = musicdetail.optJSONArray("difficulty");
						for (int i = 0; i < temp.length(); i++) {
							if (temp.optString(i).equals("0")) {
								continue;
							}
							col.putText(temp.optString(i) + "，");
						}

						col.putText("\n——————\n");
					}
				}
			}
			if (col.length() == 0) {
				col.putText("没有搜到。。。");
			}
			pack.getGroup().sendMsg(col);
		}
		
		if (text.startsWith(".md player")) {
			String[] var = text.split(" ");
			MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "---SearchResult---");
			
			if (var.length == 2) {
				pack.getGroup().sendMsg(MsgSpawner.newAtToast(pack.getMember().getUin(),"请输入玩家!"));
				return;
			}
			
			Connection connection = Jsoup.connect("https://api.musedash.moe/player/" + var[2]);
			connection.ignoreContentType(true);
			JSONObject callback = null;
			try {
				callback = new JSONObject(connection.execute().body());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!callback.optString("rl").equals("NaN")) {
				JSONObject index =  callback.optJSONObject("user");
				col.putText("\r\n昵称:" + index.optString("nickname"));
				col.putText("\r\n相对等级:" +String.format("%.3f", callback.optDouble("rl")) + "%"); 
				
				JSONArray acc = callback.optJSONArray("plays");
				double allacc = 0;
				for (int i = 0; i < acc.length();i++) {
					try {
						allacc += acc.getJSONObject(i).optDouble("acc");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				col.putText("\n平均准度:" + String.format("%.3f", allacc / acc.length()));
				col.putText("\n注册时间:" + index.optString("created_at"));
				col.putText("\n最后上线:" + index.optString("updated_at"));
				
			} else {
				col.putText("\n找不到这个uuid代表的玩家!");
			}
			pack.getGroup().sendMsg(col);
		}
		
		if (text.startsWith(".md search")) {
			String[] var = text.split(" ");
			MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "---SearchResult---");
			
			if (var.length == 2) {
				pack.getGroup().sendMsg(MsgSpawner.newAtToast(pack.getMember().getUin(),"请输入玩家!"));
				return;
			}
			
			Connection connection = Jsoup.connect("https://api.musedash.moe/search/" + var[2]);
			connection.ignoreContentType(true);
			JSONArray array = null;
			try {
				array = new JSONArray(connection.execute().body());
			} catch (Exception ignored) {}
			if (array.length() != 0) {
				int max = Math.min(array.length(), 10);
				
				for (int i = 0; i < max; i++) {
					col.putText("\r\n" + array.optJSONArray(i).optString(0) + "---" + array.optJSONArray(i).optString(1));
				}
				if (max >= 10) {
					col.putText("\r\n...等" + (array.length() - 10) + "个玩家\r\n请前往:https://musedash.moe/player处搜索");
				}
			} else {
				col.putText("\r\n无搜索结果");
			}
			

			pack.getGroup().sendMsg(col);
		}
	}

}

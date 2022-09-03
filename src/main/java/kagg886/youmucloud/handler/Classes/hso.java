package kagg886.youmucloud.handler.Classes;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.Message.MsgSpawner;
import kagg886.youmucloud.handler.MsgHandle;
import kagg886.youmucloud.util.PixivUtil;
import kagg886.youmucloud.util.ScoreUtil;
import kagg886.youmucloud.util.Utils;
import kagg886.youmucloud.util.cache.JSONArrayStorage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class hso extends MsgHandle {

	public static JSONArrayStorage imgs;
	private static final String saucenaoApikey = "7c949b57a221cb9c8e8fd5fe9055952193f58dcf";

	//private OrtSession session;


	public hso() {
		try {
			imgs = JSONArrayStorage.obtain("res/setu.json");
			//this.session = OrtEnvironment.getEnvironment().createSession( Statics.data_dir + "/res/model.onnx");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void handle(final GroupMsgPack pack) throws Exception {
		String text = pack.getMessage().getTexts();

//		if (text.startsWith(".hso detect")) {
//			ArrayList<String> img = Utils.getImage(pack);
//			if (img.size() == 0) {
//				this.sendMsg(pack, "请发送图片!");
//				return;
//			}
//
//			if (ScoreUtil.checkCoin(this,pack,5)) {
//				return;
//			}
//
//			BufferedImage outside = ImageIO.read(Jsoup.connect(img.get(0)).ignoreContentType(true).execute().bodyStream());
//			HashMap<String, OnnxTensor> map = new HashMap();
//			OnnxTensor t = OnnxTensor.createTensor(OrtEnvironment.getEnvironment(), ImageUtil.imageToMatrix(ImageUtil.scaleImg(outside, 256, 256)));
//			map.put("inputs", t);
//			OrtSession.Result Result = this.session.run(map);
//			float[][] outputProbs = (float[][])Result.get(0).getValue();
//			float[] probabilities = outputProbs[0];
//			float maxVal = Float.NEGATIVE_INFINITY;
//			String[] label = new String[]{"drawings", "hentai", "neutral", "porn", "sexy"};
//
//			int j;
//			for(int i = 0; i < probabilities.length; ++i) {
//				if (probabilities[i] > maxVal) {
//					maxVal = probabilities[i];
//				}
//
//				for(j = 0; j < probabilities.length - 1 - i; ++j) {
//					if (probabilities[j] > probabilities[j + 1]) {
//						float proTemp = probabilities[j];
//						probabilities[j] = probabilities[j + 1];
//						probabilities[j + 1] = proTemp;
//						String labelTemp = label[j];
//						label[j] = label[j + 1];
//						label[j + 1] = labelTemp;
//					}
//				}
//			}
//
//			List<SortItem> result = new ArrayList();
//
//			for(j = 0; j < 5; ++j) {
//				SortItem si = new SortItem();
//				si.qqs.add(label[j]);
//				si.value = (int)(probabilities[j] * 100.0F);
//				result.add(si);
//			}
//
//			result.sort(UpSorter.INSTANCE);
//			t.close();
//			MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), new String[]{"涩图检测结果如下:"});
//			Iterator var56 = result.iterator();
//
//			while(var56.hasNext()) {
//				SortItem s = (SortItem)var56.next();
//				col.putText("\n");
//				col.putText(s.qqs.get(0));
//				col.putText("的分数:");
//				col.putText(String.valueOf(s.value));
//			}
//
//			pack.getGroup().sendMsg(col);
//		}

		if (text.startsWith(".hso pixiv down ")) {
			String[] var = text.split(" ");
			if (var.length == 5) {
				sendMsg(pack,"请输入pid!");
				return;
			}

			if (ScoreUtil.checkCoin(this,pack,5)) {
				return;
			}

			Connection conn = PixivUtil.getPixivConnection("https://www.pixiv.net/ajax/illust/" + var[3] +"/pages?lang=zh");
			JSONObject source = new JSONObject(conn.execute().body());
			JSONArray imgList = source.optJSONArray("body");
			Utils.service.execute(() -> {
				MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(),"下载链接如下:");
				for (int i = 0; i < imgList.length(); i++) {
					col.putText("\n");
					try {
						col.putText(PixivUtil.PUrldownload(var[3] + "_original_" + (i+1),imgList.optJSONObject(i).optJSONObject("urls").optString("original")));
					} catch (Exception e) {
						col.putText(imgList.optJSONObject(i).optJSONObject("urls").optString("original") + "下载失败");
					}
				}
				pack.getGroup().sendMsg(col);
			});
		}

		if (text.startsWith(".hso saucenao")) {
			if (!pack.getMessage().containMsgType(MsgCollection.MsgType.img)) {
				sendMsg(pack,"请发送图文消息!");
				return;
			}

			if (ScoreUtil.checkCoin(this,pack,3)) {
				return;
			}

			String link = "https://saucenao.com/search.php?api_key=" +
					saucenaoApikey +
					"&db=999&output_type=2&testmode=1&numres=16&url=" +
					URLEncoder.encode(Utils.getImage(pack).get(0), "UTF-8");
			JSONObject callback = new JSONObject(Jsoup.connect(link).ignoreContentType(true).method(Connection.Method.GET).execute().body());
			JSONObject header = callback.optJSONObject("header");
			JSONArray result = callback.optJSONArray("results");
			if (header.optInt("status") == 0) {
				MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(),"识别结果如下:\n");
				int count = result.length();
				if (count > 5) {
					count = 5;
				}
				JSONObject unit_header,unit_result;
				for (int i = 0; i < count; i++) {
					unit_header = result.optJSONObject(i).optJSONObject("header");
					unit_result = result.optJSONObject(i).optJSONObject("data");
					col.putText("\n" + i + ":" + unit_header.optString("similarity"));
					col.putImage(unit_header.optString("thumbnail"));
					col.putText(unit_result.optString("title"));
					try {
						col.putText(unit_result.optJSONArray("ext_urls").optString(0));
					} catch (Exception t) {
						col.putText("暂无链接");
					}
				}
				col.putText("\n6s内调用余额:" + header.optInt("short_remaining") + "/" + header.optString("short_limit"));
				col.putText("\n1d内调用余额:" + header.optInt("long_remaining") + "/" + header.optString("long_limit"));
				pack.getGroup().sendMsg(col);
			} else {
				sendMsg(pack,"识别失败!\n错误码:" + header.optInt("status") + "\n错误信息:" + header.optString("message"));
			}
		}

		if (text.startsWith(".hso pixiv sfp")) {
			String[] var = text.split(" ");
			if (var.length == 3) {
				sendMsg(pack,"请输入pid!");
				return;
			}

			if (ScoreUtil.checkCoin(this,pack,3)) {
				return;
			}

			Connection conn = PixivUtil.getPixivConnection("https://www.pixiv.net/artworks/" + var[3]);
			Elements scripts = conn.get().getElementsByTag("meta");
			MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(),"");
			for (Element script : scripts) {
				if (script.id().equals("meta-preload-data")) {
					sendMsg(pack,"解析完毕!\n发送图片的过程可能有些慢,还请耐心等待~");
					JSONObject source = new JSONObject(script.attr("content"));
					source = source.optJSONObject("illust").optJSONObject(var[3]);
					col.putText("\ntitle:" + source.optString("illustTitle"));
					col.putText("\nDescription:" + source.optString("illustComment"));
					col.putText("\nView:" + source.optInt("bookmarkCount"));
					col.putText("\nLike:" + source.optString("likeCount")); //解析标题简介和点赞

					conn = PixivUtil.getPixivConnection("https://www.pixiv.net/ajax/illust/" + var[3] +"/pages?lang=zh");
					source = new JSONObject(conn.execute().body());

					JSONArray imgList = source.optJSONArray("body");
					for (int i = 0; i < imgList.length(); i++) {
						try {
							col.putImage(PixivUtil.PUrldownload(var[3] + "_regular_" + i,imgList.optJSONObject(i).optJSONObject("urls").optString("regular")));
						} catch (Exception e) {
							col.putText("\n" + imgList.optJSONObject(i).optJSONObject("urls").optString("regular"));
						}
					}
					pack.getGroup().sendMsg(col);
					break;
				}
			}

		}

		if (text.startsWith(".hso pixiv sfk")) {
			String[] var = text.split(" ");
			if (var.length <= 3) {
				pack.getGroup().sendMsg(MsgSpawner.newAtToast(pack.getMember().getUin(), "请输入关键词!"));
				return;
			}

			String url = String.format("https://www.pixiv.net/touch/ajax/search/illusts?include_meta=1&s_mode=s_tag&type=all&lang=zh&word=" + var[3]);

			Connection.Response response;
			try {
				response = PixivUtil.getPixivConnection(url).execute();
			} catch (Exception e) {
				sendMsg(pack,"搜索失败!");
				return;
			}
			JSONObject source = new JSONObject(response.body());
			if (source.optBoolean("error")) {
				sendMsg(pack, "请求发生错误!");
				return;
			}
			MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "");
			JSONArray lists = source.optJSONObject("body").optJSONArray("illusts");

			if (lists.length() == 0) {
				response = PixivUtil.getPixivConnection("https://www.pixiv.net/touch/ajax/search/autocomplete?keyword=" + var[3] + "&lang=zh").execute();
				source = new JSONObject(response.body());
				lists = source.optJSONObject("body").optJSONArray("candidates");
				col.putText("没能找到需要的图片");
				if (lists.length() == 0) {
					pack.getGroup().sendMsg(col);
					return;
				}
				col.putText("，建议尝试以下关键词:");
				for (int o = 0; o < lists.length(); o++) {
					source = lists.optJSONObject(o);
					col.putText(String.format("%s(%s)",source.optString("tag_name"),source.optString("tag_translation")));
					if (o != lists.length() - 1) {
						col.putText(",");
					}
				}
				pack.getGroup().sendMsg(col);
				return;
			}

			int pix = Math.min(10, lists.length());
			for (int o = 0; o < pix; o++) {
				source = lists.optJSONObject(o);
				col.putText("\n——————");
				col.putText("\nid:" + source.optString("id"));
				col.putText("\ntitle:" + new String(source.optString("title").getBytes(), StandardCharsets.UTF_8));

				try {
					col.putImage(PixivUtil.PUrldownload(source.optString("id"),source.optString("url_s")));
				} catch (Exception e) {
					col.putText("\nurl:" + source.optString("url_s"));
				}

			}
			pack.getGroup().sendMsg(col);
		}

		if (text.startsWith(".hso img")) {
			String[] s = text.split(" ");
			int yo = 1;
			try {
				yo  =Integer.parseInt(s[2]);
				if (yo > 5) {
					sendMsg(pack, "单次最多发送5张涩图!");
					return;
				}
			} catch (Exception ignored) {
			}

			if (ScoreUtil.checkCoin(this,pack,yo)) {
				return;
			}

			MsgCollection coll = new MsgCollection();
			for (int i = 1 ; i <= yo;i++) {
				JSONObject image = imgs.optJSONObject(Utils.random.nextInt(imgs.length()));
				coll.putImage(image.optString("link"));
				coll.putText("\nid:" + image.optString("id"));
			}
			pack.getGroup().sendMsg(coll);
		}
		
	}

}
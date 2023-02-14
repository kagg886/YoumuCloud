package kagg886.youmucloud.handler.group.classes;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.Message.MsgSpawner;
import kagg886.youmucloud.handler.group.GroupMsgHandle;
import kagg886.youmucloud.util.ScoreUtil;
import tax.cute.minecraftserverping.MCPing;
import tax.cute.minecraftserverpingbe.MCBePing;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class MC extends GroupMsgHandle {

    @Override
    public void handle(GroupMsgPack pack) throws Exception {
        String text = pack.getMessage().getTexts();
		
		if (text.startsWith(".mc motd ")) {
			String[] var = text.split(" ");
			String type,host;
			int port;
			try {
				type = var[2];
				if (!type.equals("je") && !type.equals("be")) {
                    throw new Exception("格式识别失败!正确的格式为:.mc motd [je/be] [ip] <端口>");
				}
			} catch (Exception e) {
				sendMsg(pack, e.getMessage());
				return;
			}

			try {
				if (var[3].contains(":")) {
					host = var[3].split(":")[0];
					port = Integer.parseInt(var[3].split(":")[1]);
				} else {
					host = var[3];
					if (type.equals("je")) {
						port = 25565;
					} else {
						port = 19132;
					}
				}
			} catch (Exception e) {
                sendMsg(pack, "格式识别失败!正确的格式为:.mc motd [je/be] [ip] <端口>");
				return;
			}

			if (ScoreUtil.checkCoin(this,pack,3)) {
				return;
			}

			if (type.equals("je")) {
				MCPing ping;
				try {
					ping = MCPing.getMotd(host,port,10000);
					MsgCollection msg = MsgSpawner.newAtToast(pack.getMember().getUin(),"ip:" + host + ":" + port);
					msg.putText("\n人数:" + ping.getOnline_players() + "/" + ping.getMax_players());
					msg.putText("\n延迟:" + ping.getDelay() + "ms");
					msg.putText("\n版本:" + ping.getVersion_name() + "(" + ping.getVersion_protocol() + ")");
					msg.putText("\n模组类型:" + ping.getType());
					msg.putText("\n模组列表:" + ping.getModList().toString());
					pack.getGroup().sendMsg(msg);
				} catch (Exception e) {
					expectionHandler(pack,e);
				}
			}

			if (type.equals("be")) {
				MCBePing ping;
				try {
					ping = MCBePing.getMotd(host,port,10000);
					MsgCollection msg = MsgSpawner.newAtToast(pack.getMember().getUin(),"ip:" + host + ":" + port);
					msg.putText("\n人数:" + ping.getOnline_players() + "/" + ping.getMax_players());
					msg.putText("\n延迟:" + ping.getDelay() + "ms");
					msg.putText("\n版本:" + ping.getVersion());
					msg.putText("\n服务端类型:" + ping.getType());
					pack.getGroup().sendMsg(msg);
				} catch (Exception e) {
					expectionHandler(pack,e);
				}
			}
			
//			int port = 25565;
//
//			if (var.length == 4) {
//				try {
//					port = Integer.parseInt(var[3]);
//				} catch (NumberFormatException e) {
//					col.putText("\r\n(端口设置有误,已自动设置为25565)");
//				}
//
//			}
//
//			JSONObject callback = null;
//			try {
//				callback = MinecraftPing.getPing(new MinecraftPingOptions().setHostname(var[2]).setPort(port).setTimeout(10000));
//				Iterator<String> keys = callback.keys();
//				String key = null;
//				while (keys.hasNext()) {
//					key = keys.next();
//
//
//					if (key.equals("modinfo")) {
//						col.putText("\r\n模组端类型:" + callback.optJSONObject(key).optString("type","Unknown"));
//						JSONArray mods = callback.optJSONObject(key).optJSONArray("modList");
//						if (mods.length() != 0) {
//							col.putText("\r\n模组列表:");
//							for (int i = 0; i < mods.length(); i++) {
//								col.putText(mods.optJSONObject(i).optString("modid") + "(" +mods.optJSONObject(i).optString("version") + "),");
//							}
//						}
//					}
//
//					if (key.equals("players")) {
//						col.putText("\r\n在线人数:" + callback.optJSONObject(key).optInt("online") + "/" + callback.optJSONObject(key).optInt("max"));
//					}
//
//					if (key.equals("description")) {
//						StringBuilder motds = new StringBuilder();
//						col.putText("\r\nmotd:");
//						try {
//							JSONObject descriptions = callback.getJSONObject(key);
//							JSONArray extra = descriptions.getJSONArray("extra");
//							for (int i = 0; i < extra.length(); i++) {
//								motds.append(extra.optJSONObject(i).optString("text"));
//							}
//						} catch (Exception e) { //description可能是文字
//							motds.append(callback.optString(key));
//						}
//
//						JSONArray sample = callback.optJSONObject("players").optJSONArray("sample");
//						if (sample != null) {
//							Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
//					        Matcher m = p.matcher(sample.toString());
//					        if (m.find()) { //motd存放在demo player里
//								for (int i = 0; i < sample.length(); i++) {
//									motds.append("\r\n");
//									motds.append(sample.getJSONObject(i).optString("name"));
//								}
//							}
//						}
//
//						col.putText(motds.toString());
//					}
//
//					if (key.equals("version")) {
//						col.putText("\r\n服务端类型:" + callback.optJSONObject(key).optString("name"));
//					}
//
//				}
//			} catch (Exception e) {
//				if (e instanceof SocketTimeoutException) {
//					col.putText("\r\n连接超时!");
//				} else if (e instanceof UnknownHostException) {
//					col.putText("\r\n无法连接到服务器!");
//				} else {
//					if (callback == null) {
//						return;
//					}
//					col.putText("\r\n发生未知错误\n请联系1up解决");
//				}
//			}
//			pack.getGroup().sendMsg(col);
//			return;
		}
	}

	private void expectionHandler(GroupMsgPack pack, Exception e) {
		if (e instanceof SocketTimeoutException) {
			sendMsg(pack,"连接超时!");
		}
		if (e instanceof UnknownHostException) {
			sendMsg(pack,"该host无法连接");
		}

		if (e instanceof SocketException) {
			sendMsg(pack,"端口号可能是错误的,若您未指认端口号,请指认一个");
		}
		sendMsg(pack,"在获取motd时发现未知错误!\n" + e.getMessage());
	}

}

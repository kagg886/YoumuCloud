package com.kagg886.youmucloud_mirai.websocket;

import com.kagg886.youmucloud_mirai.MsgHandler;
import com.kagg886.youmucloud_mirai.YoumuConfig;
import kagg886.qinternet.Message.MsgCollection;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.data.MessageChain;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.nio.channels.NotYetConnectedException;
import java.util.Map;
import java.util.Objects;

public class Connection extends WebSocketClient {
	private final long qid;
	@Override
	public void onOpen(ServerHandshake p1) {
	}

	@Override
	public void onMessage(String col) {
		if (YoumuConfig.INSTANCE.isDisableTheDataLog()) {
			MessageCenter.sendLog(MessageCenter.Logger.Server, "recv->:" + col);
		}
		try {
			final Action pack = Action.decodeAction(col);
			String action = Objects.requireNonNull(pack).getAction();

			if (action.equals("memberMute")) {
				int time = pack.optInt("time");
				NormalMember person = Objects.requireNonNull(Bot.getInstance(qid).getGroup(pack.optLong("groupid"))).getMembers().get(pack.optLong("person"));
				if (time == 0) {
					Objects.requireNonNull(person).unmute();
					return;
				}
				Objects.requireNonNull(person).mute(time * 60);
			}

			if (action.equals("sendGroupMsg")) {
				MsgCollection c = new MsgCollection(pack.optJSONArray("msg").toString());
				MessageChain m = MessageCenter.QITOMIRAIFORGROUP(c,Bot.getInstance(qid).getGroup(pack.optLong("groupid")));
				Objects.requireNonNull(Bot.getInstance(qid).getGroup(pack.optLong("groupid"))).sendMessage(m);

			}
			
			if (action.startsWith("getGroups")) {
				ContactList<Group> groups =  Bot.getInstance(qid).getGroups();
				JSONArray array = new JSONArray();
				for (Group g : groups) {
					array.put(g.getId());
				}
				pack.put("callback",array);
				send(pack.toString());
			}

			if (action.startsWith("getMembers")) {
				ContactList<NormalMember> groups =  Bot.getInstance(qid).getGroup(pack.optLong("groupid")).getMembers();
				JSONArray array = new JSONArray();
				for (NormalMember g : groups) {
					array.put(g.getId());
				}
				pack.put("callback",array);
				send(pack.toString());
			}
			
			if (action.equals("log")) {
				MessageCenter.sendLog(MessageCenter.Logger.Server,pack.getMsg());
			}
		} catch (JSONException ignored) {}
		
	}

	@Override
	public void onClose(int p1, String p2, boolean p3) {
		MessageCenter.sendLog(MessageCenter.Logger.Client, "云服务器连接中断!原因:(" + p1 + "):" + p2);
		new Thread(() -> {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException ignored) {}
			MsgHandler.initServer(false,qid);
		}).start();
	}

	@Override
	public void onError(Exception p1) {
		StringWriter w = new StringWriter();
		PrintWriter wr = new PrintWriter(w);
		p1.printStackTrace(wr);
		MessageCenter.sendLog(MessageCenter.Logger.Client,"客户端发生异常\n" + w);
	}

	public Connection(URI uri,long qid,Map<String,String> map) {
		super(uri,map);
		this.qid = qid;
	}

	@Override
	public void send(String text) throws NotYetConnectedException {
		super.send(text);
		if (YoumuConfig.INSTANCE.isDisableTheDataLog()) {
			MessageCenter.sendLog(MessageCenter.Logger.Client, "send->:" + text);
		}
	}
	
	
}

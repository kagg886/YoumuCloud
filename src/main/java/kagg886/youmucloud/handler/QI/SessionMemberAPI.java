package kagg886.youmucloud.handler.QI;

import kagg886.qinternet.Interface.MemberAPI;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.youmucloud.Client;
import org.json.JSONObject;

import java.io.IOException;

public class SessionMemberAPI implements MemberAPI {
	private final Client sess;
	
	public SessionMemberAPI (Client session) {
		this.sess = session;
	}
	
	@Override
	public boolean kick(long arg0, long arg1) {
		Action action = new Action("memberKick");
		action.put("groupid", arg0);
		action.put("person", arg1);
		return sendMsg(action);
	}

	@Override
	public boolean mute(long arg0, long arg1, int arg2) {
		Action action = new Action("memberMute");
		action.put("groupid", arg0);
		action.put("person", arg1);
		action.put("time", arg2);
		return sendMsg(action);
	}

	@Override
	public boolean sendLike(long arg0, long arg1, int arg2) {
		Action action = new Action("sendLike");
		action.put("person", arg1);
		action.put("count", arg2);
		return sendMsg(action);
	}

	@Override
	public boolean sendTempMsg(long arg0, long arg1, MsgCollection arg2) {
		Action action = new Action("memberTempMsg");
		action.put("person", arg1);
		action.put("msg",arg2);
		return sendMsg(action);
	}

	@Override
	public boolean setNick(long arg0, long arg1, String arg2) {
		Action action = new Action("membernickset");
		action.put("groupid", arg0);
		action.put("person", arg1);
		action.put("newnick", arg2);
		return sendMsg(action);
	}
	
	public boolean sendMsg(JSONObject str) {
		try {
			sess.sendMsg(str);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
}

package kagg886.youmucloud.handler.QI;

import kagg886.qinternet.Content.Group;
import kagg886.qinternet.Content.Member;
import kagg886.qinternet.Interface.GroupAPI;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.youmucloud.Client;
import kagg886.youmucloud.util.Utils;
import kagg886.youmucloud.util.WaitService;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class SessionGroupAPI implements GroupAPI {

	private final Client sess;

	public SessionGroupAPI(Client session) {
		this.sess = session;
	}

	@Override
	public void exit(long arg0) {
		Action action = new Action("exitGroup");
		action.put("groupid", arg0);
		sendMsg(action);
	}

	@Override
	public Group getGroup(long arg0) {
		return new Group(sess.getBot(),arg0,"null");
	}

	@Override
	public ArrayList<Long> getGroups() {
		Action action = new Action("getGroups_" + Utils.random.nextInt());
		sendMsg(action);

		JSONArray array;
		try {
			array = new JSONArray(WaitService.wait(action.getAction()));
		} catch (JSONException ignored) {
			return null;
		}
		ArrayList<Long> q = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			q.add(array.optLong(i));
		}
		if (array.length() == 0) {
			return null;
		}
		return q;
	}

	@Override
	public Member getMember(long arg0, long arg1) {
		return null;
	}

	@Override
	public ArrayList<Long> getMembers(long arg0) {
		Action action = new Action("getMembers_" + Utils.random.nextInt());
		action.put("groupid",arg0);
		sendMsg(action);
		JSONArray array;
		try {
			array = new JSONArray(WaitService.wait(action.getAction()));
		} catch (JSONException ignored) {
			return null;
		}
		ArrayList<Long> q = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			q.add(array.optLong(i));
		}
		if (array.length() == 0) {
			return null;
		}
		return q;
	}

	@Override
	public void sendMsg(long arg0, MsgCollection arg1) {
		Action action = new Action("sendGroupMsg");
		action.put("groupid", arg0);
		action.put("msg", arg1);
		synchronized (getClient()) {
			sendMsg(action);
		}
//		try {
//			Utils.writeStringToFile(Statics.data_dir + "/data/" + arg0 + "/latest.txt",arg1.toString());
//		} catch (IOException ignored) {}
	}

	@Override
	public boolean setAllmute(long arg0, boolean arg1) {
		Action action = new Action("setGroupAllMute");
		action.put("groupid", arg0);
		action.put("status", arg1);
		return sendMsg(action);
	}

	public boolean sendMsg(Action action) {
		try {
			sess.sendMsg(action);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public Client getClient() {
		return sess;
	}

}

package kagg886.youmucloud.handler;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.Message.MsgSpawner;
import kagg886.qinternet.QInternet;
import kagg886.youmucloud.handler.QI.Action;
import kagg886.youmucloud.handler.QI.SessionGroupAPI;
import kagg886.youmucloud.handler.QI.YoumuUser;
import org.json.JSONObject;

public abstract class MsgHandle {
	
	public abstract void handle(GroupMsgPack pack) throws Exception;
	public void sendMsg(GroupMsgPack p,String... msg) {
		p.getGroup().sendMsg(MsgSpawner.newAtToast(p.getMember().getUin(), msg));
	}
	
	public void sendClientLog(GroupMsgPack p,String log) {
		Action action = new Action("log");
		action.put("msg",log);
		SessionGroupAPI groupAPI = (SessionGroupAPI) QInternet.findBot(p.getGroup().getBotQQ()).getGroupAPI();
		groupAPI.sendMsg(action);
	}
	
	public void sendMsg(GroupMsgPack pack,MsgCollection col) {
		sendMsg(pack, col.getTexts());
	}

	public <T> T getParam(GroupMsgPack pc, String key,T defaultValue) {
		T t = (T) getParams(pc).opt(key);
		if (t == null) {
			return defaultValue;
		}
		return t;
	}

	public JSONObject getParams(GroupMsgPack pc) {
		return ((YoumuUser) QInternet.findBot(pc.getMember().getBotQQ())).getClient().getHeaders();
	}

}

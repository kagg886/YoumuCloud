package kagg886.youmucloud.handler.QI;

import kagg886.qinternet.Content.QQBot;
import kagg886.youmucloud.Client;

import java.io.IOException;

public class YoumuUser extends QQBot {
	
	private final Client client;
	
	public YoumuUser(long qq,Client session) {
		super(qq);
		this.setGroupAPI(new SessionGroupAPI(session));
		this.setMemberAPI(new SessionMemberAPI(session));
		this.client = session;
	}
	
	public Client getClient() {
		return client;
	}

	public boolean sendMsg(Action action) {
		try {
			client.getSession().getBasicRemote().sendText(action.toString());
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}

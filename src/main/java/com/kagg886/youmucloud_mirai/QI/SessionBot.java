package com.kagg886.youmucloud_mirai.QI;

import com.kagg886.youmucloud_mirai.websocket.Connection;
import kagg886.qinternet.Content.QQBot;

public class SessionBot extends QQBot {
    private Connection con;
    public SessionBot(long qid, Connection conn) {
        super(qid);
        this.con = conn;
    }

    public Connection getConnection() {
        return con;
    }

    public void setConnection(Connection con) {
        this.con = con;
    }
}

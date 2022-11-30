package com.kagg886.youmucloud.bot;

import kagg886.qinternet.Content.QQBot;

public class SessionBot extends QQBot {
    private final BotConnection conn;

    public SessionBot(BotConnection conn) {
        super(conn.getQid());
        this.conn = conn;
    }

    public BotConnection getConn() {
        return conn;
    }
}

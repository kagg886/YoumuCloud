package com.kagg886.youmucloud.bot;

import kagg886.qinternet.Message.MsgCollection;

//消息中转类
public abstract class AbstractMessageCenter<RECEIVE, SEND> {
    public abstract MsgCollection NativeToQI(RECEIVE msg);
    public abstract SEND QIToNative(long gid, MsgCollection msg);

    public abstract RECEIVE sendMsg(SEND t);

    public abstract void sendLog(LoggerLevel level,String msg);

    public abstract String getPlatform();

    public enum LoggerLevel {
        Server,Client
    }

    protected AbstractMessageCenter() {

    }
}

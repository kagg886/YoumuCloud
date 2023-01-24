package kagg886.youmucloud;

import kagg886.qinternet.Content.QQBot;
import kagg886.qinternet.Message.GroupMemberPack;
import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.QInternet;
import kagg886.youmucloud.handler.HandlerMessage;
import kagg886.youmucloud.handler.QI.Action;
import kagg886.youmucloud.handler.QI.SessionGroupAPI;
import kagg886.youmucloud.handler.QI.YoumuUser;
import kagg886.youmucloud.util.Statics;
import kagg886.youmucloud.util.Utils;
import kagg886.youmucloud.util.WaitService;
import org.json.JSONException;
import org.json.JSONObject;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * @projectName: YoumuServer
 * @package: kagg886.youmucloud
 * @className: Client
 * @author: kagg886
 * @description: 妖梦云连接终端
 * @date: 2022/12/3 21:11
 * @version: 1.0
 */
@ServerEndpoint(value = "/api/{username}", configurator = GetHttpSessionConfigurator.class)
public class Client {
    private Session session;
    private QQBot boot;
    private JSONObject headers;

    public QQBot getBot() {
        return boot;
    }

    @OnClose
    public void onClose() {
        YoumuUser user;
        for (QQBot bot : QInternet.getList()) {
            user = (YoumuUser) bot;
            if (user.getClient() == this) {
                QInternet.removeBot(bot);
                Utils.log("系统提示", boot.getId() + "与服务器断开连接");
                return;
            }
        }

    }

    @OnMessage
    public void onMessage(String string, Session session) throws JSONException {
        Action action = new Action(string, false);
        Statics.ReceiveDataPack++;

        if (action.getAction().equals("onMember")) {
            GroupMemberPack object = new GroupMemberPack(action.optString("msg"));
            HandlerMessage.INSTANCE.onMemberMsg(object);
        }

        //收取群消息
        if (action.getAction().equals("onGroupMsg")) {
            GroupMsgPack object = new GroupMsgPack(action.optString("msg"));
            HandlerMessage.INSTANCE.onGroupMsg(object);
        }

        //获取群列表的回调api
        if (action.getAction().startsWith("getGroups")) {
            WaitService.addCall(action.getAction(), action.optString("callback"));
        }

        //获取成员列表的回调api
        if (action.getAction().startsWith("getMembers")) {
            WaitService.addCall(action.getAction(), action.optString("callback"));
        }

    }

    @OnOpen
    public synchronized void onOpen(@PathParam("username") String username, Session session, EndpointConfig config) throws Exception {
        long name = Long.parseLong(username);
        String header = (String) config.getUserProperties().get("header");
        headers = new JSONObject(header);

        for (QQBot bot : QInternet.getList()) {
            if (bot.getId() == name) {
                QInternet.removeBot(bot);
                //((YoumuUser) bot).getClient().session.close();
                Utils.log("系统提示", name + "在连接时已经拥有实例，已清除该实例");
            }
        }
        boot = new YoumuUser(name, this);
        this.session = session;
        QInternet.addBot(boot);
        Utils.log("header", header);
        Utils.log("系统提示", name + "与服务器取得连接");
        Action action = new Action("log");
        action.put("msg", Utils.loadStringFromFile(Statics.data_dir + "broadcast.txt"));
        sendMsg(action);
        if (headers.optInt("ver", 0) < Statics.lowestVersion) {
            action.put("msg", "\n[警告]:当前版本因为兼容性而暂停使用，请下载最新版YoumuCloud");
            sendMsg(action);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        Utils.log("系统提示", "程序出错");
        if (throwable.getMessage() == null) {
            return;
        }
        throwable.printStackTrace();
        if (throwable.getMessage().contains("Connection reset by peer") || throwable.getMessage().contains("Broken pipe") || throwable.getMessage().contains("Connection timed out")) {
            for (QQBot bot : QInternet.getList()) {
                SessionGroupAPI api = (SessionGroupAPI) bot.getGroupAPI();
                if (this == api.getClient()) {
                    QInternet.removeBot(bot);
                    try {
                        session.close();
                    } catch (Exception ignored) {
                    }
                    Utils.log("系统提示", "客户端出错," + boot.getId() + "自动与服务器断开连接");
                    return;
                }
            }
        }
        Action action = new Action("log");
        action.put("msg", Utils.PrintException(throwable));
        try {
            sendMsg(action);
        } catch (Exception ignored) {
        }

    }

    public void sendMsg(JSONObject msg) throws IOException {
        Statics.SendDataPack++;
        session.getBasicRemote().sendText(msg.toString());
    }

    public JSONObject getHeaders() {
        return headers;
    }

    public Session getSession() {
        return session;
    }
}

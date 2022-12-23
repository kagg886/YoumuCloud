package kagg886.youmucloud.handler.memberevent;

import kagg886.qinternet.Message.GroupMemberPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.Message.MsgSpawner;
import kagg886.qinternet.QInternet;
import kagg886.youmucloud.handler.QI.Action;
import kagg886.youmucloud.handler.QI.SessionGroupAPI;
import kagg886.youmucloud.handler.QI.YoumuUser;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * @projectName: YoumuServer
 * @package: kagg886.youmucloud.handler.memberevent
 * @className: MemberMsgHandle
 * @author: kagg886
 * @description: 管理其他的群事件
 * @date: 2022/12/5 20:59
 * @version: 1.0
 */
public abstract class MemberMsgHandle {
    public static final LinkedList<MemberMsgHandle> MEMBER_MSG_HANDLES = new LinkedList<>();

    public abstract void handle(GroupMemberPack pack) throws Exception;

    public void sendMsg(GroupMemberPack p, String... msg) {
        p.getGroup().sendMsg(MsgSpawner.newAtToast(p.getMember().getUin(), msg));
    }

    public void sendClientLog(GroupMemberPack p, String log) {
        Action action = new Action("log");
        action.put("msg", log);
        SessionGroupAPI groupAPI = (SessionGroupAPI) QInternet.findBot(p.getGroup().getBotQQ()).getGroupAPI();
        groupAPI.sendMsg(action);
    }

    public void sendMsg(GroupMemberPack pack, MsgCollection col) {
        sendMsg(pack, col.getTexts());
    }

    public <T> T getParam(GroupMemberPack pc, String key, T defaultValue) {
        T t = (T) getParams(pc).opt(key);
        if (t == null) {
            return defaultValue;
        }
        return t;
    }

    public JSONObject getParams(GroupMemberPack pc) {
        return ((YoumuUser) QInternet.findBot(pc.getMember().getBotQQ())).getClient().getHeaders();
    }

}

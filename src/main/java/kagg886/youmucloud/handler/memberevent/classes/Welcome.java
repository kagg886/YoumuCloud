package kagg886.youmucloud.handler.memberevent.classes;

import kagg886.qinternet.Message.GroupMemberPack;
import kagg886.youmucloud.handler.memberevent.MemberMsgHandle;

/**
 * @projectName: YoumuServer
 * @package: kagg886.youmucloud.handler.memberevent.classes
 * @className: Welcome
 * @author: kagg886
 * @description: 入群欢迎类
 * @date: 2022/12/5 21:18
 * @version: 1.0
 */
public class Welcome extends MemberMsgHandle {
    @Override
    public void handle(GroupMemberPack pack) throws Exception {
        sendMsg(pack, "欢迎来到本群w\n机器人指令请发送.help查看");
    }
}

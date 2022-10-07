package kagg886.youmucloud.handler.Classes;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.youmucloud.handler.MsgHandle;
import kagg886.youmucloud.util.Statics;

public class Help extends MsgHandle {

	@Override
	public void handle(GroupMsgPack pack) {
		String text = pack.getMessage().getTexts();

		if (text.equals(".menu")) {
			sendMsg(pack, "指令集已迁移,请打开下列网址查看:\nhttp://" + Statics.ip + "/youmu/text?path=commandList");
			return;
		}

		if (text.equals(".help")) {
			sendMsg(pack, "综合性工具类机器人,立志为使用者带来绝妙的体验\n"
					+ "官网:http://" + Statics.ip + "/youmu/HomePage"
					+ "插件问答群:572360632\n"
					+ "东方群(有音游浓度):973510746");
			return;
		}

		if (text.startsWith(".") && text.split(" ").length == 1) {
			sendMsg(pack, "指令集已迁移,请打开下列网址查看:\nhttp://" + Statics.ip + "/youmu/text?path=c/" + text.substring(1));
		}
	}
}

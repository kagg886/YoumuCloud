package kagg886.youmucloud.handler.Classes;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.QInternet;
import kagg886.youmucloud.handler.MsgHandle;
import kagg886.youmucloud.util.Statics;
import kagg886.youmucloud.util.Utils;

import java.util.List;

public class Help extends MsgHandle {

	@Override
	public void handle(GroupMsgPack pack) {
		String text = pack.getMessage().getTexts();

		if (text.equals(".ss")) {
			Utils.service.execute(() -> {
				List<Long> ss = QInternet.findBot(pack.getGroup().getBotQQ()).getGroupAPI().getMembers(pack.getGroup().getId());
				sendMsg(pack,ss.toString());
			});
		}

		if (text.equals(".menu")) {
			sendMsg(pack, "指令集已迁移,请打开下列网址查看:\nhttp://" + Statics.ip + "/youmu/text?path=commandList");
		}
		
		if (text.equals(".help")) {
			sendMsg(pack, "综合性工具类机器人,立志为使用者带来绝妙的体验\n"
					+ "官网:http://" + Statics.ip + "/youmu/HomePage"
					+ "插件问答群:572360632\n"
					+ "东方群(有音游浓度):973510746");
		}
	}
}

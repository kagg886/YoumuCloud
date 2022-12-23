package kagg886.youmucloud.handler.group.classes;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.youmucloud.handler.group.GroupMsgHandle;
import kagg886.youmucloud.util.Statics;
import kagg886.youmucloud.util.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @projectName: YoumuServer
 * @package: kagg886.youmucloud.handler.group.classes
 * @className: AutoReply
 * @author: kagg886
 * @description: 自动回复类
 * @date: 2022/12/5 21:24
 * @version: 1.0
 */
public class AutoReply extends GroupMsgHandle {

    private static HashMap<String, String> replys = new HashMap<>();


    @Override
    public void handle(GroupMsgPack pack) throws Exception {
        String text = pack.getMessage().getTexts();
        if (text.equals("update")) {
            load();
            sendMsg(pack, "重载成功!");
        }
        for (Map.Entry<String, String> unit : replys.entrySet()) {
            if (text.matches(unit.getKey())) {
                sendMsg(pack, unit.getValue());
                return;
            }
        }
    }

    private void load() {
        replys.clear();
        try {
            String[] t = Utils.loadStringFromFile(Statics.data_dir + "AutoReply.txt").split("\n\n");
            for (String replyUnit : t) {
                String title = replyUnit.split("\n")[0];
                String content = replyUnit.replace(title + "\n", "");
                replys.put(title, content);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

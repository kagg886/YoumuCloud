package kagg886.youmucloud.util;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.youmucloud.handler.group.GroupMsgHandle;
import kagg886.youmucloud.handler.group.classes.game.ScoreStatis;
import org.json.JSONException;

public class ScoreUtil {
    public static boolean checkCoin(GroupMsgHandle handle, GroupMsgPack pack, int data) throws JSONException {
        int score = ScoreStatis.exps.optInt(String.valueOf(pack.getMember().getUin()));
        if (score <= data) {
            handle.sendMsg(pack, "当前功能需要使用:" + data, "点exp哦~");
            return true;
        }
        score -= data;
        ScoreStatis.exps.put(String.valueOf(pack.getMember().getUin()), score);
        ScoreStatis.exps.save();
        return false;
    }
}

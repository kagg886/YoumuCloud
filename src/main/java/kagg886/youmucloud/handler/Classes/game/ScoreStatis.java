package kagg886.youmucloud.handler.Classes.game;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.youmucloud.handler.MsgHandle;
import kagg886.youmucloud.util.Utils;
import kagg886.youmucloud.util.cache.JSONObjectStorage;
import kagg886.youmucloud.util.sort.Sorter;

import java.util.Calendar;

public class ScoreStatis extends MsgHandle {

    public static JSONObjectStorage exps,timer;


    private Sorter ranks = new Sorter(exps);

    static {
        try {
            exps = JSONObjectStorage.obtain("data/exp/exp.json");
            timer = JSONObjectStorage.obtain("data/exp/timer.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(GroupMsgPack pack) throws Exception {
        String text = pack.getMessage().getTexts();

        if (text.equals(".xp rank")) {
            sendMsg(pack,ranks.analyze(pack.getMember().getUin()));
        }

        if (text.startsWith(".xp find ")) {
            String[] var = text.split(" ");
            if (var.length != 3) {
                sendMsg(pack,"请输入要查询的qq!");
                return;
            }

            String qid = String.valueOf(var[2]);
            int exp = exps.optInt(qid);
            int level = (exp - (exp % 50)) / 50 + 1;
            sendMsg(pack,"Ta的信息如下:" +
                    "\n[等级]:" + level +
                    "\n[EXP]:" + exp + "/" + level * 50
            );
        }

        if (text.equals(".xp info")) {
            String qid = String.valueOf(pack.getMember().getUin());
            int exp = exps.optInt(qid);
            int level = (exp - (exp % 50)) / 50 + 1;
            sendMsg(pack,"个人信息如下:" +
                    "\n[等级]:" + level +
                    "\n[EXP]:" + exp + "/" + level * 50
            );
        }

        if (text.equals(".xp sign")) {
            String qid = String.valueOf(pack.getMember().getUin());
            int last = timer.optInt(qid);
            int now = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            if (now == last) {
                sendMsg(pack,"您今天已经签到了\n请明天再来~");
                return;
            }

            int day = timer.optInt("statTime");
            int person = timer.optInt("statPerson",1);
            if (now != day) {
                timer.put("statTime",now);
                person = 1;
            }

            int exp = exps.optInt(qid);
            int add = Utils.random.nextInt(10) + 7;
            exp += add;
			person++;
            timer.put(qid,now);
            exps.put(qid,exp);
            sendMsg(pack,"签到成功~\n您是今天第" + person +  "个签到的\n获得exp:" + add);
            timer.put("statPerson",person);

            timer.save();
            exps.save();
        }
    }
}

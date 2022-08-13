package kagg886.youmucloud.handler.Classes.game;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.youmucloud.handler.MsgHandle;
import kagg886.youmucloud.util.cache.JSONObjectStorage;
import kagg886.youmucloud.util.Utils;
import kagg886.youmucloud.util.sort.SortItem;
import kagg886.youmucloud.util.sort.UpSorter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class ScoreStatis extends MsgHandle {

    public static JSONObjectStorage exps,timer;


    public static final ArrayList<SortItem> ranks = new ArrayList<>();
    public static int day = -1;

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
            int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            int value;
            if (day != today) {
                //刷新排行榜
                ranks.clear();
                String qq;
                for (Iterator<String> it = exps.keys(); it.hasNext();) {
                    qq = it.next();
                    value = exps.optInt(qq, -1);
                    label: {
                        for (SortItem i : ranks) {
                            if (i.value == value) { //次数相等直接加进这个集合
                                i.qqs.add(qq);
                                break label;
                            }
                        }
                        SortItem y = new SortItem();
                        y.value = value;
                        y.qqs.add(qq);
                        ranks.add(y);
                    }
                }
                ranks.sort(UpSorter.INSTANCE);
                day = today; //保证排行榜一天一换
            }

            StringBuilder buf = new StringBuilder("---<exp榜>---");
            value = 1;
            for (SortItem st : ranks) {
                if (value > 10) {
                    break;
                }
                buf.append("\n");
                buf.append("No.");
                buf.append(value);
                buf.append(":");
                buf.append(st.qqs.toString());
                buf.append("---");
                buf.append(st.value);
                value++;
            }

            value = 1;
            for (SortItem rank : ranks) {
                for (String qqs : rank.qqs) {
                    if (qqs.equals(String.valueOf(pack.getMember().getUin()))) {
                        buf.append("\n你的名次:").append(value).append("/").append(exps.length());
                        sendMsg(pack, buf.toString());
                        return;
                    }
                }
                value += rank.qqs.size();
            }
            buf.append("\n你从未签到过,故无名次~");
            sendMsg(pack,buf.toString());
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
            int now = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
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

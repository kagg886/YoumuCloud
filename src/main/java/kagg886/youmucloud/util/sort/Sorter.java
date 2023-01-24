package kagg886.youmucloud.util.sort;

import kagg886.youmucloud.util.Utils;
import kagg886.youmucloud.util.cache.JSONObjectStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class Sorter extends ArrayList<SortItem> {
    private int day = -1;
    private JSONObjectStorage exps;

    public Sorter(JSONObjectStorage exps) {
        this.exps = exps;
    }


    //只支持"qq":123类型
    public String analyze(long uin) {
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int value;
        if (day != today) {
            //刷新排行榜
            this.clear();
            String qq;
            for (Iterator<String> it = exps.keys(); it.hasNext();) {
                qq = it.next();
                value = exps.optInt(qq,-1);
                label: {
                    for (SortItem i : this) {
                        if (i.value == value) { //次数相等直接加进这个集合
                            i.qqs.add(qq);
                            break label;
                        }
                    }
                    SortItem y = new SortItem();
                    y.value = value;
                    y.qqs.add(qq);
                    this.add(y);
                }
            }
            this.sort(UpSorter.INSTANCE);
            day = today; //保证排行榜一天一换
        }

        StringBuilder buf = new StringBuilder("---<exp榜>---");
        value = 1;
        for (SortItem st : this) {
            if (value > 10) {
                break;
            }
            buf.append("\n");
            buf.append("No.");
            buf.append(value);
            buf.append(":[");
            for (String a : st.qqs) {
                buf.append(Utils.mosaicString(a, 3));
            }
            buf.append("]---");
            buf.append(st.value);
            value++;
        }

        value = 1;
        for (SortItem rank : this) {
            for (String qqs : rank.qqs) {
                if (qqs.equals(String.valueOf(uin))) {
                    buf.append("\n你的名次:").append(value).append("/").append(exps.length());
                    return buf.toString();
                }
            }
            value += rank.qqs.size();
        }
        buf.append("\n你从未签到过,故无名次~");
        return buf.toString();
    }
}

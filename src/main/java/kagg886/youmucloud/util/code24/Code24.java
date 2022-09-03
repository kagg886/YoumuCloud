package kagg886.youmucloud.util.code24;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Code24
{
    public String getCards() {
        return cards;
    }
    public Code24 setCards(String cards) {
        this.cards = cards;
		return this;
    }

    public String cards;

    private final List<String>   tempList = new ArrayList<>();

    private final List<String[]> opList   = new ArrayList<>();
    private final List<String[]> cardList = new ArrayList<>();

    public String calc() {
        String[] arrs = cards.split("\\D");
        if (arrs.length != 4) {
            return "参加计算的数必须为4个!";
        }

        //1.全排列
        permCard(arrs, 0, 3);

        String[] ops = { "+", "-", "*", "/" };
        permOp(ops, 0, 3);
		StringBuilder sb=new StringBuilder();
        for (String[] ta : cardList) {

            for (String[] to : opList) {
                List<String> list = create(ta, to);
                for (String s : list) {
                    //Object result = fel.eval(s);

                    String s24 = Js.js(s) + "";
                    float l24 = Float.parseFloat(s24);
                    if (l24 == 24) {
                        sb.append(s).append(",");
                    }
                }

            }
            if (!sb.toString().matches("")) {
                return sb.toString();
            }
        }
        return "无符合条件的计算式...";

    }

    //全排列
    public void permCard(String[] buf, int start, int end) {
        if (start == end) {// 当只要求对数组中一个字母进行全排列时，只要就按该数组输出即可
            tempList.addAll(Arrays.asList(buf).subList(0, end + 1));
            // System.out.println();
            String[] newBuf = new String[4];
            for (int i = 0; i < tempList.size(); i++) {
                newBuf[i] = tempList.get(i);
            }
            cardList.add(newBuf);
            tempList.clear();

        } else {
            for (int i = start; i <= end; i++) {
                String temp = buf[start];// 交换数组第一个元素与后续的元素
                buf[start] = buf[i];
                buf[i] = temp;

                permCard(buf, start + 1, end);// 后续元素递归全排列

                temp = buf[start];// 将交换后的数组还原
                buf[start] = buf[i];
                buf[i] = temp;
            }
        }

    }
    public void permOp(String[] buf, int start, int end) {
        String[] opArr = { "+", "-", "*", "/" };
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    String[] newBuf = new String[3];
                    newBuf[0] = opArr[i];
                    newBuf[1] = opArr[j];
                    newBuf[2] = opArr[k];
                    opList.add(newBuf);
                }
            }
        }

    }
    private List<String> create(String[] ta, String[] to) {
        List<String> list = new ArrayList<>();

        list.add(ta[0] + to[0] + ta[1] + to[1] + ta[2] + to[2] + ta[3]);
        list.add("(" + ta[0] + to[0] + ta[1] + ")" + to[1] + ta[2] + to[2] + ta[3]);
        list.add("(" + ta[0] + to[0] + ta[1] + to[1] + ta[2] + ")" + to[2] + ta[3]);
        list.add("(" + ta[0] + to[0] + ta[1] + ")" + to[1] + "(" + ta[2] + to[2] + ta[3] + ")");
        return list;

    }


}

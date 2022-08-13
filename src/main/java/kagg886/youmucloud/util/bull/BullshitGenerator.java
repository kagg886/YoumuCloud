package kagg886.youmucloud.util.bull;

import kagg886.youmucloud.util.cache.JSONObjectStorage;
import kagg886.youmucloud.util.Utils;
import org.json.JSONArray;
public class BullshitGenerator {
    private static JSONArray beforeList,boshList,famousList,afterList;

    static {
        try {
            JSONObjectStorage st = JSONObjectStorage.obtain("res/spawn/bulling.json");
            beforeList = st.optJSONArray("before");
            afterList = st.optJSONArray("after");
            boshList = st.optJSONArray("bosh");
            famousList = st.optJSONArray("famous");
        } catch (Exception ignored) {
        }
    }

    private static String insert(JSONArray array) {
        return array.optString(Utils.random.nextInt(array.length()));
    }

    public static String generate(String title, int length) {
        if (length > 1000) {
            length = 1000; // 默认生成 800 字文章
        }

        StringBuilder content = new StringBuilder();
        while (content.length() < length) {
            int num = (int) ((Math.random()) * 100);
            if (num < 10) {
                content.append("\n");
            } else if (num < 20) {
                content.append(insert(famousList)
                        .replace("a", insert(beforeList))
                        .replace("b", insert(afterList)));
            } else {
                content.append(insert(boshList));
            }
        }
        return content.toString().replace("x", title);
    }
}

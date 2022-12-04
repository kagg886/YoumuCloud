package kagg886.youmucloud.servlet.spawn;

import kagg886.youmucloud.util.cache.JSONObjectStorage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;

@WebServlet("/emojimix")
public class EmojiMix extends HttpServlet {
    public static String spawn(String emoji1, String emoji2) throws Exception {
        Document v1 = Jsoup.connect("https://apps.timwhitlock.info/unicode/inspect?s=" + URLEncoder.encode(emoji1, "UTF-8")).get();
        String hex1 = v1.getElementsByAttributeValue("class", "hex").get(0).text();
        v1 = Jsoup.connect("https://apps.timwhitlock.info/unicode/inspect?s=" + URLEncoder.encode(emoji2, "UTF-8")).get();
        String hex2 = v1.getElementsByAttributeValue("class", "hex").get(0).text();

        System.out.println(hex1);
        System.out.println(hex2);
        String date = null;
        JSONObject lists = JSONObjectStorage.obtain("res/spawn/emojimix.json");
        JSONArray emojis;
        String x;
        Iterator<String> keys = lists.keys();
        while (keys.hasNext()) {
            x = keys.next();
            emojis = lists.optJSONArray(x);
            for (int i = 0; i < emojis.length(); i++) {
                JSONObject temp = emojis.optJSONObject(i);
                String l = temp.optString("leftEmoji");
                String r = temp.optString("rightEmoji");
                if (hex1.equalsIgnoreCase(l) && hex2.equalsIgnoreCase(r)) {
                    date = temp.optString("date");
                    hex1 = l;
                    hex2 = r;
                }
            }
        }
        if (date == null) {
            return null;
        }
        return String.format("https://www.gstatic.com/android/keyboard/emojikitchen/%s/u%s/u%s_u%s.png", date, hex1, hex1, hex2);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.getWriter().println(spawn(URLDecoder.decode(req.getParameter("emoji1"), "UTF-8"), URLDecoder.decode(req.getParameter("emoji2"), "UTF-8")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

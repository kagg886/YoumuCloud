package kagg886.youmucloud.util;

import kagg886.youmucloud.util.cache.JSONObjectStorage;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

public class PixivUtil {

    public static String cookie;
    public static String userid;

    static {
        JSONObjectStorage s = null;
        try {
            s = JSONObjectStorage.obtain("res/Pixiv_Cookie.json");
            cookie = s.optString("cookie");
            userid = s.optString("user_id");
        } catch (Exception e) {
            Utils.log("debug","错误!" + e.getMessage());
        }
    }

    public static String PUrldownload(String pid,String purl) throws Exception {
        if (new File(Statics.data_dir + "imgcache/" + pid + ".jpg").exists()) {
            return "http://" + Statics.ip + "/youmu/Image?id=" + pid;
        }

        InputStream input = getPixivConnection(purl).execute().bodyStream();
        byte[] data = new byte[1024*100];  // 100KB缓冲区
        int length;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((length = input.read(data)) != -1) { //用原生方法会出现图片读不全的情况
            output.write(data, 0, length);
        }
        output.flush();
        output.close(); //关闭流;
        input.close();
        return ImageUtil.ImageToLink(ImageIO.read(new ByteArrayInputStream(output.toByteArray())),pid,null);
    }

    public static Connection getPixivConnection(String url) {
        return Jsoup.connect(url)
                .method(Connection.Method.GET)
                .timeout(60000)
                .ignoreContentType(true)
                .header("Referer","http://pixiv.net")
                .header("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"96\", \"Google Chrome\";v=\"96\"")
                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("sec-ch-ua-mobile", "?0")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36")
                .header("x-user-id", userid)// todo 换成你的id
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty")
                .header("Cookie", cookie);
    }
}

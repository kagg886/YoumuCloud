package kagg886.youmucloud.util.arc;

import kagg886.youmucloud.util.Statics;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import sun.font.FontDesignMetrics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @projectName: YoumuServer
 * @package: kagg886.youmucloud.util.arc
 * @className: ArcImageUtils
 * @author: kagg886
 * @description: arc管理类
 * @date: 2023/2/4 17:44
 * @version: 1.0
 */
public class ArcImageUtils {

    private static BufferedImage pure, far, lost;

    static {
        try {
            pure = ImageIO.read(new File(Statics.data_dir + "/res/arc/status/pure.png"));
            far = ImageIO.read(new File(Statics.data_dir + "/res/arc/status/far.png"));
            lost = ImageIO.read(new File(Statics.data_dir + "/res/arc/status/lost.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage getPersonInfo(JSONObject userInfo) throws IOException {
        String name = userInfo.optString("name");
        String avatar = "https://redive.estertion.win/arcaea/backstage/icons/" +
                userInfo.optString("character") +
                (userInfo.optBoolean("is_char_uncapped") && !userInfo.optBoolean("is_char_uncapped_override") && !userInfo.optBoolean("is_skill_sealed") ? "u" : "") + "_icon.png";
        double ptt = userInfo.optInt("rating") / 100.0;
        String userCode = userInfo.optString("user_code");
        long joinDate = userInfo.optLong("join_date");


        BufferedImage user = new BufferedImage(710 * 3, 300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = user.createGraphics();

        g2d.drawImage(ImageIO.read(Jsoup.connect(avatar).ignoreContentType(true).execute().bodyStream()),
                0, 0, 300, 300, null);
        g2d.setFont(new Font("微软雅黑", Font.BOLD, 80));
        g2d.setColor(Color.WHITE);
        g2d.drawString(name, 300, 100);

        FontMetrics fm = FontDesignMetrics.getMetrics(g2d.getFont());

        g2d.setFont(new Font("微软雅黑", Font.PLAIN, 40));
        g2d.drawString("<" + userCode + ">", 300 + fm.charsWidth(name.toCharArray(), 0, name.toCharArray().length), 100);

        g2d.setColor(Color.red);
        g2d.drawString("PTT:" + ptt, 300, 180);
        g2d.setColor(Color.WHITE);
        g2d.drawString(String.format("已加入Arc%d天", (System.currentTimeMillis() - joinDate) / 86400000), 300, 250);
        g2d.dispose();
        return user;
    }

    public static BufferedImage getSongUnit(ArcaeaB30Provider.Score data) throws IOException {
        BufferedImage tx = ImageIO.read(Jsoup.connect(data.imgUrl).ignoreContentType(true).execute().bodyStream());

        BufferedImage model = new BufferedImage(710, 310, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gra = model.createGraphics();
        gra.drawImage(tx, 0, 0, 256, 256, null);

        gra.drawImage(pure.getSubimage(0, 40, 114, 26), 256, 112, 114, 26, null);
        gra.drawImage(far.getSubimage(0, 40, 114, 26), 256, 152, 114, 26, null);
        gra.drawImage(lost.getSubimage(0, 40, 114, 26), 256, 192, 114, 26, null);
        gra.setColor(Color.WHITE);
        gra.setFont(new Font("Helvetica", Font.BOLD, 60));
        String title = data.title;
        gra.drawString(title.length() > 12 ? title.substring(0, 12) + "..." : title, 256, 60);
        gra.setFont(new Font("Helvetica", Font.PLAIN, 40));
        gra.setColor(new Color(196, 1, 177));
        gra.drawString(data.pure + "(" + data.pPure + ")", 400, 138);
        gra.setColor(new Color(255, 144, 42));
        gra.drawString(String.valueOf(data.far), 400, 178);
        gra.setColor(new Color(211, 14, 82));
        gra.drawString(String.valueOf(data.lost), 400, 218);

        gra.setColor(Color.BLUE);
        gra.drawString("PTT:", 263, 258);
        double g = (g = (19.54 * data.rating)) > 255 ? 255 : g;
        int r = (int) Math.sqrt(-(g * g) + 510 * g);
        gra.setColor(new Color(r, (int) g, 0));
        gra.drawString(String.format("%.2f", data.rating), 400, 258);
        gra.setColor(Color.CYAN);
        gra.drawString(String.valueOf(data.score), 520, 105);
        String diff = null;
        switch (data.diff) {
            case 0:
                diff = "Past";
                break;
            case 1:
                diff = "Present";
                break;
            case 2:
                diff = "Future";
                break;
            case 3:
                diff = "Beyond";
                break;
        }
        gra.drawString(diff + "(" + data.constant + ")", 256, 105);
        gra.setColor(Color.orange);
        gra.drawString("Played:" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data.time), 0, 295);

        gra.setColor(Color.lightGray);
        gra.drawRect(0, 0, model.getWidth(), model.getHeight());
        gra.dispose();
        return model;
    }
}

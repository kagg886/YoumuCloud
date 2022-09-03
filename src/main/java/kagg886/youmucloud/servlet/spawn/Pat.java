package kagg886.youmucloud.servlet.spawn;

import kagg886.youmucloud.util.Statics;
import kagg886.youmucloud.util.Utils;
import kagg886.youmucloud.util.gif.AnimatedGifEncoder;
import org.jsoup.Jsoup;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@WebServlet("/pat")
public class Pat extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getParameter("url");
        int cf = 2;
        try {
            cf = Integer.parseInt(req.getParameter("repeat"));
            if (cf > 10) {
                cf = 10;
            }
        } catch (Exception ignored) {}
        resp.getWriter().println(spawn(url,cf));
    }

    public static String spawn(String url,int cfTime) throws IOException {
        int rd = Utils.random.nextInt();
        BufferedImage bga = ImageIO.read(Jsoup.connect(url).ignoreContentType(true).execute().bodyStream());
        AnimatedGifEncoder g = new AnimatedGifEncoder();
        g.setDelay(150);
        g.setRepeat(0);
        g.start(new File(Statics.data_dir + "/imgcache/pat_" + rd + ".jpg"));
        int i = 0,fe = 0;
        while (i < 9) {
            BufferedImage back = ImageIO.read(new File(Statics.data_dir + "res/spawn/pat/" + i + ".png"));
            BufferedImage model = new BufferedImage(235,196,BufferedImage.TYPE_INT_RGB);
            Graphics gph = model.getGraphics();

            if (i == 2) {
                gph.drawImage(bga,8, 79, 112, 96,null);
            } else {
                gph.drawImage(bga,11,73,106,100,null);
            }
            gph.drawImage(back,0,0,235,196,null);
            gph.dispose();
            g.addFrame(model);
            i++;
            if (i == 3) {
                fe++;
                if (fe <= cfTime) {
                    i = 0;
                }
            }
        }
        g.finish();
        return "http://" + Statics.ip + "/youmu/Image?id=pat_" + rd;
    }
}

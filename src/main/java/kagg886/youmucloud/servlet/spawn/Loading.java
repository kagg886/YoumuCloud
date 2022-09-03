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

@WebServlet("/loading")
public class Loading extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println(spawn(req.getParameter("url")));
    }

    public static String spawn(String url) throws IOException {
        AnimatedGifEncoder e = new AnimatedGifEncoder();
        e.setRepeat(0);
        e.setDelay(110);
        int rd = Utils.random.nextInt();
        BufferedImage label = ImageIO.read(Jsoup.connect(url).ignoreContentType(true).execute().bodyStream());
        e.start(new File(Statics.data_dir + "imgcache/lo_" + rd + ".jpg"));
        for (int i = 1; i <= 68; i++) {
            BufferedImage frame;
            BufferedImage o = ImageIO.read(new File(Statics.data_dir + "res/spawn/loading/e-" + i + ".png"));
            if (i >= 46 && i <= 51) {
                frame = new BufferedImage(o.getWidth(),o.getHeight(),BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = frame.createGraphics();
                g2d.drawImage(label,100,127,292,107,null);
                g2d.drawImage(o,0,0,o.getWidth(),o.getHeight(),null);
                g2d.dispose();
            } else {
                frame = o;
            }
            e.addFrame(frame);
        }
        e.finish();
        return "http://" + Statics.ip + "/youmu/Image?id=lo_" + rd;
    }
}

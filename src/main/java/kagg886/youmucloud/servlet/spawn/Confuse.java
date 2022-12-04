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

@WebServlet("/confuse")
public class Confuse extends HttpServlet {
    public static String spawn(String url) throws IOException {
        int rd = Utils.random.nextInt();

        BufferedImage source = ImageIO.read(Jsoup.connect(url).ignoreContentType(true).execute().bodyStream());
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.setRepeat(0);
        encoder.setDelay(5);
        encoder.start(new File(Statics.data_dir + "/imgcache/confuse_" + rd + ".jpg"));

        BufferedImage backGround, frame;
        Graphics2D g2d;
        for (int i = 0; i < 100; i++) {
            backGround = ImageIO.read(new File(Statics.data_dir + "/res/spawn/confuse/" + i + ".png"));

            frame = new BufferedImage(backGround.getWidth(), backGround.getHeight(), BufferedImage.TYPE_INT_ARGB);
            g2d = frame.createGraphics();
            g2d.drawImage(source, 0, 0, backGround.getWidth(), backGround.getHeight(), null);
            g2d.drawImage(backGround, 0, 0, backGround.getWidth(), backGround.getHeight(), null);
            g2d.dispose();
            encoder.addFrame(frame);
        }
        encoder.finish();
        return "http://" + Statics.ip + "/youmu/Image?id=confuse_" + rd;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println(spawn(req.getParameter("url")));
    }
}

package kagg886.youmucloud.servlet.spawn;

import kagg886.youmucloud.util.ImageUtil;
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

@WebServlet("/garbage")
public class Garbage extends HttpServlet {
    private static int[][] arr = {
            {},
            {39, 40}, {39, 40}, {39, 40},
            {39, 30}, {39, 30},
            {39, 32}, {39, 32}, {39, 32}, {39, 32}, {39, 32}, {39, 32}, {39, 32}, {39, 32}, {39, 32}, {39, 32},
            {39, 30},
            {39, 27},
            {39, 32},
            {37, 49},
            {37, 64},
            {37, 67},
            {39, 69},
            {37, 70},
            {}
    };

    public static String spawn(String url) throws IOException {
        int rd = Utils.random.nextInt();
        BufferedImage qLogo = ImageIO.read(Jsoup.connect(url).ignoreContentType(true).execute().bodyStream());
        qLogo = ImageUtil.scaleImg(qLogo, 79, 79);

        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.setDelay(100);
        encoder.setRepeat(0);
        encoder.start(new File(Statics.data_dir + "/imgcache/garbage_" + rd + ".jpg"));

        BufferedImage frame;
        for (int i = 0; i < 25; i++) {
            frame = new BufferedImage(158, 233, BufferedImage.TYPE_INT_ARGB);
            ;
            Graphics2D g2d = frame.createGraphics();
            BufferedImage bg = ImageIO.read(new File(Statics.data_dir + "/res/spawn/garbage/" + i + ".png"));
            if (arr[i].length == 0) {
                continue;
            }
            g2d.drawImage(qLogo, arr[i][0], arr[i][1], 79, 79, null);
            g2d.drawImage(bg, 0, 0, 158, 233, null);
            g2d.dispose();
            encoder.addFrame(frame);
        }
        encoder.finish();
        return "http://" + Statics.ip + "/youmu/Image?id=garbage_" + rd;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println(spawn(req.getParameter("url")));
    }
}

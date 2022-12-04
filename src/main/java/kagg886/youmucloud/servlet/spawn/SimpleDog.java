package kagg886.youmucloud.servlet.spawn;

import kagg886.youmucloud.util.ImageUtil;
import kagg886.youmucloud.util.Statics;
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

@WebServlet("/simpledog")
public class SimpleDog extends HttpServlet {
    public static String spawn(String link) throws IOException {
        BufferedImage bg = ImageIO.read(new File(Statics.data_dir + "res/spawn/simpledog.png"));
        BufferedImage logo = ImageIO.read(Jsoup.connect(link).ignoreContentType(true).execute().bodyStream());
        BufferedImage out = new BufferedImage(bg.getWidth(), bg.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = out.createGraphics();
        g2d.drawImage(logo, 211, 520, 723, 962, null);
        g2d.drawImage(bg, 0, 0, bg.getWidth(), bg.getHeight(), null);
        g2d.dispose();
        return ImageUtil.ImageToLink(out, "simpledog");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println(spawn(req.getParameter("url")));
    }
}

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

@WebServlet("/suki")
public class Suki extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println(spawn(req.getParameter("url")));
    }

    public static String spawn(String url) throws IOException {
        BufferedImage image = ImageIO.read(new File(Statics.data_dir + "res/spawn/suki.png"));
        BufferedImage source = ImageIO.read(Jsoup.connect(url).ignoreContentType(true).execute().bodyStream());
        BufferedImage model = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics g = model.getGraphics();
        g.drawImage(source,107,70,306,233,null);
        g.drawImage(image,0,0,image.getWidth(),image.getHeight(),null);
        g.dispose();

        return ImageUtil.ImageToLink(model,"suki");
    }
}

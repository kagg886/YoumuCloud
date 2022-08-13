package kagg886.youmucloud.servlet.spawn;

import kagg886.youmucloud.util.Statics;
import kagg886.youmucloud.util.gif.AnimatedGifEncoder;
import kagg886.youmucloud.util.ImageUtil;
import kagg886.youmucloud.util.Utils;
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

@WebServlet("/pet")
public class PetPet extends HttpServlet {

    public static final int[][] pos = {
            {14, 20, 98, 98},
            {12,33,101,85},
            {8,40,110,76},
            {10,33,102,84},
            {12,20,98,98}};

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println(spawn(req.getParameter("url")));
    }

    public static String spawn(String url) throws IOException {
        BufferedImage bga = ImageIO.read(Jsoup.connect(url).ignoreContentType(true).execute().bodyStream());
        bga = ImageUtil.SquareToCircle(bga); //头像圆化

        //初始化gif合成器,读取帧列表
        AnimatedGifEncoder g = new AnimatedGifEncoder();
        g.setDelay(100);
        g.setRepeat(0);
        int rd = Utils.random.nextInt();
        g.start(new File(Statics.data_dir + "/imgcache/pet_" + rd + ".jpg"));
        for (int i = 0; i < 5; i++) {
            //素材
            BufferedImage hand = ImageIO.read(new File(Statics.data_dir + "/res/spawn/pet/" + i + ".png"));

            //创建空白画布
            BufferedImage pic = new BufferedImage(112,112,BufferedImage.TYPE_INT_ARGB);
            Graphics temp = pic.getGraphics();
            temp.drawImage(bga,pos[i][0],pos[i][1],pos[i][2],pos[i][3],null);
            temp.drawImage(hand,0,0,112,112,null);
            temp.dispose();

            g.addFrame(pic);
        }
        g.finish();
        return "http://"  + Statics.ip + "/youmu/Image?id=pet_" + rd;
    }
}

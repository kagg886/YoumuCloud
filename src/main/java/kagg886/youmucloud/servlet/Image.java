package kagg886.youmucloud.servlet;

import kagg886.youmucloud.util.Statics;
import kagg886.youmucloud.util.Utils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@WebServlet("/Image")
public class Image extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String imgid = req.getParameter("id");
        File f = new File(Statics.data_dir + "imgcache/" + imgid + ".jpg");
        if (!f.exists()) {
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println("Not exist!");
            return;
        }
        resp.setContentType("image/jpeg");
        resp.getOutputStream().write(Utils.loadByteFromFile(f.getAbsolutePath()));
        f.delete();
    }
}

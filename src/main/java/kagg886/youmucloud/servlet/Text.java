package kagg886.youmucloud.servlet;

import kagg886.youmucloud.util.Statics;
import kagg886.youmucloud.util.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/text")
public class Text extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        File f = new File(Statics.data_dir + "/static/" + req.getParameter("path") + ".txt");
        if (!f.exists()) {
            resp.getWriter().println("There is nothing in this path!");
            return;
        }
        String builder = "<head>\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "\n" +
                "</head>\n" +
                Utils.loadStringFromFile(f.getAbsolutePath());
        resp.getOutputStream().write(builder.getBytes(StandardCharsets.UTF_8));
    }
}

package kagg886.youmucloud.servlet;

import kagg886.youmucloud.util.Statics;
import kagg886.youmucloud.util.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/OneSentence")
public class OneSentence extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String[] a = Utils.loadStringFromFile(Statics.data_dir + "res/yiyan.txt").split("\n");
        resp.getOutputStream().write(a[Utils.random.nextInt(a.length)].getBytes(StandardCharsets.UTF_8));
        super.doGet(req, resp);
    }
}

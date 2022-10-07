package kagg886.youmucloud.servlet;


import kagg886.youmucloud.util.Statics;
import kagg886.youmucloud.util.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/text")
public class Text extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getParameter("path") + ".txt";
        resp.setContentType("text/html;charset=UTF-8");
        resp.getOutputStream().write(Utils.loadByteFromFile(Statics.data_dir + "/static/" + path));
    }
}

package kagg886.youmucloud.servlet.admin;

import kagg886.qinternet.Message.MsgSpawner;
import kagg886.qinternet.QInternet;
import kagg886.youmucloud.handler.QI.YoumuUser;
import kagg886.youmucloud.util.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/sendGroupMsg")
public class sendGroupMsg extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long bot = Long.parseLong(req.getParameter("bot"));
        long gid = Long.parseLong(req.getParameter("gid"));
        String msg = req.getParameter("msg").replace("\\n","\n");
        YoumuUser user = (YoumuUser) QInternet.findBot(bot);
        try {
            user.getGroupAPI().getGroup(gid).sendMsg(MsgSpawner.newPlainText(msg));
            resp.getWriter().println("OK!!");
        } catch (Throwable e) {
            resp.getWriter().println(Utils.PrintException(e));
        }
    }
}

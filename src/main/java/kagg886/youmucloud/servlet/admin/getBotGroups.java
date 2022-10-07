package kagg886.youmucloud.servlet.admin;


import kagg886.qinternet.QInternet;
import kagg886.youmucloud.handler.QI.YoumuUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/getBotGroups")
public class getBotGroups extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long bot = Long.parseLong(req.getParameter("bot"));
        YoumuUser user = (YoumuUser) QInternet.findBot(bot);
        resp.getWriter().println(user.getGroupAPI().getGroups().toString());
    }
}
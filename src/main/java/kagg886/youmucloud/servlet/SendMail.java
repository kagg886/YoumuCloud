package kagg886.youmucloud.servlet;

import kagg886.qinternet.Message.MsgCollection;
import kagg886.youmucloud.util.Mail;
import kagg886.youmucloud.util.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/sendmail")
public class SendMail extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            MsgCollection col = new MsgCollection(req.getParameter("msg"));
            Mail.sendMessage(req.getParameter("to"), req.getParameter("title"), col);
        } catch (Throwable e) {
            resp.getWriter().println(Utils.PrintException(e).replace("\n", "<br>"));
        }
    }
}

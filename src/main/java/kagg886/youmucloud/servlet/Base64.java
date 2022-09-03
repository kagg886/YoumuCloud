package kagg886.youmucloud.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/Base64")
public class Base64 extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = null;
        String input = null;
        try {
            action = req.getParameter("action");
            input = req.getParameter("input");
        } catch (Exception ignored) {}

        if (action == null || input == null) {
            resp.getWriter().println("You must write the parameter:action->[decode/encode],input->[String]");
            return;
        }
        if (action.equals("decode")) {
            resp.getOutputStream().write(java.util.Base64.getDecoder().decode(input));
            return;
        }
        if (action.equals("encode")) {
            resp.getWriter().println(new String(java.util.Base64.getEncoder().encode(input.getBytes(StandardCharsets.UTF_8))));
            return;
        }
        resp.getWriter().println("the parameter:action must be decode or encode!");
    }
}

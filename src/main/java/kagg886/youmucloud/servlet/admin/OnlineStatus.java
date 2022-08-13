package kagg886.youmucloud.servlet.admin;

import kagg886.qinternet.Content.QQBot;
import kagg886.qinternet.QInternet;
import kagg886.youmucloud.handler.QI.YoumuUser;
import kagg886.youmucloud.util.Statics;
import kagg886.youmucloud.util.WaitService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@WebServlet("/OnlineStatus")
public class OnlineStatus extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        StringBuilder builder = new StringBuilder();
        builder.append("————基本信息————")
                .append("\n活跃线程:").append(WaitService.queues.size())
                .append("\n收包:").append(Statics.ReceiveDataPack)
                .append("\n发包:").append(Statics.SendDataPack)
                .append("\n————在线列表————");
        YoumuUser user;
        for (QQBot bot : QInternet.getList()) {
            user = (YoumuUser) bot;
            builder.append("\n").append(user.getId()).append("---").append(user.getClient().getHeaders().optString("platform", "null"));
        }
        builder.append("\n————等待线程详情————");
        for (Map.Entry<String, WaitService.CallBack> s : WaitService.queues.entrySet()) {
            builder.append("\n").append(s.getKey()).append("---").append(s.getValue().toString());
        }
        response.getOutputStream().write(builder.toString().getBytes(StandardCharsets.UTF_8));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }

}

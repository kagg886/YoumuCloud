package kagg886.youmucloud.servlet.admin;

import kagg886.qinternet.Content.QQBot;
import kagg886.qinternet.QInternet;
import kagg886.youmucloud.handler.QI.Action;
import kagg886.youmucloud.handler.QI.YoumuUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//发公告接口
@WebServlet("/BCSend")
public class BCSend extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String BC = request.getParameter("content");
		StringBuilder buffer = new StringBuilder();
		buffer.append("Your Input:" + BC);
		
		if (BC == null || BC.equals("")) {
			return;
		}
		
		for (QQBot bot : QInternet.getList()) {
			YoumuUser user = (YoumuUser) bot;
			Action action = new Action("log");
			action.put("msg",BC);
			try {
				user.sendMsg(action);
			} catch (Exception e) {
				buffer.append("\n" + user.getId() + ":failed!");
			}
		}
		buffer.append("\nAll success!");
		
		response.getOutputStream().write(buffer.toString().getBytes());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}

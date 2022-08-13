package kagg886.youmucloud.servlet;

import kagg886.youmucloud.util.cache.JSONArrayStorage;
import kagg886.youmucloud.util.Utils;
import org.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/Setu")
public class Setu extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private JSONArrayStorage imgs;
	
	@Override
	public void init() throws ServletException {
		try {
			imgs = JSONArrayStorage.obtain("res/setu.json");
		} catch (Exception e) {}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int co;
		try {
			co = Integer.parseInt(request.getParameter("count"));
			if (co > 25) {
				co = 25;
			}
		} catch (Exception ignored) {
			co = 1;
		}
		JSONArray arr = new JSONArray();

		for (int i = 0; i < co; i++) {
			arr.put(imgs.optJSONObject(Utils.random.nextInt(imgs.length())));
		}
		response.getOutputStream().write(arr.toString().getBytes());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

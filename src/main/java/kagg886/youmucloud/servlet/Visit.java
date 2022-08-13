package kagg886.youmucloud.servlet;

import kagg886.youmucloud.util.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/visit")
public class Visit extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println("Please use the POST method!");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String url = req.getParameter("url");
        String method = req.getParameter("method");

        if (url == null) {
            error(new SecurityException("Please input URL!"),resp);
            return;
        }
        //安全检查:防止自调用导致死循环
        if (url.contains("youmu/visit")) {
            error(new SecurityException("Please don't request this Website!"),resp);
            return;
        }

        if (method == null) {
            method = "GET";
        }

        Connection conn = Jsoup.connect(url).ignoreContentType(true);
        //填充请求头
        try {
             JSONObject headers = new JSONObject(req.getParameter("headers"));
             String key;
             for (Iterator<String> i = headers.keys(); i.hasNext();) {
                 key = i.next();
                 conn.header(key,headers.optString(key));
             }
        } catch (Exception ignored) {}

        //填充表单数据
        try {
            JSONObject headers = new JSONObject(req.getParameter("fromdata"));
            String key;
            for (Iterator<String> i = headers.keys(); i.hasNext();) {
                key = i.next();
                conn.header(key,headers.opt(key).toString());
            }
        } catch (Exception ignored) {}


        //判断为GET还是POST
        if (method.equals("GET")) {
            conn.method(Connection.Method.GET);
        } else {
            conn.method(Connection.Method.POST);
        }

        //开始请求,然后包装响应包的包体
        try {
            BufferedInputStream buf = conn.execute().bodyStream();
            JSONArray ary = new JSONArray();
            int b;
            while ((b = buf.read()) != -1) {
                ary.put(b);
            }
            buf.close();

            JSONObject o = new JSONObject();
            try {
                o.put("error",false);
                o.put("msg", ary);
            } catch (JSONException ignored) {}
            resp.getOutputStream().write(o.toString().getBytes());
        } catch (Exception e) {
            error(e,resp);
        }
    }

    private void error(Exception e,HttpServletResponse resp) throws IOException {
        JSONObject o = new JSONObject();
        try {
            o.put("error",true);
            o.put("stacktrace", Utils.PrintException(e));
            o.put("msg",e.getMessage());
        } catch (JSONException ignored) {}
        resp.getOutputStream().write(o.toString().getBytes());
    }
}

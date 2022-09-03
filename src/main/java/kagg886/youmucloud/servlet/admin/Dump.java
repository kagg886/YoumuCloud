package kagg886.youmucloud.servlet.admin;

import kagg886.youmucloud.util.Statics;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;

@WebServlet("/Dump")
public class Dump extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        resp.getWriter().println(name);
        String pid = name.split("@")[0];

        Process process = Runtime.getRuntime().exec("jmap -dump:live,format=b,file=" + Statics.data_dir + "dump.bin " + pid);
        InputStreamReader isr = new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8);
        int byt;
        while ((byt = isr.read()) != -1) {
            resp.getOutputStream().write(byt);
        }
    }
}

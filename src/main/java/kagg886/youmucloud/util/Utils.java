package kagg886.youmucloud.util;

import kagg886.qinternet.Message.GroupMsgPack;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;

public class Utils {
    public static ThreadPoolExecutor service = (ThreadPoolExecutor) Executors.newFixedThreadPool(500);

    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

    public static Random random = new Random();


    public static String randomStr(int length) {
        StringBuilder b = new StringBuilder();
        while (length != 0) {
            length--;
            char p;
            if (Math.random() < 0.5) {
                p = (char) (new Random().nextInt(26) + 97);
            } else {
                p = (char) (new Random().nextInt(26) + 65);
            }
            b.append(p);
        }
        return b.toString();
    }


    public static ArrayList<String> getImage(GroupMsgPack pack) {
        ArrayList<String> links = new ArrayList<>();
        pack.getMessage().iterator(new MsgIterator() {
            @Override
            public void onImage(String s) {
                links.add(s);
            }
        });
        return links;
    }

    public static String h5fliter(String str) {
        String htmlStr = str;
        String textStr = "";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;
        java.util.regex.Pattern p_html1;
        java.util.regex.Matcher m_html1;


        try {
            // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
            // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
            // 定义HTML标签的正则表达式
            String regEx_html = "<[^>]+>";
            String regEx_html1 = "<[^>]+";
            // 过滤script标签
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll("");
            // 过滤style标签
            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll("");
            // 过滤html标签
            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll("");
            // 过滤html标签
            p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
            m_html1 = p_html1.matcher(htmlStr);
            htmlStr = m_html1.replaceAll("");


            textStr = htmlStr;

        } catch (Exception ignored) {
        }


        return textStr;
    }

    public static byte[] loadByteFromFile(String file) throws IOException {
        FileInputStream stream;
        try {
            stream = new FileInputStream(file);
        } catch (Exception e) {
            return new byte[0];
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int by;
        while ((by = stream.read()) != -1) {
            output.write(by);
        }

        stream.close();
        output.close();

        return output.toByteArray();
    }

    public static String loadStringFromFile(String file) throws IOException {
        return new String(loadByteFromFile(file));
    }


    public static void writeByteToFile(String file, byte[] byt) throws IOException {
        FileOutputStream stream;
        try {
            stream = new FileOutputStream(file);
        } catch (Exception e) {
            File b = new File(file);
            b.getParentFile().mkdirs();
            if (!b.createNewFile()) {
                throw e;
            }
            writeByteToFile(file, byt);
            return;
        }
        stream.write(byt);
        stream.close();
    }

    public static void writeStringToFile(String file, String content) throws IOException {
        writeByteToFile(file, content.getBytes());
    }

    public static String PrintException(Throwable e) {
        StringWriter writer = new StringWriter();
        PrintWriter pWriter = new PrintWriter(writer);
        e.printStackTrace(pWriter);
        return writer.toString();
    }

    public static void log(String type, String msg) {
        System.err.println(format.format(System.currentTimeMillis()) + "[" + type + "]:" + msg);
    }

    public static String unicodeToString(String s) {
        StringBuilder builder = new StringBuilder();

        for (String value : s.split("\\\\")) {

            if (value.startsWith("u")) {
                builder.append((char) Integer.parseInt(value.substring(1, 5), 16));
                if (value.length() > 5) {
                    builder.append(value.substring(5));
                }
            } else {
                builder.append(value);
            }

        }
        return builder.toString();
    }

    public static void trustSSL() {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (NoSuchAlgorithmException e) {
        } catch (KeyManagementException e) {
        }
    }
}

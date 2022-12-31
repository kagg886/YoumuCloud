package com.kagg886.youmucloud.bot;

import android.content.SharedPreferences;
import android.os.Build;
import com.kagg886.youmucloud.bot.secluded.PluginService;
import com.kagg886.youmucloud.util.Constant;
import com.kagg886.youmucloud.util.ContextUtil;
import com.kagg886.youmucloud.util.IOUtil;
import com.kagg886.youmucloud.util.storage.JSONObjectStorage;
import it.sauronsoftware.base64.Base64;
import kagg886.qinternet.QInternet;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

//K为收包格式,V为发包格式
public abstract class BotConnection<K,V> extends WebSocketClient {

    protected long qid;
    protected SharedPreferences sp;
    protected AbstractMessageCenter<K,V> center;

    public AbstractMessageCenter<K, V> getCenter() {
        return center;
    }

    public long getQid() {
        return qid;
    }

    public static <M,N> void init(BotConnection conn, SharedPreferences sp) {

        //填充Header
        JSONObject jSONObject = new JSONObject();
        if (sp.getBoolean("enableHeader",true)) {
            try {
                JSONObjectStorage tmp = JSONObjectStorage.obtain("sdcard/Android/data/" + Constant.PKG_NAME + "/files/config/headers.json");
                String key;
                for (Iterator<String> iterator = tmp.keys(); iterator.hasNext(); ) {
                    key = iterator.next();
                    jSONObject.put(key,tmp.optString(key));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    jSONObject.put("ver", Constant.VERSION.getLongVersionCode());
                } else {
                    jSONObject.put("ver",Constant.VERSION.versionCode);
                }
                jSONObject.put("platform", conn.getCenter().getPlatform());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        conn.addHeader("json", Base64.encode(jSONObject.toString()));
        conn.setConnectionLostTimeout(0);

        //异步连接
        conn.connect();

        int i = 0;
        while ((conn.getReadyState() != ReadyState.OPEN && i < 30)) {
            if (QInternet.findBot(conn.getQid()) == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
                i++;
                conn.getCenter().sendLog(AbstractMessageCenter.LoggerLevel.Client, "连接中...(" + i + "秒)");
            } else {
                break;
            }
        }

        if (i == 30) {
            conn.close();
            conn.getCenter().sendLog(AbstractMessageCenter.LoggerLevel.Client, "连接失败!");
            return;
        }

        conn.getCenter().sendLog(AbstractMessageCenter.LoggerLevel.Client, "YoumuCloud初始化完成！");
        SessionBot bot = new SessionBot(conn);
        QInternet.addBot(bot);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {

    }

    public void onClose(int i, String str, boolean z) {
        center.sendLog(AbstractMessageCenter.LoggerLevel.Client, "云服务器连接中断!原因:(" + i + "):" + str);
        QInternet.removeBot(QInternet.findBot(this.qid));
    }

    public void onError(Exception exc) {
        StringWriter stringWriter = new StringWriter();
        exc.printStackTrace(new PrintWriter(stringWriter));
        center.sendLog(AbstractMessageCenter.LoggerLevel.Client, "客户端发生异常\n" + stringWriter);
    }


    public void send(String str) {
        BotConnection.super.send(str);
        if (sp.getBoolean("outputLogs",true)) {
            center.sendLog(AbstractMessageCenter.LoggerLevel.Client, "send->:" + str);
        }
    }

    public BotConnection(long j, AbstractMessageCenter<K,V> bridge,SharedPreferences sp) {
        super(URI.create("ws://" + sp.getString("server", "youmucloud.kagg886.top") + "/youmu/api/"+ j));
        this.qid = j;
        this.center = bridge;
        this.sp = sp;
    }
}

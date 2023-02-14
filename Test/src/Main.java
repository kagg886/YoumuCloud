import kagg886.qinternet.Content.Group;
import kagg886.qinternet.Content.Member;
import kagg886.qinternet.Content.Person;
import kagg886.qinternet.Content.QQBot;
import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

/**
 * @projectName: YoumuServer
 * @package: PACKAGE_NAME
 * @className: Main
 * @author: kagg886
 * @description: TODO
 * @date: 2023/1/31 10:25
 * @version: 1.0
 */
public class Main {
    public static void main(String[] args) throws URISyntaxException, InterruptedException, JSONException {
        WebSocketClient client = new WebSocketClient(new URI("ws://localhost:8082/youmu/api/1693256674")) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {

            }

            @Override
            public void onMessage(String s) {
                try {
                    MsgCollection collection = new MsgCollection(new JSONObject(s).optString("msg"));
                    System.out.println(collection.getTexts());
                } catch (JSONException e) {
                    try {
                        System.out.println(new JSONObject(s).optString("msg"));
                    } catch (JSONException ex) {
                        System.out.println(s);
                    }
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                System.out.println("closed:" + i + "/" + s);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        };
        client.addHeader("json", Base64.getEncoder().encodeToString("{\"platform\":\"test\",\"ver\":20230131}".getBytes(StandardCharsets.UTF_8)));
        client.connectBlocking();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            Thread.sleep(1000);
            MsgCollection ol = new MsgCollection();
            ol.putJson("{\"app\":\"com.tencent.miniapp_01\",\"config\":{\"autoSize\":0,\"ctime\":1675645877,\"forward\":1,\"height\":0,\"token\":\"6ed8b74b1b02c58b2b7035ebf24ee03b\",\"type\":\"normal\",\"width\":0},\"desc\":\"哔哩哔哩\",\"extra\":{\"app_type\":1,\"appid\":100951776,\"uin\":3097693362},\"meta\":{\"detail_1\":{\"appType\":0,\"appid\":\"1109937557\",\"desc\":\"【steam皮肤】辉夜，灵梦和纯狐\",\"gamePoints\":\"\",\"gamePointsUrl\":\"\",\"host\":{\"nick\":\"Spinda\",\"uin\":3097693362},\"icon\":\"https://open.gtimg.cn/open/app_icon/00/95/17/76/100951776_100_m.png?t=1675158231\",\"preview\":\"pubminishare-30161.picsz.qpic.cn/cb353351-8f8f-4d8a-9547-0cdfc0dc1847\",\"qqdocurl\":\"https://b23.tv/3jBIGpE?share_medium=android&share_source=qq&bbid=XUB15B6B3936AA52517E0CEFA8959387F828E&ts=1675645871102\",\"scene\":1036,\"shareTemplateData\":{},\"shareTemplateId\":\"8C8E89B49BE609866298ADDFF2DBABA4\",\"showLittleTail\":\"\",\"title\":\"哔哩哔哩\",\"url\":\"m.q.qq.com/a/s/a5c3578b41963e3a4355070d39c01b27\"}},\"needShareCallBack\":false,\"prompt\":\"QQ小程序哔哩哔哩\",\"ver\":\"1.0.0.19\",\"view\":\"view_8C8E89B49BE609866298ADDFF2DBABA4\"}");
            QQBot bot = new QQBot(1693256674);
            GroupMsgPack pack = new GroupMsgPack(
                    new Group(bot, 12345, "121"),
                    new Member(bot, 12345, 485184047, "qwq", 4, Person.Sex.BOY, "qwq", "awa", Member.Permission.MEMBER),
                    ol
            );
            JSONObject action = new JSONObject();
            action.put("action", "onGroupMsg");
            action.put("msg", pack.toString());
            client.send(action.toString());
        }
    }
}

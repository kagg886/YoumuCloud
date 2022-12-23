package kagg886.youmucloud.handler.group.classes.game;

import com.github.markozajc.akiwrapper.Akiwrapper;
import com.github.markozajc.akiwrapper.AkiwrapperBuilder;
import com.github.markozajc.akiwrapper.core.entities.Server;
import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.youmucloud.handler.group.GroupMsgHandle;
import kagg886.youmucloud.util.Utils;
import kagg886.youmucloud.util.WaitService;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @projectName: YoumuServer
 * @package: kagg886.youmucloud.handler.group.classes.game
 * @className: Akinator
 * @author: kagg886
 * @description: Akinator模拟器
 * @date: 2022/12/23 16:38
 * @version: 1.0
 */
public class Akinator extends GroupMsgHandle {
    private static AkiwrapperBuilder builder;

    static {
        builder = new AkiwrapperBuilder().setLanguage(Server.Language.CHINESE).setGuessType(Server.GuessType.CHARACTER);
    }

    private ConcurrentHashMap<Long, Akiwrapper> map = new ConcurrentHashMap<>();

    @Override
    public void handle(GroupMsgPack pack) throws Exception {
        String text = pack.getMessage().getTexts();
        String qq = String.valueOf(pack.getMember().getUin());

        if (WaitService.hasKey(qq + "_aki")) {
            int choice;
            try {
                choice = Integer.parseInt(text);
                if (choice < 0 || choice > 5) {
                    throw new Exception();
                }
                WaitService.addCall(qq + "_aki", String.valueOf(choice));
                sendMsg(pack, "选择成功!", "你的选择为:" + choice);
            } catch (Exception e) {
                sendMsg(pack, "请输入0-4之间(含端点)数字以完成Akinator!");
                return;
            }
        }
        if (text.equals(".gm akinator start")) {
            if (map.getOrDefault(pack.getMember().getUin(), null) == null) {
                map.put(pack.getMember().getUin(), builder.build());
                sendMsg(pack, "创建游戏成功，等待服务器响应ing...");
            } else {
                sendMsg(pack, "请勿重复开始游戏!");
                return;
            }
            Utils.service.execute(new Runnable() {
                @Override
                public void run() {
                    sendMsg(pack, getCurrentProcess(pack.getMember().getUin()));
                    int choice = Integer.parseInt(WaitService.wait(pack.getMember().getUin() + "_aki", 30000));
                    Akiwrapper.Answer answer = null;
                    for (Akiwrapper.Answer w : Akiwrapper.Answer.values()) {
                        if (choice == w.getId()) {
                            answer = w;
                            break;
                        }
                    }
                    map.get(pack.getMember().getUin()).answer(answer);
                    Utils.service.execute(this);
                }
            });
        }
    }

    public String getCurrentProcess(Long uin) {
        Akiwrapper akiwrapper = map.get(uin);
        StringBuilder builder1 = new StringBuilder();
        builder1.append("---Progress:").append(akiwrapper.getQuestion().getProgression() * 100).append("%---");
        builder1.append("\n");
        builder1.append("Question.").append(akiwrapper.getQuestion().getStep());
        builder1.append("\n");
        builder1.append(akiwrapper.getQuestion().getQuestion());
        builder1.append("\n回复以下数字来代表你的回答:");
        builder1.append("0——是\n1——否\n2——不知道\n3——或许是\n4——或许不是\n5——回退");
        return builder1.toString();
    }
}

/*
import com.github.markozajc.akiwrapper.Akiwrapper;
import com.github.markozajc.akiwrapper.AkiwrapperBuilder;
import com.github.markozajc.akiwrapper.core.entities.Guess;
import com.github.markozajc.akiwrapper.core.entities.Server;
import com.github.markozajc.akiwrapper.core.exceptions.ServerNotFoundException;
import org.json.JSONArray;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Scanner;
import java.util.Spliterator;

public class Main {
    public static void main(String[] args) throws ServerNotFoundException {
        System.setProperty("proxyType", "4");
        System.setProperty("proxySet", "true");
        System.setProperty("proxyHost", "192.168.0.101");
        System.setProperty("proxyPort", "7890");

        Akiwrapper akiwrapper = new AkiwrapperBuilder().setLanguage(Server.Language.CHINESE).setGuessType(Server.GuessType.CHARACTER).build();

        while (true) {
            System.out.println("Question:" + akiwrapper.getQuestion().getQuestion());
            System.out.println("InfoGain:" + akiwrapper.getQuestion().getInfogain());
            System.out.println("Progress:" + akiwrapper.getQuestion().getProgression());
            System.out.println("Guess");
            for (Guess g : akiwrapper.getGuesses()) {
                System.out.println(g.getName() + "---" + g.getDescription() + "---" + g.getProbability());
            }
            Scanner scanner = new Scanner(System.in);
            String p = scanner.nextLine();
            akiwrapper.answer(Akiwrapper.Answer.valueOf(p));
            System.out.println("提交完毕，请骚等...");
        }
    }
}

 */
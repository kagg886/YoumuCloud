package kagg886.youmucloud.handler.group.classes.game;

import com.github.markozajc.akiwrapper.Akiwrapper;
import com.github.markozajc.akiwrapper.AkiwrapperBuilder;
import com.github.markozajc.akiwrapper.core.entities.Guess;
import com.github.markozajc.akiwrapper.core.entities.Server;
import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.Message.MsgSpawner;
import kagg886.youmucloud.handler.group.GroupMsgHandle;
import kagg886.youmucloud.util.Utils;
import kagg886.youmucloud.util.WaitService;

import java.util.List;
import java.util.Map;
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

    private ConcurrentHashMap<Long, Akiwrapper> akiWrappers = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Long, Long> groupLock = new ConcurrentHashMap<>(); //群号---QQ

    @Override
    public void handle(GroupMsgPack pack) throws Exception {
        String text = pack.getMessage().getTexts();
        String qq = String.valueOf(pack.getMember().getUin());

        if (WaitService.hasKey(qq + "_aki")) {
            int choice;
            try {
                choice = Integer.parseInt(text);
                if (choice < 0 || choice > 4) {
                    throw new Exception();
                }
                WaitService.addCall(qq + "_aki", String.valueOf(choice));
                sendMsg(pack, "选择成功!", "你的选择为:" + choice);
            } catch (Exception e) {
                sendMsg(pack, "请输入0-4之间(含端点)数字以完成Akinator!");
            }
        }

        if (text.equals(".gm akinator stop")) {
            if (akiWrappers.getOrDefault(pack.getMember().getUin(), null) != null) {
                sendMsg(pack, "已强行停止游戏");

                akiWrappers.remove(pack.getMember().getUin());
                WaitService.addCall(pack.getMember().getUin() + "_aki", "null");
                for (Map.Entry<Long, Long> entry : groupLock.entrySet()) {

                }
                return;
            } else {
                sendMsg(pack, "未找到游戏开始的实例");
            }
        }
        if (text.equals(".gm akinator start")) {
            if (akiWrappers.getOrDefault(pack.getMember().getUin(), null) == null) {
                akiWrappers.put(pack.getMember().getUin(), builder.build());
                sendMsg(pack, "创建游戏成功，等待服务器响应ing...");
            } else {
                sendMsg(pack, "请勿重复开始游戏!");
                return;
            }
            Utils.service.execute(new Runnable() {
                @Override
                public void run() {
                    Akiwrapper akiwrapper = akiWrappers.getOrDefault(pack.getMember().getUin(), null);
                    if (akiwrapper == null) {
                        return;
                    }
                    sendMsg(pack, getCurrentProcess(akiwrapper));
                    String ans = WaitService.wait(pack.getMember().getUin() + "_aki", 30000);
                    if (ans == null) {
                        sendMsg(pack, "超时，游戏自动关闭!");
                        akiWrappers.remove(pack.getMember().getUin());
                        return;
                    }

                    if (ans.equals("null")) {
                        return;
                    }

                    int choice = Integer.parseInt(ans);
                    Akiwrapper.Answer answer = null;
                    for (Akiwrapper.Answer w : Akiwrapper.Answer.values()) {
                        if (choice == w.getId()) {
                            answer = w;
                            break;
                        }
                    }
                    akiwrapper.answer(answer);

                    List<Guess> guessList = null;
                    if (akiwrapper.getQuestion().getStep() > 60) {
                        guessList = akiwrapper.getGuesses();
                    } else {
                        guessList = akiwrapper.getGuessesAboveProbability(0.85f);
                    }
                    if (guessList.size() != 0) {
                        if (guessList.size() >= 2) {
                            guessList.sort((guess, t1) -> Double.compare(t1.getProbability(), guess.getProbability()));
                        }
                        Guess guess = guessList.get(0);
                        MsgCollection collection = MsgSpawner.newAtToast(pack.getMember().getUin(), "");
                        if (guess.getImage() != null) {
                            collection.putImage(guess.getImage().toString());
                        }
                        if (akiwrapper.getQuestion().getStep() > 60) {
                            collection.putText("特么的，爷不猜了。你是不是想的是");
                        } else {
                            collection.putText("我猜你的心里想的是...");
                        }
                        collection.putText("\n");
                        collection.putText(guess.getName());
                        collection.putText("\n");
                        collection.putText(guess.getDescription());
                        collection.putText("\n游戏结束，玩的愉快>_<");
                        akiWrappers.remove(pack.getMember().getUin());
                        pack.getGroup().sendMsg(collection);
                        return;
                    }

                    Utils.service.execute(this);
                }
            });
        }
    }

    public String getCurrentProcess(Akiwrapper akiwrapper) {
        StringBuilder builder1 = new StringBuilder();
        builder1.append("---Progress:").append(akiwrapper.getQuestion().getProgression()).append("%---");
        builder1.append("\n");
        builder1.append("Question.").append(akiwrapper.getQuestion().getStep());
        builder1.append("\n");
        builder1.append(akiwrapper.getQuestion().getQuestion());
        builder1.append("\n请在30秒内回复以下数字来代表你的回答:\n");
        builder1.append("0——是\n1——否\n2——不知道\n3——或许是\n4——或许不是");
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
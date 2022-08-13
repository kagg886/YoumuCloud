package kagg886.youmucloud.handler.Classes;

import kagg886.youmucloud.util.*;
import kagg886.youmucloud.util.nd.BNDFile;
import kagg886.youmucloud.util.nd.BNDPerson;
import kagg886.youmucloud.util.nd.BNDShare;
import kagg886.youmucloud.util.nd.LanzouHelper;
import kagg886.youmucloud.util.cache.JSONArrayStorage;
import kagg886.youmucloud.util.cache.JSONObjectStorage;
import kagg886.youmucloud.util.code24.Code24;
import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.Message.MsgSpawner;
import kagg886.youmucloud.util.gif.AnimatedGifEncoder;
import kagg886.youmucloud.handler.MsgHandle;
import kagg886.youmucloud.util.tank.GreyParams;
import kagg886.youmucloud.util.tank.MirageTank;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ToolKit extends MsgHandle {

    private JSONArrayStorage answers;
    private String[] os;
    private JSONObjectStorage langheaders;

    private MirageTank mirageTank = new MirageTank();;

    public ToolKit() {
        try {
            answers = JSONArrayStorage.obtain("res/Answers.json");
            os = Utils.loadStringFromFile(Statics.data_dir + "res/yiyan.txt").split("\\n");
            langheaders = JSONObjectStorage.obtain("res/langheaders.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(GroupMsgPack pack) throws Exception {
        String text = pack.getMessage().getTexts();
        final long qq = pack.getMember().getUin();

        if (WaitService.hasKey(qq + "_bdnVerify")) {
            if (WaitService.addCall(qq + "_bdnVerify", text)) {
                sendMsg(pack, "验证码提交成功,正在继续解析中...");
            } else {
                sendMsg(pack, "验证码提交失败");
            }
        }

        if (text.startsWith(".tk tank")) {
            ArrayList<String> img = Utils.getImage(pack);
            if (img.size() != 2) {
                sendMsg(pack,"使用此功能需要发送两张图片:第一张为表图，第二张为里图");
                return;
            }

            BufferedImage outside = ImageIO.read(Jsoup.connect(img.get(0)).ignoreContentType(true).execute().bodyStream());
            //防止图片大小不一致出现bug
            BufferedImage inside = new BufferedImage(outside.getWidth(),outside.getHeight(),BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = inside.createGraphics();
            g2d.drawImage(ImageIO.read(Jsoup.connect(img.get(1)).ignoreContentType(true).execute().bodyStream()),0,0,inside.getWidth(),inside.getHeight(), null);

            BufferedImage result = mirageTank.outputGrey(outside, inside, GreyParams.getDefault());
            MsgCollection c = MsgSpawner.newAtToast(pack.getMember().getUin(),"生成完毕!");
            c.putImage(ImageUtil.ImageToLink(result,"tank"));
            pack.getGroup().sendMsg(c);
        }

        if (text.startsWith(".tk baidudecode ")) {
            String[] vars = text.split(" ");
            if (vars.length < 4) {
                sendMsg(pack, "正确的格式应改为.tk baidudecode [度盘分享链接] [提取码]");
                return;
            }
            String shareCode = vars[2];
            String pwd = vars[3];
            if (shareCode.contains("pan.baidu.com/s/") && shareCode.startsWith("http")) {
                vars = shareCode.split("/");
                shareCode = vars[vars.length - 1].split("\\?")[0];
                if (ScoreUtil.checkCoin(this, pack, 10)) {
                    return;
                }
                final BNDShare shareInfo = new BNDShare(shareCode, vCode_link -> {
                    MsgCollection c = MsgSpawner.newAtToast(qq, "请在十五秒内发送如图所示的验证码,取消验证请发送-1");
                    c.putImage(vCode_link);
                    sendMsg(pack, c);
                    String verifyCode = WaitService.wait(qq + "_bdnVerify", 15);
                    if (verifyCode.equals("-1")) {
                        //传null取消验证
                        return null;
                    }
                    return verifyCode;
//					try {
//						ImageIO.write(ImageIO.read(Jsoup.connect(vCode_link).ignoreContentType(true).execute().bodyStream()), "PNG", new File("C:\\Users\\iveou\\Desktop\\verify.png"));
//					} catch (IOException ignored) {
//					}
//					System.out.println("请输入验证码");
//					return new Scanner(System.in).nextLine();
                });
                Utils.service.execute(() -> {
                    try {
                        shareInfo.verify(pwd);
                        BNDPerson p = shareInfo.getSharePerson();

                        StringBuffer c = new StringBuffer();
                        printBNDFile(shareInfo.getShareInfo(),c);

                        Mail.sendMessage(qq + "@qq.com", "百度网盘解析结果By kagg886",
                                "上传者:", p.getNick(), c.toString()
                                ,"<br>推荐使用IDM下载~<br>顺带一提的是，请设置4线程并修改UA为 netdisk;3.0.0.112"
                        );

                        sendMsg(pack,"解析结果已发送到您的qq邮箱中了!");
                    } catch (Exception e) {
                        Utils.log("BNDError",Utils.PrintException(e));
                        sendMsg(pack, "运行百度网盘解析时出错!");
                    }
                });
            }
        }

        if (text.startsWith(".tk lanzoudecode ")) {
            String[] vars = text.split(" ");
            if (vars.length == 2) {
                sendMsg(pack, "请输入链接!");
                return;
            }
            LanzouHelper.Lanzou res;
            try {
                res = LanzouHelper.getLanZouRealLink(vars[2]);
            } catch (Throwable e) {
                sendMsg(pack, "解析失败!原因:\n" + e.getMessage());
                return;
            }
            sendMsg(pack, "文件名:", res.getName(),
                    "<br>大小:", res.getSize(),
                    "<br>下载链接:", res.getDlLink());
        }

        if (text.startsWith(".tk gif")) {
            int framejump = 500;
            boolean isFix = true;
            try {
                framejump = Integer.parseInt(text.split(" ")[2]);
                if (framejump <= 0) {
                    throw new Exception();
                }
            } catch (Exception ignored) {
            }
            if (text.contains("-nofix")) {
                isFix = false;
            }
            int r = Utils.random.nextInt();
            ArrayList<String> links = Utils.getImage(pack);
            if (links.size() < 2) {
                sendMsg(pack, "请发送两张以上图片!");
                return;
            }

            if (ScoreUtil.checkCoin(this, pack, links.size() * 3)) {
                return;
            }
            AnimatedGifEncoder e = new AnimatedGifEncoder();
            e.setRepeat(0);
            e.start(new File(Statics.data_dir + "imgcache/gif_" + r + ".jpg"));
            e.setDelay(framejump);
            boolean isFirst = true;
            int wid = 0, height = 0;
            BufferedImage img;
            for (String l : links) {
                img = ImageIO.read(Jsoup.connect(l).ignoreContentType(true).execute().bodyStream());
                if (isFirst & isFix) {
                    wid = img.getWidth();
                    height = img.getHeight();
                    e.addFrame(img);
                    isFirst = false;
                    continue;
                }

                if (!isFix) {
                    e.addFrame(img);
                    continue;
                }

                BufferedImage fix = new BufferedImage(wid, height, BufferedImage.TYPE_INT_RGB);
                Graphics g = fix.getGraphics();
                g.drawImage(img, 0, 0, wid, height, null);
                g.dispose();
                e.addFrame(fix);
            }
            e.finish();
            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "");
            col.putImage("http://" + Statics.ip + "/youmu/Image?id=gif_" + r);
            pack.getGroup().sendMsg(col);

        }

        if (text.startsWith(".tk jsformat")) {
            String[] vars = text.split(" ");
            if (vars.length == 2) {
                sendMsg(pack, "请输入json!");
                return;
            }
            String result;
            try {
                try {
                    result = new JSONObject(vars[2]).toString(2);
                } catch (Exception e) {
                    result = new JSONArray(vars[2]).toString(2);
                }
            } catch (Exception e) {
                sendMsg(pack, "发生错误:" + e.getMessage());
                return;
            }
            sendMsg(pack, result);
        }

        if (text.startsWith(".tk pgrun ")) {
            String[] vars = text.split("\n");
            if (vars.length == 1) {
                sendMsg(pack, "请在第一行末尾添加对应语言的文件扩展名!");
                return;
            }
            if (ScoreUtil.checkCoin(this, pack, 2)) {
                return;
            }
            String lang = text.split(" ")[2].split("\n")[0];
            Connection.Response r = Jsoup.connect("https://tool.runoob.com/compile2.php")
                    .ignoreContentType(true)
                    .data("fileext", lang)
                    .data("code", text.replace(vars[0], ""))
                    .data("token", "4381fe197827ec87cbac9552f14ec62a")
                    .data("stdin", "")
                    .data("language", String.valueOf(langheaders.getInt(lang)))
                    .method(Connection.Method.POST).execute();
            JSONObject rt;
            try {
                rt = new JSONObject(r.body());
            } catch (JSONException e) {
                sendMsg(pack, r.body());
                return;
            }
            sendMsg(pack, rt.optString("errors") + rt.optString("output"));
        }

        if (text.equals(".tk os")) {
            sendMsg(pack, os[Utils.random.nextInt(os.length)]);
        }

        if (text.startsWith(".tk ans")) {
            String[] vars = text.split(" ");
            if (vars.length <= 2) {
                sendMsg(pack, "参数不够!");
                return;
            }
            sendMsg(pack, "对于问题:" + vars[2], "\n我的答案是:\n", answers.optString(Utils.random.nextInt(answers.length())));
        }

        if (text.startsWith(".tk wf ")) {
            String[] vars = text.split(" ");
            if (vars.length <= 2) {
                sendMsg(pack, "参数不够!");
                return;
            }

            if (ScoreUtil.checkCoin(this, pack, 2)) {
                return;
            }

            String lambda = text.replace(".tk wf ", "");
            Connection conn = Jsoup.connect("https://api.wolframalpha.com/v2/query?input=" + URLEncoder.encode(lambda, "UTF-8") + "&appid=VWLQLP-27UKE8YVWR");
            JSONObject data = XML.toJSONObject(conn.ignoreContentType(true).execute().body()).optJSONObject("queryresult");

            if (!data.optBoolean("success")) {
                StringBuilder buf = new StringBuilder();

                if (data.has("didyoumeans")) {
                    buf.append("我不知道你要问什么,但是我猜测你要问:");
                    try {
                        buf.append(data.optJSONObject("didyoumeans").optJSONObject("didyoumean").optString("content"));
                    } catch (Exception e) {
                        JSONArray didyousay = data.optJSONObject("didyoumeans").optJSONArray("didyoumean");
                        for (int i = 0; i < didyousay.length(); i++) {
                            buf.append("\n").append(didyousay.optJSONObject(i).optString("level"));
                        }
                    }
                }
                sendMsg(pack, buf.toString());
                return;
            }
            String ty = "None";
            if (!data.optString("datatypes").equals("")) {
                ty = data.optString("datatypes");
            }

            MsgCollection mob = MsgSpawner.newAtToast(pack.getMember().getUin(), "");
            mob.putText("Subject:" + ty);

            JSONArray content = data.optJSONArray("pod");
            JSONObject pod;
            for (int i = 0; i < content.length(); i++) {
                pod = content.optJSONObject(i);
                if (pod.has("subpod")) {
                    mob.putText("\n" + pod.optString("title"));
                    try {
                        mob.putImage(pod.optJSONObject("subpod").optJSONObject("img").optString("src"));
                    } catch (Exception e) {
                        //System.err.println(pod.toString(4));
                        //为jsonArray形式，需要再度遍历
                        JSONArray subpods = pod.optJSONArray("subpod");
                        for (int o = 0; o < subpods.length(); o++) {
                            pod = subpods.optJSONObject(o);
                            if (!pod.optString("title").equals("")) {
                                mob.putText("\n" + pod.optString("title"));
                            }
                            mob.putImage(pod.optJSONObject("img").optString("src"));
                        }
                    }

                }
            }

            pack.getGroup().sendMsg(mob);
        }

        if (text.startsWith(".tk code24")) {
            String[] vars = text.split(" ");
            if (vars.length != 3) {
                sendMsg(pack, "参数不够或过多!");
                return;
            }
            String j = new Code24().setCards(vars[2]).calc();
            sendMsg(pack, j);
        }
    }

    public static void printBNDFile(List<BNDFile> file,StringBuffer buffer) throws Exception {
        for (BNDFile f : file) {
            if (f.isDirectory()) {
                printBNDFile(f.listFiles(),buffer);
            } else {
                buffer.append("<br>").append(f.getPath()).append("---").append(f.getDownloadLink());
            }
        }
    }
}

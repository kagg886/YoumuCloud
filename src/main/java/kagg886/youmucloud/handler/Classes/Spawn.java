package kagg886.youmucloud.handler.Classes;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.Message.MsgSpawner;
import kagg886.youmucloud.handler.MsgHandle;
import kagg886.youmucloud.servlet.spawn.*;
import kagg886.youmucloud.util.ImageUtil;
import kagg886.youmucloud.util.ScoreUtil;
import kagg886.youmucloud.util.Statics;
import kagg886.youmucloud.util.Utils;
import kagg886.youmucloud.util.bull.BullshitGenerator;
import kagg886.youmucloud.util.sudo.SudokuChecker;
import kagg886.youmucloud.util.sudo.SudokuFactory;
import net.coobird.thumbnailator.Thumbnails;
import org.jsoup.Jsoup;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class Spawn extends MsgHandle {
    @Override
    public void handle(GroupMsgPack pack) throws Exception {
        String text = pack.getMessage().getTexts();

        if (text.startsWith(".spawn ")) {
            if (ScoreUtil.checkCoin(this,pack,3)) {
                return;
            }
        }

        if (text.startsWith(".spawn loading")) {
            ArrayList<String> links = Utils.getImage(pack);
            if (links.size() == 0) {
                sendMsg(pack,"请发送图片!");
                return;
            }
            sendMsg(pack,"生成速度较慢(约20s)，还请耐心等待~");
            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(),"");
            col.putImage(Loading.spawn(links.get(0)));
            pack.getGroup().sendMsg(col);
        }

        if (text.startsWith(".spawn pat@")) {
            ArrayList<Long> targets = pack.getMessage().getAt();
            if (targets.size() == 0) {
                sendMsg(pack, "请艾特一个人,不要复制");
                return;
            }
            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "流口水~");
            col.putImage(Pat.spawn("https://q1.qlogo.cn/g?b=qq&nk=" + targets.get(0) + "&s=640", 2));
            pack.getGroup().sendMsg(col);
        }

        if (text.startsWith(".spawn garbage@")) {
            ArrayList<Long> targets = pack.getMessage().getAt();
            if (targets.size() == 0) {
                sendMsg(pack, "请艾特一个人,不要复制");
                return;
            }
            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "垃姬探头~");
            col.putImage(Garbage.spawn("https://q1.qlogo.cn/g?b=qq&nk=" + targets.get(0) + "&s=640"));
            pack.getGroup().sendMsg(col);
        }

        if (text.startsWith(".spawn pet@")) {
            ArrayList<Long> targets = pack.getMessage().getAt();
            if (targets.size() == 0) {
                sendMsg(pack, "请艾特一个人,不要复制");
                return;
            }
            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "摸摸~");
            col.putImage(PetPet.spawn("https://q1.qlogo.cn/g?b=qq&nk=" + targets.get(0) + "&s=640"));
            pack.getGroup().sendMsg(col);
        }

        if (text.startsWith(".spawn bth")) {
            ArrayList<String> links = Utils.getImage(pack);
            if (links.size() == 0) {
                sendMsg(pack,"请发送图片!");
                return;
            }
            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(),"");
            col.putImage(OtherThink.spawn(links.get(0)));
            pack.getGroup().sendMsg(col);
        }

        if (text.startsWith(".spawn suki")) {
            ArrayList<String> links = Utils.getImage(pack);
            if (links.size() == 0) {
                sendMsg(pack,"请发送图片!");
                return;
            }
            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(),"");
            col.putImage(Suki.spawn(links.get(0)));
            pack.getGroup().sendMsg(col);
        }

        if (text.startsWith(".spawn bestlike")) {
            ArrayList<String> links = Utils.getImage(pack);
            if (links.size() == 0) {
                sendMsg(pack,"请发送图片!");
                return;
            }
            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(),"");
            col.putImage(BestLike.spawn(links.get(0)));
            pack.getGroup().sendMsg(col);
        }

        if (text.startsWith(".spawn shit")) {
            String[] var = text.split(" ");
            if (var.length == 2) {
                sendMsg(pack,"请输入主题！");
                return;
            }
            int zs = 800;
            try {
                zs = Integer.parseInt(var[3]);
            } catch (Exception ignored) {
            }
            sendMsg(pack,BullshitGenerator.generate(var[2],zs));
        }

        if (text.startsWith(".spawn dio@")) {
            ArrayList<Long> targets = pack.getMessage().getAt();
            if (targets.size() == 0) {
                sendMsg(pack,"请艾特一个人,不要复制");
                return;
            }
            File f = new File(Statics.data_dir + "imgcache/dio_" + targets.get(0) + ".jpg");
            hans: {
                if (f.exists()) {
                    break hans;
                }

                BufferedImage image = ImageIO.read(Jsoup.connect("https://q1.qlogo.cn/g?b=qq&nk=" + targets.get(0) + "&s=640").ignoreContentType(true).execute().bodyStream());
                image = ImageUtil.SquareToCircle(image);
                image = Thumbnails.of(image).scale(1.0).outputQuality(1.0).rotate(Utils.random.nextInt(360)).asBufferedImage();
                BufferedImage bkg = ImageIO.read(new File(Statics.data_dir + "/res/spawn/throw.png"));
                Graphics g = bkg.getGraphics();
                g.drawImage(image,19, 181, 137, 137, null);
                g.dispose();
                ImageIO.write(bkg,"PNG",f);
            }

            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(),"dio~");
            col.putImage("http://" + Statics.ip + "/youmu/Image?id=dio_" + targets.get(0));
            pack.getGroup().sendMsg(col);
        }

        if (text.startsWith(".spawn sudo")) {
            int level = 1;
            String lebel = "normal";
            String[] vars = text.split(" ");
            if (vars.length == 3) {
                lebel = vars[2];
                switch (vars[2]) {
                    case "easy":
                        level = 0;
                        break;
                    case "hard":
                        level = 2;
                        break;
                    case "lunatic":
                        level = 3;
                        break;
                    case "overdrive":
                        level = 4;
                        break;
                }
            }
            SudokuFactory sudokuFactory = SudokuFactory.create(level);
            byte[][] question =  sudokuFactory.getTitle();
            SudokuChecker.showSudokuMap(question);
            BufferedImage background = ImageIO.read(new File(Statics.data_dir + "/res/spawn/sodu.png"));
            Graphics a = background.getGraphics();
            a.setColor(Color.black);
            a.setFont(new Font("宋体",Font.BOLD,30));
            int x = 13,y = 31;
            for (byte[] line : question) {
                for (byte point : line) {
                    if (point != 0) {
                        a.drawString(String.valueOf(point),x,y);
                    }
                    x += 40;
                }
                y += 40;
                x = 13;
            }
            a.dispose();
            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(),"生成完毕!","难度:" + lebel);
            col.putImage(ImageUtil.ImageToLink(background,"sd"));
            pack.getGroup().sendMsg(col);
        }
    }
}

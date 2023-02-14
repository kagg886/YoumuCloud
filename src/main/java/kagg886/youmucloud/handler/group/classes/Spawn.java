package kagg886.youmucloud.handler.group.classes;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.Message.MsgSpawner;
import kagg886.youmucloud.handler.group.GroupMsgHandle;
import kagg886.youmucloud.servlet.spawn.*;
import kagg886.youmucloud.util.ImageUtil;
import kagg886.youmucloud.util.ScoreUtil;
import kagg886.youmucloud.util.Statics;
import kagg886.youmucloud.util.Utils;
import kagg886.youmucloud.util.bull.BullshitGenerator;
import kagg886.youmucloud.util.sudo.SudokuChecker;
import kagg886.youmucloud.util.sudo.SudokuFactory;
import org.json.JSONArray;
import org.jsoup.Jsoup;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class Spawn extends GroupMsgHandle {

    private JSONArray colors, weapons, objects, body, roles, chests, hairs, height, dispositions;

    public Spawn() {
        try {
            colors = new JSONArray("[\"红色\",\"黑色\",\"白色\",\"紫色\",\"绿色\",\"黄色\",\"蓝色\",\"灰色\",\"橙色\",\"青色\",\"银色\",\"粉色\",\"金色\",\"随意\"]");
            weapons = new JSONArray("[\"单手剑\",\"长戟/枪\",\"斧头\",\"镰刀\",\"锤子\",\"太刀\",\"弓\",\"双剑\",\"巨剑\",\"扫把\",\"伞\",\"书\",\"弩\",\"钓鱼竿\",\"铲子\",\"旗帜\",\"玩偶\",\"棒球套装\",\"盾牌\",\"物理学圣剑\",\"吉他\",\"御币\",\"爪子\",\"舰装\",\"匕首\",\"砍刀\",\"法杖\",\"火铳\",\"手枪\",\"步枪\",\"狙击枪\",\"斧\",\"飞刀\",\"手里剑\",\"双手剑\"]");
            objects = new JSONArray("[\"机械钟\",\"冰\",\"棺材\",\"花\",\"月亮\",\"太阳\",\"星辰\",\"王冠\",\"绸带\",\"发卡\",\"发箍\",\"头巾\",\"方巾\",\"围巾\",\"网球拍\",\"乒乓球拍\",\"羽毛球拍\",\"排球\",\"怀表\",\"书\",\"眼睛\",\"泪痣\",\"腕带\",\"发带\",\"尾巴\",\"机械躯干\",\"面具\",\"背包\",\"耳机\",\"滑板\",\"骷髅\",\"锁链\",\"钟表\",\"火焰\",\"轮椅\",\"动物\",\"糖果\",\"手电筒\",\"油灯\",\"眼罩\",\"伤痕\",\"角\",\"伤痕\",\"扑克牌A\",\"扑克牌Q\",\"扑克牌JOKER\",\"晒痕\",\"挑染\",\"发簪\"]");
            body = new JSONArray("[\"机械手\",\"眼镜\",\"翅膀\",\"机械腿\",\"帽子\",\"面具\",\"眼罩\",\"大裙摆\",\"机械尾巴\",\"伤痕\",\"T恤\",\"西服\",\"晚礼服\",\"帽衫\",\"风衣\",\"破烂的衣服\",\"校服\",\"制服\",\"水手服\",\"旗袍\",\"铠甲\",\"女仆装\",\"围裙\",\"皮夹克\",\"紧身衣\",\"浴袍\",\"和服\",\"婚纱\",\"白无垢\",\"比基尼\",\"连体式\",\"死库水\",\"睡衣\",\"睡裙\",\"浴衣\",\"球服\",\"囚服\",\"道袍\",\"修女服\",\"白大褂\",\"巫女服\",\"魔女服\",\"连衣裙\",\"机车服\",\"运动服\",\"黑丝\",\"白丝\",\"肉丝\",\"长筒袜\",\"短袜\"]");
            roles = new JSONArray("[\"千金大小姐\",\"离家少女\",\"中二病少女\",\"军旅少女\",\"偶像\",\"学生\",\"兽娘\",\"法师\",\"史莱姆\",\"JS\",\"JC\",\"JK\",\"JD\",\"OL\",\"修女\",\"恶魔\",\"魅魔\",\"青梅竹马\",\"萝莉\",\"猫娘\",\"御姐\",\"女仆\",\"医生\",\"护士\",\"天使\",\"精灵\",\"亡灵\",\"furry\",\"龙娘\",\"人偶\",\"机器人\",\"辣妹\",\"不良少女\",\"犬耳娘\",\"狐娘\",\"狼女\",\"蛇女\",\"鹰身女妖\",\"鸟人\",\"王女\",\"皇女\",\"堕天使\",\"文学少女\",\"伪少年\",\"蜜蜂女\",\"虎娘\",\"伪绿茶\",\"巫女\",\"魔女\",\"雪女\",\"裂口女\",\"未亡人\",\"忍者\",\"杀手\",\"猎人\",\"售货员\",\"学生会长\",\"风纪委员\",\"体育生\"]");
            chests = new JSONArray("[\"贫乳\",\"平胸\",\"正常\",\"丰满\",\"巨乳\",\"爆乳\"]");
            hairs = new JSONArray("[\"短发\",\"长发\",\"中长发\",\"丸子头\",\"单马尾\",\"双马尾\",\"披肩长发\",\"齐耳短发\",\"卷发\",\"杂乱头发\",\"麻花辫\",\"中分\",\"三七/二八分\",\"波波头\",\"鲍勃头\",\"中短发\",\"杂乱发型\"]");
            height = new JSONArray("[\"幼小\",\"矮小\",\"娇小\",\"中等身材\",\"高挑\",\"高大\",\"巨大\"]");
            dispositions = new JSONArray("[\"阴郁\",\"笨蛋\",\"色欲\",\"元气\",\"阴沉\",\"忧郁\",\"别扭\",\"腹黑\",\"傲娇\",\"病娇\",\"知性\",\"迷糊\",\"大大咧咧\",\"感性\",\"大姐头\",\"天真\",\"天然呆\",\"三无\",\"高冷\",\"女王\",\"抖S\",\"抖M\",\"弱气\",\"毒舌\",\"内向\",\"黑化\"]");
        } catch (Exception ignored) {
        }
    }


    private String getRanStr(JSONArray ary) {
        return ary.optString(Utils.random.nextInt(ary.length()));
    }

    @Override
    public void handle(GroupMsgPack pack) throws Exception {
        String text = pack.getMessage().getTexts();

        if (text.startsWith(".spawn ")) {
            if (ScoreUtil.checkCoin(this, pack, 3)) {
                return;
            }

        }

        if (text.startsWith(".spawn wife")) {
            ArrayList<String> links = Utils.getImage(pack);
            if (links.size() == 0) {
                sendMsg(pack, "请发送图片!");
                return;
            }
            BufferedImage img = ImageIO.read(Jsoup.connect(links.get(0)).ignoreContentType(true).execute().bodyStream());
            BufferedImage bg = ImageIO.read(new File(Statics.data_dir + "/res/spawn/wife.png"));
            Graphics2D graphics2D = bg.createGraphics();
            graphics2D.drawImage(img, 0, 115, 941, 702, null);
            graphics2D.dispose();
            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "要好好对老婆负责哦~");
            col.putImage(ImageUtil.ImageToLink(bg, "wife"));
            pack.getGroup().sendMsg(col);
        }

        if (text.startsWith(".spawn confuse")) {
            ArrayList<String> links = Utils.getImage(pack);
            if (links.size() == 0) {
                sendMsg(pack, "请发送图片!");
                return;
            }
            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "");
            col.putImage(Confuse.spawn(links.get(0)));
            pack.getGroup().sendMsg(col);
        }

        if (text.startsWith(".spawn simpledog@")) {
            ArrayList<Long> targets = pack.getMessage().getAt();
            if (targets.size() == 0) {
                sendMsg(pack, "请艾特一个人,不要复制");
                return;
            }
            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "精神支柱!");
            col.putImage(SimpleDog.spawn("https://q1.qlogo.cn/g?b=qq&nk=" + targets.get(0) + "&s=640"));
            pack.getGroup().sendMsg(col);

        }

        if (text.startsWith(".spawn emojimix")) {
            String[] vars = text.split(" ");
            if (vars.length != 4) {
                sendMsg(pack, "参数识别失败!正确的格式为:.spawn emojimix [emoji表情1] [emoji表情2]");
                return;
            }
            String url = EmojiMix.spawn(vars[2], vars[3]);
            if (url == null) {
                sendMsg(pack, "暂不支持!", vars[2], vars[3]);
                return;
            }
            MsgCollection p = MsgSpawner.newAtToast(pack.getMember().getUin(), "你的混合emoji~");
            p.putImage(url);
            pack.getGroup().sendMsg(p);
        }

        if (text.startsWith(".spawn chara")) {
            sendMsg(pack, "生成的属性如下:",
                    "\n角色:", getRanStr(roles),
                    "\n性格:", getRanStr(dispositions),
                    "\n主色:", getRanStr(colors),
                    "\n武器:", getRanStr(weapons),
                    "\n身着:", getRanStr(body),
                    "\n搭配:", getRanStr(objects),
                    "\n欧派:", getRanStr(chests),
                    "\n发型:", getRanStr(hairs),
                    "\n身体:", getRanStr(height)
            );
        }

        if (text.startsWith(".spawn loading")) {
            ArrayList<String> links = Utils.getImage(pack);
            if (links.size() == 0) {
                sendMsg(pack, "请发送图片!");
                return;
            }
            sendMsg(pack, "生成速度较慢(约20s)，还请耐心等待~");
            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "");
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

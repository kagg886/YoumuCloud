package kagg886.youmucloud.handler.group.classes;

import kagg886.qinternet.Message.GroupMsgPack;
import kagg886.qinternet.Message.MsgCollection;
import kagg886.qinternet.Message.MsgSpawner;
import kagg886.youmucloud.handler.group.GroupMsgHandle;
import kagg886.youmucloud.util.ImageUtil;
import kagg886.youmucloud.util.Statics;
import kagg886.youmucloud.util.Utils;
import kagg886.youmucloud.util.arc.ArcImageUtils;
import kagg886.youmucloud.util.arc.ArcaeaB30Provider;
import kagg886.youmucloud.util.cache.JSONObjectStorage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MusicGame extends GroupMsgHandle {
    private JSONObject source;
    private BufferedImage bg;

    private JSONObjectStorage arcBind;

    public MusicGame() {
        try {
            bg = ImageIO.read(new File(Statics.data_dir + "/res/arc/bg.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        arcBind = JSONObjectStorage.obtain("data/exp/arcBind.json");

//        try {
//            source = JSONObjectStorage.obtain("res/MuseMusic.json").optJSONObject("fullAlbums");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        Utils.service.execute(new Runnable() {
            @Override
            public void run() {
                Connection c = Jsoup.connect("https://musedash.moe").ignoreContentType(true);
                try {
                    source = new JSONObject(c.execute().body().split("__INITIAL_STATE__=")[1].split("</script>")[0]).optJSONObject("fullAlbums");
                    Utils.log("debug", "MuseDash曲库装载完成");
                } catch (Exception e) {
                    Utils.log("debug", "MuseDash曲库装载失败,准备重新装载...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    Utils.service.execute(this);
                }
            }
        });
    }

    @Override
    public void handle(GroupMsgPack pack) throws InterruptedException {
        String text = pack.getMessage().getTexts();

        if (text.startsWith(".arc bind")) {
            String[] val = text.split(" ");
            if (val.length == 2) {
                sendMsg(pack, "格式错误!正确的格式为:\n.arc bind [好友码]");
                return;
            }
            try {
                Long.parseLong(val[2]);
                ArcaeaB30Provider provider = new ArcaeaB30Provider(val[2] + " -1 -1", new ArcaeaB30Provider.Listener() {
                    @Override
                    public void onSuccess(ArrayList<ArcaeaB30Provider.Score> scores, JSONObject userInfo) {
                        try {
                            arcBind.put(String.valueOf(pack.getMember().getUin()), val[2]);
                            arcBind.saveUnsafe();
                            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "绑定成功!");
                            col.putImage(ImageUtil.ImageToLink(ArcImageUtils.getPersonInfo(userInfo), "arcBind"));
                            pack.getGroup().sendMsg(col);
                        } catch (Exception e) {
                            sendClientLog(pack, Utils.PrintException(e));
                            sendMsg(pack, "成功获取数据，但是保存失败惹！");
                        }
                    }

                    @Override
                    public void onFailed(Exception e) {
                        sendMsg(pack, "绑定失败!原因:" + e.getMessage());
                    }
                });
                provider.connectBlocking();
            } catch (Exception e) {
                sendMsg(pack, "不是约定的好友码格式!请重新输入");
                return;
            }
        }

        if (text.equals(".arc b30")) {
            if (arcBind.isNull(String.valueOf(pack.getMember().getUin()))) {
                sendMsg(pack, "请先绑定账号!\n指令:.arc bind [好友码]");
                return;
            }
            ArcaeaB30Provider provider = new ArcaeaB30Provider(arcBind.optString(String.valueOf(pack.getMember().getUin())), new ArcaeaB30Provider.Listener() {
                @Override
                public void onSuccess(ArrayList<ArcaeaB30Provider.Score> scores, JSONObject userInfo) {
                    BufferedImage big = new BufferedImage(710 * 3, 310 * 10, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D graphics2D = big.createGraphics();
                    int x = 0, y = 0;
                    for (int i = 0; i < Math.min(scores.size(), 31); i++) {
                        sendClientLog(pack, "合成第:" + i + "个");
                        try {
                            graphics2D.drawImage(ArcImageUtils.getSongUnit(scores.get(i)), x, y, null);
                        } catch (Exception e) {
                            graphics2D.dispose();
                            sendMsg(pack, "拉取B30失败!\n原因:合成图片时发生错误");
                            sendClientLog(pack, scores.get(i).id);
                        }
                        x += 710;
                        if ((i + 1) % 3 == 0) {
                            x = 0;
                            y += 310;
                        }
                    }
                    graphics2D.dispose();

                    BufferedImage out = new BufferedImage(710 * 3, 310 * 10 + 300, BufferedImage.TYPE_INT_ARGB);
                    graphics2D = out.createGraphics();
                    graphics2D.drawImage(bg, 0, 0, out.getWidth(), out.getHeight(), null);
                    try {
                        graphics2D.drawImage(ArcImageUtils.getPersonInfo(userInfo), 0, 0, null);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    graphics2D.drawImage(big, 0, 300, null);

                    graphics2D.dispose();
                    MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "数据生成完毕，请点击下方链接以查看");
                    try {
                        col.putText("\n");
                        col.putText(ImageUtil.ImageToLink(ImageUtil.compress(out), "arcB30"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    pack.getGroup().sendMsg(col);
                }

                @Override
                public void onFailed(Exception e) {
                    sendMsg(pack, "请求提交失败!原因:" + e.getMessage());
                }
            });
            if (provider.connectBlocking()) {
                sendMsg(pack, "请求提交成功,等待返回数据");
                return;
            }
        }

        if (text.startsWith(".md music")) {
            String[] var = text.split(" ");
            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "");

            if (var.length == 2) {
                sendMsg(pack, "请输入曲名!");
                return;
            }
            if (source == null) {
                sendMsg(pack, "MuseDash曲库装载中,请稍后再试");
                return;
            }

            Iterator<String> albums = source.keys();

            while (albums.hasNext()) {
                JSONObject album = source.optJSONObject(albums.next());
                if (album == null) {
                    continue;
                }
                JSONObject songs = album.optJSONObject("music");
                Iterator<String> songkey = songs.keys();
                while (songkey.hasNext()) {
                    JSONObject musicdetail = songs.optJSONObject(songkey.next());
                    String name = musicdetail.optString("name");

                    if (name.toLowerCase().contains(var[2].toLowerCase())) {

                        col.putText("\n曲包：" + album.optJSONObject("ChineseS").optString("title"));
                        col.putText("\n曲名：" + name);
                        col.putText("\n作者：" + musicdetail.optString("author"));
                        col.putText("\nbpm：" + musicdetail.optString("bpm"));
                        col.putText("\n谱师：");
                        JSONArray temp = musicdetail.optJSONArray("levelDesigner");
                        for (int i = 0; i < temp.length(); i++) {
                            if (temp.optString(i).equals("null")) {
                                continue;
                            }
                            col.putText(temp.optString(i) + "，");

                        }
                        col.putText("\n难度：");
                        temp = musicdetail.optJSONArray("difficulty");
                        for (int i = 0; i < temp.length(); i++) {
                            if (temp.optString(i).equals("0")) {
                                continue;
                            }
                            col.putText(temp.optString(i) + "，");
                        }

                        col.putText("\n——————\n");
                    }
                }
            }
            if (col.length() == 0) {
                col.putText("没有搜到。。。");
            }
            pack.getGroup().sendMsg(col);
        }

        if (text.startsWith(".md player")) {
            String[] var = text.split(" ");
            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "---SearchResult---");

            if (var.length == 2) {
                pack.getGroup().sendMsg(MsgSpawner.newAtToast(pack.getMember().getUin(), "请输入玩家!"));
                return;
            }

            Connection connection = Jsoup.connect("https://api.musedash.moe/player/" + var[2]);
            connection.ignoreContentType(true);
            JSONObject callback = null;
            try {
                callback = new JSONObject(connection.execute().body());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!callback.optString("rl").equals("NaN")) {
                JSONObject index = callback.optJSONObject("user");
                col.putText("\r\n昵称:" + index.optString("nickname"));
                col.putText("\r\n相对等级:" + String.format("%.3f", callback.optDouble("rl")) + "%");

                JSONArray acc = callback.optJSONArray("plays");
                double allacc = 0;
                for (int i = 0; i < acc.length(); i++) {
                    try {
                        allacc += acc.getJSONObject(i).optDouble("acc");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                col.putText("\n平均准度:" + String.format("%.3f", allacc / acc.length()));
                col.putText("\n注册时间:" + index.optString("created_at"));
                col.putText("\n最后上线:" + index.optString("updated_at"));

            } else {
                col.putText("\n找不到这个uuid代表的玩家!");
            }
            pack.getGroup().sendMsg(col);
        }

        if (text.startsWith(".md search")) {
            String[] var = text.split(" ");
            MsgCollection col = MsgSpawner.newAtToast(pack.getMember().getUin(), "---SearchResult---");

            if (var.length == 2) {
                pack.getGroup().sendMsg(MsgSpawner.newAtToast(pack.getMember().getUin(), "请输入玩家!"));
                return;
            }

            Connection connection = Jsoup.connect("https://api.musedash.moe/search/" + var[2]);
            connection.ignoreContentType(true);
            JSONArray array = null;
            try {
                array = new JSONArray(connection.execute().body());
            } catch (Exception ignored) {
            }
            if (array.length() != 0) {
                int max = Math.min(array.length(), 10);

                for (int i = 0; i < max; i++) {
                    col.putText("\r\n" + array.optJSONArray(i).optString(0) + "---" + array.optJSONArray(i).optString(1));
                }
                if (max >= 10) {
                    col.putText("\r\n...等" + (array.length() - 10) + "个玩家\r\n请前往:https://musedash.moe/player处搜索");
                }
            } else {
                col.putText("\r\n无搜索结果");
            }


            pack.getGroup().sendMsg(col);
        }
    }

}

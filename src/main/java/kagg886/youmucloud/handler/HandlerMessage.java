package kagg886.youmucloud.handler;

import kagg886.qinternet.Interface.QQMsgListener;
import kagg886.qinternet.Message.*;
import kagg886.qinternet.QInternet;
import kagg886.youmucloud.handler.Classes.*;
import kagg886.youmucloud.handler.Classes.game.LiteGame;
import kagg886.youmucloud.handler.Classes.game.ScoreStatis;
import kagg886.youmucloud.handler.QI.YoumuUser;
import kagg886.youmucloud.util.MsgIterator;
import kagg886.youmucloud.util.Utils;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;

public class HandlerMessage implements QQMsgListener {

    public static final HandlerMessage INSTANCE = new HandlerMessage();
    public static final LinkedList<MsgHandle> handles = new LinkedList<>();

    private String[] fixChar = new String[]{"<", ">", "[", "]"};

    static {
        //注册指令监听器
        try {
            handles.add(new CardListener());
        } catch (Exception e) {
            Utils.PrintException(e);
        }
        handles.add(new Help());
        handles.add(new MC());
        handles.add(new MuseDash());
        handles.add(new ToolKit());
        handles.add(new Music());
        handles.add(new hso());
        handles.add(new BiliBili());
        handles.add(new RSSService());
        handles.add(new ScoreStatis());
        handles.add(new Spawn());
        handles.add(new LiteGame());
    }

    @Override
    public void onFriendChange(FriendChangePack arg0) {

    }

    @Override
    public void onFriendMsg(FriendMsgPack arg0) {

    }

    @Override
    public void onGroupEnterApplication(GroupMemberApplicationPack arg0) {

    }

    @Override
    public void onGroupMsg(GroupMsgPack pack) {
//		Utils.service.submit(new Runnable() {
//			@Override
//			public void run() {
        if (Utils.service.getActiveCount() == Utils.service.getMaximumPoolSize()) {
            pack.getGroup().sendMsg(MsgSpawner.newAtToast(pack.getMember().getUin(), "服务器繁忙,请稍后再试"));
        }

        if (((YoumuUser) QInternet.findBot(pack.getMember().getBotQQ())).getClient().getHeaders().optInt("ver", 0) < Utils.lowestVersion) {
            pack.getGroup().sendMsg(MsgSpawner.newPlainText("抱歉，当前版本因为兼容性而暂停使用\n请下载最新版YoumuCloud"));
            return;
        }
        boolean canFilter = true;
        boolean canReplacer = true;
        boolean canAutoFixer = true;
        for (MsgHandle msgHandle : handles) {

            if (canFilter) { //指令过滤器，加一个bool保证只过滤一次
                //444_fliter   11,112   11
                for (String unit : msgHandle.getParam(pack, pack.getGroup().getId() + "_fliter", "none").split(",")) {
                    if (pack.getMessage().getTexts().startsWith(".") && pack.getMessage().getTexts().contains(unit)) {
                        msgHandle.sendClientLog(pack, pack.getMessage().getTexts() + "命令已被屏蔽");
                        msgHandle.sendMsg(pack, "此命令由于触发屏蔽词而被屏蔽\n详情请咨询管理员");
                        return;
                    }
                }
                canFilter = false;
            }

            if (canReplacer) { //指令替换器
                //replace:排行榜   .xp rank
                JSONObject source = msgHandle.getParams(pack);

                for (Iterator<String> it = source.keys(); it.hasNext();) {
                    String rpl = it.next();
                    if (rpl.contains(":") && rpl.split(":").length == 2 && rpl.startsWith("replace:")) {
                        String rp;
                        try {
                            rp = rpl.split(":")[1];
                        } catch (Exception e) {
                            msgHandle.sendClientLog(pack,"警告:运行群指令替换器时出错!\n请确保是否按照replace:[指令]——[替换的指令]填写!");
                            break;
                        }
                        if (pack.getMessage().getTexts().equals(rp)) {
                            //创建新包,然后搬移text
                            final MsgCollection c = new MsgCollection();
                            c.putText(source.optString(rpl));
                            pack.getMessage().iterator(new MsgIterator() {
                                @Override
                                public void onImage(String s) {
                                    c.putImage(s);
                                }

                                @Override
                                public void onXml(String s) {
                                    c.putxml(s);
                                }

                                @Override
                                public void onJson(String s) {
                                    c.putJson(s);
                                }

                                @Override
                                public void onPtt(String s) {
                                    c.putPtt(s);
                                }

                                @Override
                                public void onAt(long l) {
                                    c.putAt(l);
                                }
                            });
                            pack = new GroupMsgPack(pack.getGroup(), pack.getMember(), c);
                            break;
                        }
                    }
                }
                canReplacer = false;
            }
            //自动纠正
            if (canAutoFixer) {
                for (String fix : fixChar) {
                    if (pack.getMessage().getTexts().contains(fix)) {
                        final MsgCollection c = new MsgCollection();
                        pack.getMessage().iterator(new MsgIterator() {

                            @Override
                            public void onText(String s) {
                                c.putText(s.replace(fix, ""));
                            }

                            @Override
                            public void onImage(String s) {
                                c.putImage(s);
                            }

                            @Override
                            public void onAt(long l) {
                                c.putAt(l);
                            }
                        });
                        pack = new GroupMsgPack(pack.getGroup(), pack.getMember(), c);
                    }
                }
                canAutoFixer = false;
            }

            try {
                msgHandle.handle(pack);
            } catch (Throwable e) {
                msgHandle.sendMsg(pack, "运行bot时出错!请复制以下错误信息然后加入官方群告知管理员!\n", Utils.PrintException(e));
                msgHandle.sendClientLog(pack, Utils.PrintException(e));
            }
        }
//			}
//		});
    }

    @Override
    public void onMemberMsg(GroupMemberPack arg0) {

    }

}

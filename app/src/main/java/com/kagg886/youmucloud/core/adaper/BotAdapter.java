package com.kagg886.youmucloud.core.adaper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.kagg886.youmucloud.R;
import com.kagg886.youmucloud.bot.AbstractMessageCenter;
import com.kagg886.youmucloud.bot.SessionBot;
import com.kagg886.youmucloud.core.activity.MainActivity;
import com.kagg886.youmucloud.util.IOUtil;
import kagg886.qinternet.QInternet;
import org.java_websocket.client.WebSocketClient;
import org.jsoup.Jsoup;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BotAdapter extends BaseAdapter {
    private MainActivity ctx;

    public BotAdapter(MainActivity ctx) {
        this.ctx = ctx;
    }
    @Override
    public int getCount() {
        return QInternet.getList().size();
    }

    @Override
    public Object getItem(int position) {
        return QInternet.getList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.adapter_bot,null);

        ImageView qLogo = v.findViewById(R.id.adapter_bot_account);
        TextView plat = v.findViewById(R.id.adapter_bot_plat);
        TextView id = v.findViewById(R.id.adapter_bot_id);
        Button detail = v.findViewById(R.id.adapter_bot_details);
        Button restart = v.findViewById(R.id.adapter_bot_restart);

        SessionBot bot = (SessionBot) QInternet.getList().get(position);
        IOUtil.asyncHttp(ctx, Jsoup.connect("https://q1.qlogo.cn/g?b=qq&nk=" + bot.getId() + "&s=640").ignoreContentType(true), new IOUtil.Response() {
            @Override
            public void onSuccess(byte[] byt) {
                qLogo.setImageBitmap(BitmapFactory.decodeByteArray(byt, 0, byt.length));
            }

            @Override
            public void onFailed(Throwable e) {
                ctx.snack(bot.getId() + "头像拉取失败");
            }
        });
        plat.setText("Platform:" + bot.getConn().getCenter().getPlatform());
        id.setText("QQ:" + bot.getId());

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Bot:" + bot.getId());
                //反射取出Headers
                Map<String,String> headers;
                try {
                    Field f = WebSocketClient.class.getDeclaredField("headers");
                    f.setAccessible(true);
                    headers = (Map<String,String>) f.get(bot.getConn());
                } catch (Exception e) {
                    headers = new HashMap<>();
                    headers.put("失败!",e.getMessage());
                }
                builder.setMessage(String.format("云服务器连接状态:%s\n连接Header:%s",bot.getConn().getReadyState().toString(),headers.toString()));
                builder.create().show();
            }
        });

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QInternet.removeBot(bot);
                bot.getConn().close();
                ctx.snack("关闭连接成功!若插件收到群消息包则会继续加载!");
                notifyDataSetChanged();
            }
        });
        return v;
    }
}

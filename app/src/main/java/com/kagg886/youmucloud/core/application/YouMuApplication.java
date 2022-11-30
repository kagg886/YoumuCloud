package com.kagg886.youmucloud.core.application;

import android.app.Application;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import com.kagg886.youmucloud.bot.SessionBot;
import com.kagg886.youmucloud.util.Constant;
import com.kagg886.youmucloud.util.ExceptionUtil;
import com.kagg886.youmucloud.util.IOUtil;
import kagg886.qinternet.Content.QQBot;
import kagg886.qinternet.QInternet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class YouMuApplication extends Application implements Runnable, Thread.UncaughtExceptionHandler {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化版本信息
        try {
            Constant.VERSION = getPackageManager().getPackageInfo(Constant.PKG_NAME,0);
        } catch (Exception ignored) {}

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("logCrash",true)) {
            Thread.setDefaultUncaughtExceptionHandler(this);
            new Handler(Looper.getMainLooper()).post(this);
        }
    }

    @Override
    public void run() {
        //主线程崩溃检测
        while (true) {
            try {
                Looper.loop();
            } catch (Throwable th) {
                writeCrash(th);
            }
        }
    }

    private void writeCrash(Throwable th) {
        File wr = IOUtil.newFile(getExternalFilesDir("crash-handler"),"/",System.currentTimeMillis(),".log");
        try {
            FileWriter fileWriter = new FileWriter(wr);
            fileWriter.append("This is a YoumuCloud Exception log,Please send it to developer as fast!");
            fileWriter.append("\n---DeviceInfo---");
            fileWriter.append("\nAndroid-Version:" + Build.VERSION.RELEASE);
            fileWriter.append("\nModel:" + Build.MODEL);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                fileWriter.append("\nAPP-Version:" + Constant.VERSION.versionName + "(" + Constant.VERSION.getLongVersionCode() + ")");
            } else {
                fileWriter.append("\nAPP-Version:" + Constant.VERSION.versionName + "(" + Constant.VERSION.versionCode + ")");
            }
            fileWriter.append("\n---WebSocketInfo---");
            for (QQBot bot : QInternet.getList()) {
                SessionBot bot1 = (SessionBot) bot;
                fileWriter.append(String.format("\n%d---%s(%s)",bot1.getId(),bot1.getConn().getReadyState().toString(),bot1.getConn().getCenter().getPlatform()));
            }
            fileWriter.append("\n---StackTrace---\n");
            fileWriter.append(ExceptionUtil.appendStack(th));
            fileWriter.flush();
            fileWriter.close();
            Process.killProcess(Process.myPid());
        } catch (Exception ignored) {}
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        //子线程崩溃检测
        writeCrash(e);
    }
}

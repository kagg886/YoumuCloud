package com.kagg886.youmucloud.util;

import android.content.Context;
import android.content.pm.PackageInfo;

public class Constant {

    public static PackageInfo VERSION;

    public static final String INFO = "发送\".menu\"查看机器人指令";

    public static final String AUTHOR = "kagg886";

    public static boolean isOriginalPkg(Context ctx) {
        return ctx.getPackageName().equals("com.kagg886.youmucloud");
    }
}

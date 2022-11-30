package com.kagg886.youmucloud.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import androidx.preference.PreferenceManager;
import com.kagg886.youmucloud.core.activity.MainActivity;

import java.lang.reflect.Method;

public class ContextUtil {

    public static String getServerAddress(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString("server", "43.129.249.30:8082");
    }

    public static void copyString(MainActivity ctx, String text) {
        ClipboardManager cm = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ClipData mClipData = ClipData.newPlainText("Label", text);
            cm.setPrimaryClip(mClipData);
        } else {
            cm.setText(text);
        }
        ctx.snack("复制成功!");
    }
}

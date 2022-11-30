package com.kagg886.youmucloud.core.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.BaseBundle;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.AppSettingsDialogHolderActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

@SuppressLint("RestrictedApi")
public class CustomAppSettingsDialogHolderActivity extends AppSettingsDialogHolderActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Field mDialog = AppSettingsDialogHolderActivity.class.getDeclaredField("mDialog");
            mDialog.setAccessible(true);
            AlertDialog dialog = (AlertDialog) mDialog.get(this);
            dialog.cancel();
            dialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("警告")
                    .setMessage("程序的运行必需这些权限\n点击下面的确定按钮已跳转到对应设置开启权限")
                    .setPositiveButton("确定", this)
                    .setNegativeButton("退出程序", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .show();
            mDialog.set(this,dialog);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}

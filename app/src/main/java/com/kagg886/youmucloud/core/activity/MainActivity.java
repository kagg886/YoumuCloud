package com.kagg886.youmucloud.core.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.kagg886.youmucloud.R;
import com.kagg886.youmucloud.core.adaper.HomePagerAdapter;
import com.kagg886.youmucloud.core.fragment.BotFragment;
import com.kagg886.youmucloud.core.fragment.CrashFragment;
import com.kagg886.youmucloud.core.fragment.HeadersFragment;
import com.kagg886.youmucloud.core.fragment.SettingsFragment;
import com.kagg886.youmucloud.databinding.ActivityMainBinding;
import com.kagg886.youmucloud.util.Constant;
import com.kagg886.youmucloud.util.ContextUtil;
import com.kagg886.youmucloud.util.IOUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IOUtil.Response, EasyPermissions.PermissionCallbacks, View.OnKeyListener {

    private ActivityMainBinding binding;

    private TabLayout layout;
    private ViewPager pager;
    private HomePagerAdapter adapter;

    private LinearLayout root;

    private AlertDialog webDialog;

    private final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    public TabLayout getLayout() {
        return layout;
    }

    public LinearLayout getRoot() {
        return root;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        root = binding.activityMainRoot;
        setContentView(root);
        //初始化adapter
        initView();

        //检查权限
        checkPermissions();

        //临时代码，将在未来删除
        SharedPreferences pf = PreferenceManager.getDefaultSharedPreferences(this);
        if (pf.getBoolean("launchAs4_3",true)) {
            pf.edit().putString("server","youmucloud.kagg886.top").putBoolean("launchAs4_3",false).apply();
        }


        //检查更新
        IOUtil.asyncHttp(this, Jsoup.connect("http://" + ContextUtil.getServerAddress(this) + "/youmu/text?path=update").ignoreContentType(true), this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void showDialog() {
        WebView view = new WebView(this);
        view.getSettings().setJavaScriptEnabled(true);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setOnKeyListener(this);
        view.loadUrl("http://" + ContextUtil.getServerAddress(this) + "/youmu/HomePage");

        webDialog = new AlertDialog.Builder(this).setView(view).setPositiveButton("确定", null).show();
    }

    private void checkPermissions() {
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, 114514, permissions)
                            .setRationale("为了程序的正常运行，请给予程序读写设备文件权限").build()
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void initView() {
        adapter = new HomePagerAdapter(getSupportFragmentManager());
        ArrayList<HomePagerAdapter.Structure> fragments = new ArrayList<>();
        //...放置数据
        fragments.add(new HomePagerAdapter.Structure("BOT状态", new BotFragment()));
        fragments.add(new HomePagerAdapter.Structure("参数控制", new HeadersFragment()));
        fragments.add(new HomePagerAdapter.Structure("崩溃日志", new CrashFragment()));
        fragments.add(new HomePagerAdapter.Structure("设置", new SettingsFragment()));
        adapter.setViews(fragments);

        //初始化TabLayout,ViewPager并设置关联
        pager = binding.activityMainViewViewPager;
        layout = binding.activityMainViewTabLayout;
        pager.setAdapter(adapter);
        layout.setupWithViewPager(pager);

        //动态设置背景

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("autoFetchBG", true)) {
            new Thread(() -> {
                try {
                    JSONArray object = new JSONArray(Jsoup.connect("http://" + ContextUtil.getServerAddress(this) + "/youmu/Setu").timeout(2000).ignoreContentType(true).execute().body());
                    byte[] img = IOUtil.loadByteFromStream(Jsoup.connect(object.optJSONObject(0).optString("link")).ignoreContentType(true).execute().bodyStream());
                    runOnUiThread(() -> {
                        Bitmap v = BitmapFactory.decodeByteArray(img, 0, img.length);
                        root.setBackground(new BitmapDrawable(getResources(), v));
                    });
                } catch (Throwable ignored) {
                    runOnUiThread(() -> snack("背景图拉取失败"));
                }
            }).start();
        } else {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            root.setBackgroundColor(
                    Color.rgb(sp.getInt("R", 255),
                            sp.getInt("G", 87),
                            sp.getInt("B", 634)
                    )
            );
        }
    }


    //一些工具类

    public void snack(String txt) {
        Snackbar.make(root, txt, Snackbar.LENGTH_LONG).show();
    }

    //更新检查方法
    @Override
    public void onSuccess(byte[] byt) throws Exception {
        JSONObject latestInfo = new JSONObject(new String(byt));
        long latestVer = latestInfo.optLong("latestVersionCode");
        long latestCode = 0;
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            latestCode = Constant.VERSION.getLongVersionCode();
        } else {
            latestCode = Constant.VERSION.versionCode;
        }
        if (latestVer != latestCode) {
            View v = LayoutInflater.from(this).inflate(R.layout.dialog_update, null);
            ((TextView) v.findViewById(R.id.dialog_update_info)).setText(latestInfo.optString("updateDesc"));
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(String.format("发现新版本:%d", latestVer)).
                    setView(v).setCancelable(false).create();
            Button cancel = v.findViewById(R.id.dialog_update_cancel);
            if (latestInfo.optBoolean("force")) {
                cancel.setClickable(false);
            }
            cancel.setOnClickListener(v1 -> {
                dialog.cancel();
                showDialog();
            });
            Button start = v.findViewById(R.id.dialog_update_start);
            start.setOnClickListener(btn -> {
                dialog.setTitle("下载中...");
                cancel.setClickable(false);
                start.setClickable(false);
                new Thread(() -> {
                    try {
                        FileOutputStream stream = new FileOutputStream(IOUtil.newFile(getExternalFilesDir("update"), "/update.apk"));
                        Connection.Response resp = Jsoup.connect("http://" + ContextUtil.getServerAddress(this) + "/youmu/update").ignoreContentType(true).execute();
                        long total = Long.parseLong(resp.header("Content-Length"));
                        InputStream receive = resp.bodyStream();
                        int byt1;
                        long count = 0, bar = 0;
                        long milestone = 0, unit = total / 100;
                        while ((byt1 = receive.read()) != -1) {
                            stream.write(byt1);
                            count++;
                            if (count >= milestone) {
                                if (bar == 100) {
                                    continue;
                                }
                                start.setText(String.format("%d%%/100%%", ++bar));
                                milestone += unit;
                            }
                        }
                        receive.close();
                        stream.close();
                        File apkFile = IOUtil.newFile(getExternalFilesDir("update"), "/update.apk");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri contentUri = FileProvider.getUriForFile(
                                    MainActivity.this
                                    , "com.kagg886.youmucloud.fileprovider"
                                    , apkFile);
                            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                        } else {
                            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                        }
                        startActivity(intent);

                    } catch (Exception e) {
                        snack("更新失败!");
                        dialog.cancel();
                    }
                }).start();
            });
            dialog.show();
            return;
        }
        showDialog();
    }

    @Override
    public void onFailed(Throwable e) {
        snack("更新检查失败!" + e.getMessage());
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull @NotNull List<String> perms) {
        snack("权限已获取，欢迎使用YoumuCloud!");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull @NotNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog dialog = new AppSettingsDialog.Builder(this).build();
            Intent intent = new Intent(this, CustomAppSettingsDialogHolderActivity.class);
            intent.putExtra("extra_app_settings", dialog);
            startActivityForResult(intent, 114514);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            if (((WebView) v).canGoBack()) {
                ((WebView) v).goBack();
            } else {
                webDialog.cancel();
            }
        }
        return true;
    }
}
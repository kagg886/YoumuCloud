package com.kagg886.youmucloud.core.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.kagg886.youmucloud.R;
import com.kagg886.youmucloud.core.activity.MainActivity;
import com.kagg886.youmucloud.util.Constant;
import com.kagg886.youmucloud.util.IOUtil;
import com.kagg886.youmucloud.util.storage.JSONObjectStorage;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private MainActivity ctx;

    private Bitmap old;

    private SharedPreferences sp;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        ctx = (MainActivity) getActivity();
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        this.sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        sp.registerOnSharedPreferenceChangeListener(this);
        findPreference("crashOnce").setOnPreferenceClickListener(this);
        findPreference("reset").setOnPreferenceClickListener(this);
        findPreference("BGColorSetting").setOnPreferenceClickListener(this);
        findPreference("restart").setOnPreferenceClickListener(this);

        Preference s = findPreference("verInfo");
        s.setTitle("当前版本:" + Constant.VERSION.versionName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            s.setSummary("版本号:" + Constant.VERSION.getLongVersionCode());
        } else {
            s.setSummary("版本号:" + Constant.VERSION.versionCode);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        ctx.snack("重启软件和主程序生效!");
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals("crashOnce")) throw new RuntimeException("TEST");
        if (key.equals("restart")) {
            Intent t = new Intent(getActivity().getApplicationContext(),MainActivity.class);
            startActivity(t);
            ctx.finish();
        }
        if (key.equals("reset")) {
            PreferenceManager.getDefaultSharedPreferences(ctx).edit().clear().apply();
            IOUtil.delFile(ctx.getExternalFilesDir("config"));
            JSONObjectStorage.destroy(ctx.getExternalFilesDir("config") + "/headers.json");
            Toast.makeText(ctx,"请重新打开APP",Toast.LENGTH_LONG).show();
            ctx.finish();
        }
        if (key.equals("BGColorSetting")) {
            if (old != null) {
                old.recycle();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("设置背景颜色...");
            View v = LayoutInflater.from(ctx).inflate(R.layout.dialog_colorseek,null);
            builder.setView(v);
            SeekBar Red,Green,Blue;

            Red = v.findViewById(R.id.dialog_colorSeek_R);
            Green = v.findViewById(R.id.dialog_colorSeek_G);
            Blue = v.findViewById(R.id.dialog_colorSeek_B);


            Red.setProgress(sp.getInt("R",255));
            Green.setProgress(sp.getInt("G",87));
            Blue.setProgress(sp.getInt("B",634));

            ImageView preView = v.findViewById(R.id.dialog_colorSeek_preView);

            SeekBar.OnSeekBarChangeListener se = new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int R,G,B;
                    R = Red.getProgress();
                    G = Green.getProgress();
                    B = Blue.getProgress();
                    old = Bitmap.createBitmap(170,170, Bitmap.Config.RGB_565);
                    for (int x = 0; x < old.getWidth(); x++) {
                        for (int y = 0; y < old.getHeight(); y++) {
                            old.setPixel(x,y,Color.rgb(R,G,B));
                        }
                    }
                    preView.setImageBitmap(old);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    sp.edit().putInt("R", Red.getProgress()).putInt("G",Green.getProgress()).putInt("B",Blue.getProgress()).apply();
                }
            };
            Red.setOnSeekBarChangeListener(se);
            Green.setOnSeekBarChangeListener(se);
            Blue.setOnSeekBarChangeListener(se);
            builder.create().show();
        }
        return true;
    }
}
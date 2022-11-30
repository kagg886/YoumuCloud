package com.kagg886.youmucloud.core.adaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.kagg886.youmucloud.R;

import java.io.File;

public class CrashAdapter extends BaseAdapter {
    private final Context ctx;

    public CrashAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return ctx.getExternalFilesDir("crash-handler").listFiles().length;
    }

    @Override
    public Object getItem(int position) {
        return getCrashLog(position);
    }

    public File getCrashLog(int position) {
        return ctx.getExternalFilesDir("crash-handler").listFiles()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.adapter_crash,null);
        TextView tex = v.findViewById(R.id.adapter_crash_fileName);
        tex.setText(getCrashLog(position).getName());
        return v;
    }
}

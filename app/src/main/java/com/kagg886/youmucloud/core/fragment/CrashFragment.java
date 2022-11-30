package com.kagg886.youmucloud.core.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kagg886.youmucloud.R;
import com.kagg886.youmucloud.core.activity.MainActivity;
import com.kagg886.youmucloud.core.adaper.CrashAdapter;
import com.kagg886.youmucloud.util.ContextUtil;
import com.kagg886.youmucloud.util.IOUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class CrashFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private MainActivity ctx;

    private ListView list;

    private CrashAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ctx = (MainActivity) getActivity();
        if (!PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("logCrash",true)) {
            TextView view = new TextView(ctx);
            view.setText("崩溃记录不可用!\n请前往设置开启\"启用崩溃记录\"");
            return view;
        }
        View v = LayoutInflater.from(ctx).inflate(R.layout.fragment_header,null);
        //初始化fab按钮
        FloatingActionButton add = v.findViewById(R.id.fragment_header_widget_add);
        add.setImageResource(android.R.drawable.ic_delete);
        add.setOnClickListener(this);

        //初始化ListView
        list = v.findViewById(R.id.fragment_header_view_list);

        TextView t = new TextView(ctx);
        t.setText("没有数据!");
        ((ViewGroup) list.getParent()).addView(t);
        list.setEmptyView(t);


        adapter = new CrashAdapter(ctx);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        for (File b : ctx.getExternalFilesDir("crash-handler").listFiles()) {
            b.delete();
        }
        ctx.snack("清理完成!");
        adapter.notifyDataSetChanged();
    }

    //点击崩溃报告的事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("崩溃详情");
        String[] x;
        try {
            x = IOUtil.loadStringFromFile(adapter.getCrashLog(position).getAbsolutePath()).split("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        builder.setItems(x, (pw, which) -> ContextUtil.copyString(ctx,x[which]));
        builder.create().show();
    }
}

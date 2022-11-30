package com.kagg886.youmucloud.core.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.kagg886.youmucloud.R;
import com.kagg886.youmucloud.core.activity.MainActivity;
import com.kagg886.youmucloud.core.adaper.HeaderAdapter;
import com.kagg886.youmucloud.util.ContextUtil;
import com.kagg886.youmucloud.util.IOUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.OutputStream;
import java.util.Map;

public class HeadersFragment extends Fragment implements View.OnClickListener {
    private ListView list;
    private MainActivity ctx;

    private HeaderAdapter adapter;

    private ActivityResultLauncher<Intent> readCall, writeCall;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = (MainActivity) getActivity();
        writeCall = this.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getData() == null) {
                    return;
                }
                try {
                    OutputStream stream = ctx.getContentResolver().openOutputStream(result.getData().getData());
                    stream.write(IOUtil.loadByteFromFile(IOUtil.newFile(ctx.getExternalFilesDir("config"), "/headers.json").getAbsolutePath()));
                    stream.close();
                    ctx.snack("备份完成!");
                } catch (Exception e) {
                    ctx.snack("导出失败!");
                }
            }
        });
        readCall = this.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getData() == null) {
                return;
            }
            try {
                String s = IOUtil.loadStringFromStream(ctx.getContentResolver().openInputStream(result.getData().getData()));
                new JSONObject(s);
                IOUtil.writeStringToFile(IOUtil.newFile(ctx.getExternalFilesDir("config"), "/headers.json").getAbsolutePath(), s);
                adapter.notifyDataSetChanged();
                ctx.snack("导入成功!");
            } catch (Exception e) {
                ctx.snack("导入失败!");
            }
        });
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (!PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("enableHeader", true)) {
            TextView view = new TextView(ctx);
            view.setText("参数设定不可用!\n请前往设置开启\"启用参数设定\"");
            return view;
        }
        View v = LayoutInflater.from(ctx).inflate(R.layout.fragment_header, null);
        //初始化fab按钮
        FloatingActionButton add = v.findViewById(R.id.fragment_header_widget_add);
        add.setOnClickListener(this);

        //初始化ListView
        list = v.findViewById(R.id.fragment_header_view_list);

        TextView t = new TextView(ctx);
        t.setText("没有数据!");
        ((ViewGroup) list.getParent()).addView(t);
        list.setEmptyView(t);

        adapter = new HeaderAdapter(ctx);
        list.setAdapter(adapter);
        return v;
    }

    //fab按钮点击事件
    @Override
    public void onClick(View v) {
        AlertDialog.Builder menu = new AlertDialog.Builder(ctx);
        menu.setItems(new String[]{"导入配置", "导出配置", "添加参数"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");//无类型限制
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    readCall.launch(intent);
                    break;
                case 1:
                    writeToStorage();
                    break;
                case 2:
                    showAddDialog();
                    break;
            }
        });
        menu.show();
    }

    private void writeToStorage() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // 文件类型
        intent.setType("text/plain");
        // 文件名称
        intent.putExtra(Intent.EXTRA_TITLE, "YoumuCloudHeaderBackUp:" + System.currentTimeMillis() + ".txt");
        writeCall.launch(intent);
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("添加键值对");
        View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_keyvalue, null);
        builder.setView(view);
        builder.setPositiveButton("确定", (dialog, which) -> {
            TextInputLayout keyEdit = view.findViewById(R.id.dialog_editKey);
            TextInputLayout valueEdit = view.findViewById(R.id.dialog_editValue);
            String key = keyEdit.getEditText().getText().toString();
            String value = valueEdit.getEditText().getText().toString();

            if (TextUtils.isEmpty(key)) {
                ctx.snack("键不能为空!");
                showAddDialog();
                return;
            }

            if (!adapter.storage.isNull(key)) {
                ctx.snack("不要添加重复的键值对\n若要添加,请点击按对应按钮进行修改");
                showAddDialog();
                return;
            }

            if (TextUtils.isEmpty(value)) {
                ctx.snack("值不能为空!");
                showAddDialog();
                return;
            }
            adapter.storage.put(key, value);
            adapter.storage.save();
            adapter.notifyDataSetChanged();
            ctx.snack("添加成功!");
        });
        builder.create().show();
    }
}

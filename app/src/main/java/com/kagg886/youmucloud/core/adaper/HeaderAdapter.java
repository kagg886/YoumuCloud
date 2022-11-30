package com.kagg886.youmucloud.core.adaper;

import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.kagg886.youmucloud.R;
import com.kagg886.youmucloud.core.activity.MainActivity;
import com.kagg886.youmucloud.util.IOUtil;
import com.kagg886.youmucloud.util.storage.JSONObjectStorage;

public class HeaderAdapter extends BaseAdapter implements View.OnClickListener {

    private final MainActivity ctx;
    public JSONObjectStorage storage;

    public HeaderAdapter(MainActivity ctx) {
        this.ctx = ctx;
        storage = JSONObjectStorage.obtain(IOUtil.newFile(ctx.getExternalFilesDir("config"),"/headers.json").getAbsolutePath());
    }

    @Override
    public int getCount() {
        return storage.length();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Button btn = new Button(ctx);
        btn.setBackgroundResource(R.drawable.rounded_button);
        btn.setText(storage.indexKey(position));
        btn.setOnClickListener(this);
        return btn;
    }

    @Override
    public void notifyDataSetChanged() {
        JSONObjectStorage.destroy(storage.getWorkdir());
        storage = JSONObjectStorage.obtain(storage.getWorkdir());
        super.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        String key = ((Button) v).getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("修改键值对");
        View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_keyvalue,null);

        //锁定键 设置值
        TextInputLayout keyEdit = view.findViewById(R.id.dialog_editKey);
        keyEdit.getEditText().setText(key);
        keyEdit.getEditText().setInputType(InputType.TYPE_NULL);
        TextInputLayout valueEdit = view.findViewById(R.id.dialog_editValue);
        valueEdit.getEditText().setText(storage.optString(key));

        builder.setView(view);
        builder.setPositiveButton("确定", (dialog, which) -> {
            String value = valueEdit.getEditText().getText().toString();
            if (TextUtils.isEmpty(value)) {
                ctx.snack("值不能为空!");
                return;
            }
            storage.put(key,value);
            storage.save();
            notifyDataSetChanged();
            ctx.snack("修改成功!");
        });

        builder.setNegativeButton("删除", (dialog, which) -> {
            storage.remove(key);
            storage.save();
            notifyDataSetChanged();
            ctx.snack("删除成功!");
        });
        builder.create().show();
    }
}

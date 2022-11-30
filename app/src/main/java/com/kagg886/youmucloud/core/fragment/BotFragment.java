package com.kagg886.youmucloud.core.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.kagg886.youmucloud.core.activity.MainActivity;
import com.kagg886.youmucloud.core.adaper.BotAdapter;
import com.kagg886.youmucloud.core.widget.canFetchDownListView;
import kagg886.qinternet.QInternet;
import org.jetbrains.annotations.NotNull;

public class BotFragment extends Fragment implements canFetchDownListView.OnRefreshListener {

    private canFetchDownListView list;
    private BotAdapter adapter;
    private MainActivity ctx;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        ctx = (MainActivity) getActivity();
        if (ctx.getLayout().getSelectedTabPosition() == 0) {
            ctx.snack("当前已接入妖梦云服务器bot数:" + QInternet.getList().size());
        }
        list = new canFetchDownListView(ctx);
        adapter = new BotAdapter(ctx);
        list.setAdapter(adapter);
        list.setonRefreshListener(this);
        return list;
    }

    @Override
    public void onRefresh() {
        adapter.notifyDataSetChanged();
        list.onRefreshComplete();
    }
}

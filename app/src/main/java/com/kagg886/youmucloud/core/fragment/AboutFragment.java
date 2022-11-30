package com.kagg886.youmucloud.core.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.kagg886.youmucloud.core.activity.MainActivity;
import com.kagg886.youmucloud.util.ContextUtil;

public class AboutFragment extends Fragment implements View.OnKeyListener {
    private MainActivity ctx;
    private WebView view;


    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ctx = (MainActivity) getActivity();
        view = new WebView(ctx);
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
        view.loadUrl("http://" + ContextUtil.getServerAddress(ctx) + "/youmu/HomePage");
        return view;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            if (view.canGoBack()) {
                view.goBack();
            }
        }
        return true;
    }
}

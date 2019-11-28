package ru.ryzhikov.photomathsolver.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.ryzhikov.photomathsolver.R;

public class WebViewFragment extends Fragment {

    private static final String WOLFRAM_INPUT_URL = "https://www.wolframalpha.com/input/?i=";
    private final String mURL;

    private WebView mWebView;

    static WebViewFragment newInstance(String editedFormula) {
        return new WebViewFragment(editedFormula);
    }

    private WebViewFragment(String editedFormula) {
        mURL = WOLFRAM_INPUT_URL + editedFormula;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView = view.findViewById(R.id.web_view);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(mURL);

    }
}

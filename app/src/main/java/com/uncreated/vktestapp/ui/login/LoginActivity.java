package com.uncreated.vktestapp.ui.login;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.uncreated.vktestapp.R;
import com.uncreated.vktestapp.presentation.login.LoginPresenter;
import com.uncreated.vktestapp.ui.friends.FriendsActivity;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private final LoginPresenter mLoginPresenter = LoginPresenter.getInstance();

    /**
     * Для авторизации по протоколу OAuth 2.0
     */
    private WebView mWebView;

    private ImageView mLogoImageView;

    /**
     * Заголовок окна ошибки
     */
    private String mErrorTitle;

    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mErrorTitle = getResources().getString(R.string.error_title);

        mLogoImageView = findViewById(R.id.image_view);

        mWebView = findViewById(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return mLoginPresenter.onRedirect(request.getUrl().toString()) ||
                        super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return mLoginPresenter.onRedirect(url) ||
                        super.shouldOverrideUrlLoading(view, url);
            }
        });

        if (savedInstanceState != null) {
            mWebView.restoreState(savedInstanceState);
        }

        mLoginPresenter.onAttachView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLoginPresenter.onDetachView(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mWebView.saveState(outState);
    }

    @Override
    public void onLoggedIn() {
        Intent intent = new Intent(this, FriendsActivity.class);
        startActivity(intent);
    }

    @Override
    public void openUrl(String url) {
        mWebView.loadUrl(url);
    }

    @Override
    public void showError(String error) {
        if (mAlertDialog != null) {
            mAlertDialog.cancel();
        }
        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle(mErrorTitle)
                .setMessage(error)
                .show();
    }

    @Override
    public void showLoading(boolean show) {
        if (mAlertDialog != null) {
            mAlertDialog.cancel();
            mAlertDialog = null;
        }
        if (show) {
            mAlertDialog = new AlertDialog.Builder(this)
                    .setView(R.layout.dialog_loading)
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    public void showWeb() {
        mWebView.setVisibility(View.VISIBLE);
        mLogoImageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public Context getContext() {
        return this;
    }
}

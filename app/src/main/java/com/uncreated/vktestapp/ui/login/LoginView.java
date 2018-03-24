package com.uncreated.vktestapp.ui.login;

import com.uncreated.vktestapp.mvp.ViewBase;
import com.uncreated.vktestapp.presentation.login.LoginPresenter;

/**
 * Интерфейс наследуемый от {@link ViewBase} для {@link LoginPresenter}
 */
public interface LoginView extends ViewBase {

    void onLoggedIn();

    void openUrl(String url);

    void showError(String error);

    void showLoading(boolean show);

    void showWeb();
}

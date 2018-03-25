package com.uncreated.vktestapp.presentation.login;

import com.uncreated.vktestapp.model.vk.VkHttpClient;
import com.uncreated.vktestapp.mvp.PresenterBase;
import com.uncreated.vktestapp.ui.login.LoginView;

/**
 * Singleton
 * Презентер для {@link LoginView}
 */
public class LoginPresenter extends PresenterBase<LoginView> {
    private static final int OPEN_URL_ID = 3;
    private static final int SHOW_WEB_ID = 4;
    private static final int ERROR_ID = 5;
    private static final int LOGGED_IN_ID = 6;

    private static final LoginPresenter ourInstance = new LoginPresenter();

    public static LoginPresenter getInstance() {
        return ourInstance;
    }

    private VkHttpClient mVkHttpClient = VkHttpClient.getInstance();

    private LoginPresenter() {
        getUser();
        runCommand(new Command(15, () -> mView.showError("123")));
        runCommand(new Command(15, () -> mView.showError("456")));
        runCommand(new Command(15, () -> mView.showError("789")));
    }

    private void getUser() {
        mVkHttpClient.getUser(this::onLoggedIn, errorMessage -> showLogInPage());
    }

    public boolean onRedirect(String url) {
        try {
            if (mVkHttpClient.auth(url)) {
                getUser();
                return true;
            }
        } catch (VkHttpClient.AuthException e) {
            onLogInFailed(e.getMessage());
            return true;
        }
        return false;
    }

    private void showLogInPage() {
        runCommand(new UniqueCommand(SHOW_WEB_ID, () -> mView.showWeb()));
        runCommand(new Command(OPEN_URL_ID, () -> mView.openUrl(VkHttpClient.AUTH_URL)));
    }

    private void onLoggedIn() {
        clearCommands();
        runCommand(new DisposableCommand(LOGGED_IN_ID, () -> mView.onLoggedIn()));
    }

    private void onLogInFailed(String error) {
        runCommand(new DisposableCommand(ERROR_ID, () -> mView.showError(error)));
        showLogInPage();
    }
}


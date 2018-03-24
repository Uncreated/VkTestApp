package com.uncreated.vktestapp.presentation.login;

import android.os.AsyncTask;

import com.uncreated.vktestapp.model.Session;
import com.uncreated.vktestapp.model.VkClient;
import com.uncreated.vktestapp.mvp.PresenterBase;
import com.uncreated.vktestapp.ui.login.LoginView;

/**
 * Singleton
 * Презентер для {@link LoginView}
 */
public class LoginPresenter extends PresenterBase<LoginView> {
    private static final LoginPresenter ourInstance = new LoginPresenter();

    public static LoginPresenter getInstance() {
        return ourInstance;
    }

    private VkClient mVkClient = VkClient.getInstance();
    private LogInHandler mLogInHandler = new LogInHandler();

    private LoginPresenter() {
    }

    @Override
    protected void onFirstAttachView() {
        showLoading(true);

        Session session = Session.load(mView.getContext());
        if (session != null) {
            //TODO:asyncTask
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    if (aBoolean) {
                        Session.setCurrent(session);
                        onLoggedIn();
                    } else {
                        showLogInPage();
                    }
                }

                @Override
                protected Boolean doInBackground(Void... voids) {
                    return mVkClient.checkSession(session);
                }
            }.execute();
        } else {
            showLogInPage();
        }
    }

    public boolean onRedirect(String url) {
        try {
            Session session = mLogInHandler.getToken(url);
            if (session != null) {
                Session.setCurrent(session);
                onLoggedIn();
                return true;
            }
        } catch (LogInHandler.AuthException e) {
            onLogInFailed(e.getMessage());
            return true;
        }
        return false;
    }

    private void showLoading(boolean show) {
        runCommand(new Command(false, () -> mView.showLoading(show)), false);
    }

    private void showLogInPage() {
        showLoading(false);
        runCommand(new Command(false, () -> mView.showWeb()), true);
        runCommand(new Command(true, () -> mView.openUrl(LogInHandler.AUTH_URI)), false);
    }

    private void onLoggedIn() {
        showLoading(false);
        runCommand(new Command(true, () -> {
            Session.getCurrent().save(mView.getContext());
            mView.onLoggedIn();
        }), false);
    }

    private void onLogInFailed(String error) {
        showLoading(false);
        runCommand(new Command(true, () -> {
            mView.showError(error);
            mView.openUrl(LogInHandler.AUTH_URI);
        }), false);
    }
}

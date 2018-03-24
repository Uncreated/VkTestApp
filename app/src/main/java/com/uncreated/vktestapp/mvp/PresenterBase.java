package com.uncreated.vktestapp.mvp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.LinkedList;

/**
 * Базовый класс презентера паттерна MVP
 *
 * @param <T> {@link ViewBase} наследник с перечнем команд, принимаемых от презентера
 */
public abstract class PresenterBase<T extends ViewBase> {

    protected T mView;
    /**
     * Список обязательных команд
     */
    protected LinkedList<Command> mRequiredCommands = new LinkedList<>();
    /**
     * Последняя команда
     */
    protected Command mLastCommand;

    private boolean mFirstAttach = true;

    /**
     * Привязывает view к презентеру и выполняет {@link this#onFirstAttachView()},
     * если вызов первый, либо вызывает срабатывание всех накопленных команд
     *
     * @param view наследуемый от {@link ViewBase}
     */
    public void onAttachView(@NonNull T view) {
        mView = view;
        if (mFirstAttach) {
            mFirstAttach = false;
            onFirstAttachView();
        } else {
            runAllCommands();
        }
    }

    /**
     * Вызывается при первой привязке view к презентеру
     */
    protected abstract void onFirstAttachView();

    /**
     * Отвязывает view от презентера
     *
     * @param view
     */
    public void onDetachView(T view) {
        if (mView == view) {
            mView = null;
        }
    }

    /**
     * Сохраняет команду как последнюю и,
     * если она не null и к презентеру привязан {@link ViewBase}, выполняет её
     *
     * @param command выполняемая команда
     */
    protected void runCommand(@Nullable Command command, boolean required) {
        if (required) {
            if (command != null) {
                mRequiredCommands.add(command);
            }
            mLastCommand = null;
        } else {
            mLastCommand = command;
        }
        if (command != null) {
            command.run();
        }
    }


    protected void runAllCommands() {
        for (Command command : mRequiredCommands) {
            command.run();
        }
        runCommand(mLastCommand, false);
    }

    public class Command {
        private Runnable mRunnable;
        private boolean mSingleCall;

        public Command(boolean singleCall, Runnable runnable) {
            mSingleCall = singleCall;
            mRunnable = runnable;
        }

        /**
         * Выполняет операцию, если она определена и к презентеру прикреплен view
         * Если команда одиночного вызова, определение операции стирается
         */
        void run() {
            if (mRunnable != null && mView != null) {
                mRunnable.run();
                if (mSingleCall) {
                    mRunnable = null;
                }
            }
        }
    }
}

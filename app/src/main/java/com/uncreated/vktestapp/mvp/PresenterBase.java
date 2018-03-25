package com.uncreated.vktestapp.mvp;

import android.support.annotation.NonNull;

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
    protected LinkedList<Command> mCommands = new LinkedList<>();

    /**
     * Привязывает view к презентеру и вызывает срабатывание всех накопленных команд
     *
     * @param view наследуемый от {@link ViewBase}
     */
    public void onAttachView(@NonNull T view) {
        mView = view;
        runAllCommands();
    }

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
     * Сбрасывает все сохранённые команды
     */
    protected void clearCommands() {
        mCommands.clear();
    }

    /**
     * Сохраняет команду как последнюю и,
     * если она не null и к презентеру привязан {@link ViewBase}, выполняет её
     *
     * @param command выполняемая команда
     */
    protected void runCommand(@NonNull Command command) {
        command.add();
        command.run();
    }

    protected void runAllCommands() {
        for (Command command : mCommands) {
            command.run();
        }
    }

    /**
     * Срабатывает только 1 раз
     */
    public class DisposableCommand extends Command {
        public DisposableCommand(int id, Runnable runnable) {
            super(id, runnable);
        }

        @Override
        boolean run() {
            if (super.run()) {
                mRunnable = null;
                return true;
            }
            return false;
        }
    }

    /**
     * Удаляет другие команды с таким же id
     */
    public class UniqueCommand extends Command {
        public UniqueCommand(int id, Runnable runnable) {
            super(id, runnable);
        }

        @Override
        void add() {
            int size = mCommands.size();
            for (int i = size - 1; i >= 0; i--) {
                if (mCommands.get(i).mId == mId) {
                    mCommands.remove(i);
                }
            }
            super.add();
        }
    }

    public class Command {
        protected int mId;
        protected Runnable mRunnable;

        public Command(int id, @NonNull Runnable runnable) {
            mId = id;
            mRunnable = runnable;
        }

        /**
         * Выполняет операцию, если она определена и к презентеру прикреплен view
         * Если команда одиночного вызова, определение операции стирается
         */
        boolean run() {
            if (mRunnable != null && mView != null) {
                mRunnable.run();
                return true;
            }
            return false;
        }

        void add() {
            mCommands.add(this);
        }

        public int getId() {
            return mId;
        }
    }
}

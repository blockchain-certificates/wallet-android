package com.learningmachine.android.app.data.log;

import timber.log.Timber;

public class NoLoggingTree extends Timber.Tree {

    @Override
    public void v(String message, Object... args) {
    }

    @Override
    public void v(Throwable throwable, String message, Object... args) {
    }

    @Override
    public void d(String message, Object... args) {
    }

    @Override
    public void d(Throwable throwable, String message, Object... args) {
    }

    @Override
    public void i(String message, Object... args) {
    }

    @Override
    public void i(Throwable throwable, String message, Object... args) {
    }

    @Override
    public void w(String message, Object... args) {
    }

    @Override
    public void w(Throwable throwable, String message, Object... args) {
    }

    @Override
    public void e(String message, Object... args) {
    }

    @Override
    public void e(Throwable throwable, String message, Object... args) {
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {

    }
}

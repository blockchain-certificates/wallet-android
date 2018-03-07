package com.learningmachine.android.app.util;


import android.util.Log;
import timber.log.Timber;

public class FileLoggingTree extends Timber.DebugTree {

    static StringBuilder memoryLog;

    public static String logAsString() {
        return memoryLog.toString();
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (priority == Log.VERBOSE) {
            return;
        }

        if(memoryLog == null) {
            memoryLog = new StringBuilder();
        }

        String logMessage = tag + ": " + message;
        switch (priority) {
            case Log.DEBUG:
                memoryLog.append(String.format("[d] %s\n", logMessage));
                break;
            case Log.INFO:
                memoryLog.append(String.format("[i] %s\n", logMessage));
                break;
            case Log.WARN:
                memoryLog.append(String.format("[w] %s\n", logMessage));
                break;
            case Log.ERROR:
                memoryLog.append(String.format("[e] %s\n", logMessage));
                break;
        }

        int maxMemoryLogSize = 1024*1024 / 2;
        if (memoryLog.length() > maxMemoryLogSize) {
            memoryLog.replace(0, maxMemoryLogSize / 2, "");
        }
    }
}
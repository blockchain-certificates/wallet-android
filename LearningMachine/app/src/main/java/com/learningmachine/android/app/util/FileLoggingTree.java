package com.learningmachine.android.app.util;


import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import timber.log.Timber;

public class FileLoggingTree extends Timber.DebugTree {

    static StringBuilder memoryLog;

    //TODO: call this function also before closing the app
    public static void saveLogToFile(Context context) {
        FileUtils.saveLogs(context, memoryLog);
        //After save, clean memoryLog
        memoryLog = new StringBuilder();
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (priority == Log.VERBOSE) {
            return;
        }

        if(memoryLog == null) {
            memoryLog = new StringBuilder();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.US);
        String dateString = formatter.format(new java.util.Date());
        String logMessage = "[" + dateString + "]" + tag + ": " + message;
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
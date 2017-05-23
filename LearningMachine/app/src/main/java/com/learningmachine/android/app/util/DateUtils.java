package com.learningmachine.android.app.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtils {

    public static String formatDateString(String dateString) {
        DateTime dateTime = DateTime.parse(dateString);
        DateTimeFormatter formatter = DateTimeFormat.mediumDate();
        return formatter.print(dateTime);
    }
}

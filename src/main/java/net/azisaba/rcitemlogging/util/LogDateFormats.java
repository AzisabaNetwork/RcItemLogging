package net.azisaba.rcitemlogging.util;

import java.text.SimpleDateFormat;

public class LogDateFormats {
    public static final SimpleDateFormat TIMESTAMP = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");

    public static String getTimestampStr() {
        return LogDateFormats.TIMESTAMP.format(System.currentTimeMillis());
    }
}

package com.soulstrewn.zonepos.Utility;

import java.util.Date;

/**
 * User: jeff
 * Date: Mar 4, 2009
 * Time: 3:26:22 PM
 * No warranties, expressed or implied, including the warranty that it'll actually work, etc...
 */
import java.util.Date;

/**
 * User: jeff
 * Date: Sep 6, 2008
 * Time: 8:28:18 PM
 * No warranties, expressed or implied, including the warranty that it'll actually work, etc...
 */
public class Log {
    public static enum LogLevel
    {
        NONE, NOTE, WARNING, CAUTION, CRITICAL
    }

    public static void Log(LogLevel level, String message)
    {
        StringBuilder sb = new StringBuilder()
                .append(level.toString())
                .append("\t")
                .append((new Date()).toString())
                .append(":\t")
                .append(message);
        System.out.println(sb.toString());
    }

    final public static String Version = "0.1a";

}



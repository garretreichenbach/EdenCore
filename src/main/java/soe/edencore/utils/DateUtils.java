package soe.edencore.utils;

import java.sql.Date;

/**
 * DateUtils.java
 * Utility functions for date objects.
 *
 * @author TheDerpGamer
 * @since 03/20/2021
 */
public class DateUtils {

    public static int getAgeDays(Date date) {
        Date current = new Date(System.currentTimeMillis());
        long difference = Math.abs(current.getTime() - date.getTime());
        return (int) (difference / (1000 * 60 * 60 * 24));
    }

    public static int getAgeDays(long time) {
        return getAgeDays(new Date(time));
    }
}

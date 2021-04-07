package soe.edencore.data.logs;

import soe.edencore.utils.DateUtils;
import java.util.Date;

/**
 * AdminLogEntry.java
 * <Description>
 *
 * @author TheDerpGamer
 * @since 03/20/2021
 */
public class AdminLogEntry {

    public LogType logType;
    public long time;
    public String adminName;
    public String description;

    public AdminLogEntry(LogType logType, long time, String adminName, String description) {
        this.logType = logType;
        this.time = time;
        this.adminName = adminName;
        this.description = description;
    }

    public Date getDate() {
        return new Date(time);
    }

    public String getDateString() {
        Date date = new Date(time);
        return date.getMonth() + "/" + date.getDay() + "/" + date.getYear();
    }

    public int getAge() {
        return DateUtils.getAgeDays(time);
    }

    public enum LogType {ALL, SERVER, PLAYER, FACTION, ENTITY, OTHER}
}

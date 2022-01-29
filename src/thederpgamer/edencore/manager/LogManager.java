package thederpgamer.edencore.manager;

import api.DebugFile;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.annotation.Nullable;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.utils.DataUtils;
import thederpgamer.edencore.utils.DateUtils;

/**
 * Handles mod specific logging for EdenCore.
 *
 * @author TheDerpGamer
 * @since 06/27/2021
 */
public class LogManager {

  private enum MessageType {
    DEBUG("[DEBUG]: "),
    INFO("[INFO]: "),
    WARNING("[WARNING]: "),
    ERROR("[ERROR]: "),
    CRITICAL("[CRITICAL]: ");

    public String prefix;

    MessageType(String prefix) {
      this.prefix = prefix;
    }
  }

  private static FileWriter logWriter;
  private static final ArrayList<String> messageQueue = new ArrayList<>();

  public static void initialize() {
    String logFolderPath = DataUtils.getWorldDataPath() + "/logs";
    File logsFolder = new File(logFolderPath);
    if (!logsFolder.exists()) logsFolder.mkdirs();
    else {
      if (logsFolder.listFiles() != null && logsFolder.listFiles().length > 0) {
        File[] logFiles = new File[logsFolder.listFiles().length];
        int j = logFiles.length - 1;
        for (int i = 0; i < logFiles.length && j >= 0; i++) {
          logFiles[i] = logsFolder.listFiles()[j];
          j--;
        }

        for (File logFile : logFiles) {
          String fileName = logFile.getName().replace(".txt", "");
          int logNumber = Integer.parseInt(fileName.substring(fileName.indexOf("log") + 3)) + 1;
          String newName = logFolderPath + "/log" + logNumber + ".txt";
          if (logNumber < ConfigManager.getMainConfig().getInt("max-world-logs") - 1)
            logFile.renameTo(new File(newName));
          else logFile.delete();
        }
      }
    }
    try {
      File newLogFile = new File(logFolderPath + "/log0.txt");
      if (newLogFile.exists()) newLogFile.delete();
      newLogFile.createNewFile();
      logWriter = new FileWriter(newLogFile);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  public static void logInfo(String message) {
    logMessage(MessageType.INFO, message);
  }

  public static void logDebug(String message) {
    if (ConfigManager.getMainConfig().getBoolean("debug-mode"))
      logMessage(MessageType.DEBUG, message);
  }

  public static void logWarning(String message, @Nullable Exception exception) {
    if (exception != null)
      logMessage(MessageType.WARNING, message + ":\n" + exception.getMessage());
    else logMessage(MessageType.WARNING, message);
  }

  public static void logException(String message, Exception exception) {
    exception.printStackTrace();
    logMessage(MessageType.ERROR, message + ":\n" + exception.getMessage());
  }

  public static void logCritical(String message, Exception exception) {
    exception.printStackTrace();
    logMessage(MessageType.CRITICAL, message + ":\n" + exception.getMessage());
    System.exit(1);
  }

  private static void logMessage(MessageType messageType, String message) {
    if (!messageQueue.contains(message) || messageType.equals(MessageType.CRITICAL)) {
      String prefix = "[" + DateUtils.getTimeFormatted() + "] [EdenCore] " + messageType.prefix;
      try {
        StringBuilder builder = new StringBuilder();
        builder.append(prefix);
        String[] lines = message.split("\n");
        if (lines.length > 1) {
          for (int i = 0; i < lines.length; i++) {
            builder.append(lines[i]);
            if (i < lines.length - 1)
              if (i > 1) for (int j = 0; j < prefix.length(); j++) builder.append(" ");
          }
        } else builder.append(message);
        System.out.println(builder.toString());
        logWriter.append(builder.toString()).append("\n");
        logWriter.flush();
        DebugFile.log(builder.toString(), EdenCore.getInstance());
      } catch (IOException var3) {
        var3.printStackTrace();
      }
      if (messageType.equals(MessageType.CRITICAL)) System.exit(1);
    }
    if (messageQueue.size() >= 5)
      messageQueue.remove(messageQueue.size() - 1); // Prevent spam from repeated messages
    messageQueue.add(message);
  }

  public static void clearLogs() {
    String logFolderPath = DataUtils.getWorldDataPath() + "/logs";
    File logsFolder = new File(logFolderPath);
    if (logsFolder.listFiles() != null && logsFolder.listFiles().length > 0) {
      for (File logFile : logsFolder.listFiles()) {
        String logName = logFile.getName().replace(".txt", "");
        int logNumber = Integer.parseInt(logName.substring(logName.indexOf("log") + 3));
        if (logNumber != 0
            && logNumber - 1 >= ConfigManager.getMainConfig().getInt("max-world-logs"))
          logFile.delete();
      }
    }
  }
}

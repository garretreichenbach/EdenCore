package thederpgamer.soe.manager;

/**
 * MessageType enum for LogManager.
 *
 * @author TheDerpGamer
 * @since 06/27/2021
 */
public enum MessageType {
    INFO("[INFO]: "),
    WARNING("[WARNING]: "),
    ERROR("[ERROR]: "),
    CRITICAL("[CRITICAL]: ");

    public String prefix;

    MessageType(String prefix) {
        this.prefix = prefix;
    }
}
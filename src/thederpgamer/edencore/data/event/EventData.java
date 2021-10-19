package thederpgamer.edencore.data.event;

import org.schema.common.util.linAlg.Vector3i;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/21/2021]
 */
public class EventData {

    public String name;
    public Vector3i sector;
    public EventType eventType;

    public enum EventType {
        ALL,
        CAPTURE,
        DEFENSE,
        DESTROY,
        ESCORT,
        PURSUIT
    }
}

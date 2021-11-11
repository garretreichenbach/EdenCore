package thederpgamer.edencore.data.event;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.common.util.linAlg.Vector3i;
import thederpgamer.edencore.data.SerializableData;

import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/21/2021]
 */
public abstract class EventData implements EventUpdater, SerializableData {

    public static final int NONE = 0;
    public static final int WAITING = 1;
    public static final int ACTIVE = 2;
    public static final int FINISHED = 3;

    public enum EventType {
        ALL,
        CAPTURE,
        DEFENSE,
        DESTROY,
        ESCORT,
        PURSUIT;

        public static EventType deserialize(PacketReadBuffer readBuffer) throws IOException {
            String s = readBuffer.readString().toUpperCase();
            switch(s) {
                case "CAPTURE": return CAPTURE;
                case "DEFENSE": return DEFENSE;
                case "DESTROY": return DESTROY;
                case "ESCORT": return ESCORT;
                case "PURSUIT": return PURSUIT;
                default: return ALL;
            }
        }
    }

    protected String name;
    protected String description;
    protected EventType eventType;
    protected EventRuleset ruleset;
    protected Vector3i sector;
    protected EventTarget[] targets;

    protected int status;

    public EventData(String name, String description, EventType eventType, EventRuleset ruleset, Vector3i sector, EventTarget... targets) {
        this.name = name;
        this.description = description;
        this.eventType = eventType;
        this.ruleset = ruleset;
        this.targets = targets;
    }

    public EventData(PacketReadBuffer readBuffer) throws IOException {
        deserialize(readBuffer);
    }

    public abstract void deserialize(PacketReadBuffer readBuffer) throws IOException;
    public abstract void serialize(PacketWriteBuffer writeBuffer) throws IOException;
}

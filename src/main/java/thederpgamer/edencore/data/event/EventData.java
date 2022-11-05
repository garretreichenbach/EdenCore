package thederpgamer.edencore.data.event;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.common.util.linAlg.Vector3i;
import thederpgamer.edencore.data.SerializableData;
import thederpgamer.edencore.data.event.types.EventRuleSet;
import thederpgamer.edencore.data.event.types.capture.CaptureEvent;
import thederpgamer.edencore.data.event.types.defense.DefenseEvent;
import thederpgamer.edencore.manager.LogManager;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/21/2021]
 */
public abstract class EventData implements EventUpdater, SerializableData, Serializable {

    public static final int NONE = 0;
    public static final int WAITING = 1;
    public static final int ACTIVE = 2;
    public static final int FINISHED = 3;
	public static final int PVE = 0;
	public static final int PVP = 1;
	public static final int DAILY = 2;
	public static final int WEEKLY = 4;
	public static final int MONTHLY = 8;

	public enum EventType {ALL, CAPTURE, DEFENSE, DESTROY, ESCORT, PURSUIT}

    protected String name;
    protected String description;
    protected EventType eventType;
    protected EventRuleSet ruleset;
    protected Vector3i sector;
    protected EventTarget[] targets;

    protected int status;

    public EventData(String name, String description, EventType eventType, EventRuleSet ruleset, Vector3i sector, EventTarget... targets) {
        this.name = name;
        this.description = description;
        this.eventType = eventType;
        this.ruleset = ruleset;
        this.targets = targets;
    }

    public EventData(PacketReadBuffer readBuffer) throws IOException {
        deserialize(readBuffer);
    }

    public EventEnemyData getToughestEnemy() {
        double largestMass = 0;
        EventEnemyData toughestEnemy = null;
        for(EventEnemyData enemyData : getEnemies()) {
            if(enemyData.getSpawnCount() <= 0) continue;
            double currentMass = 0;
            for(int i = 0; i < enemyData.getSpawnCount(); i ++) currentMass += enemyData.getCatalogEntry().getMass();
            if(currentMass > largestMass) {
                largestMass = currentMass;
                toughestEnemy = enemyData;
            }
        }
        return toughestEnemy;
    }

    public abstract void deserialize(PacketReadBuffer readBuffer) throws IOException;
    public abstract void serialize(PacketWriteBuffer writeBuffer) throws IOException;

    public abstract String getAnnouncement();
    public abstract ArrayList<EventEnemyData> getEnemies();

    public static EventData fromPacket(PacketReadBuffer readBuffer) {
        try {
            EventType type = EventType.values()[readBuffer.readInt()];
            switch(type) {
                case CAPTURE: return new CaptureEvent(readBuffer);
                case DEFENSE: return new DefenseEvent(readBuffer);
                case DESTROY: return new DestroyEvent(readBuffer);
                case ESCORT: return new EscortEvent(readBuffer);
                case PURSUIT: return new PursuitEvent(readBuffer);
                default: throw new IllegalStateException("Invalid event type: " + type.name());
            }
        } catch(Exception exception) {
            LogManager.logException("Failed to deserialize EventData", exception);
        }
        return null;
    }
}

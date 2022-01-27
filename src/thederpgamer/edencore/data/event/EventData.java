package thederpgamer.edencore.data.event;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import java.io.IOException;
import org.schema.common.util.linAlg.Vector3i;
import thederpgamer.edencore.data.SerializableData;
import thederpgamer.edencore.data.event.types.CaptureEvent;
import thederpgamer.edencore.manager.LogManager;

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
    PURSUIT
  }

  protected String name;
  protected String description;
  protected EventType eventType;
  protected EventRuleset ruleset;
  protected Vector3i sector;
  protected EventTarget[] targets;

  protected int status;

  public EventData(
      String name,
      String description,
      EventType eventType,
      EventRuleset ruleset,
      Vector3i sector,
      EventTarget... targets) {
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

  public static EventData fromPacket(PacketReadBuffer readBuffer) {
    try {
      EventType type = EventType.values()[readBuffer.readInt()];
      switch (type) {
        case CAPTURE:
          return new CaptureEvent(readBuffer);
          // case DEFENSE: return new DefenseEvent(readBuffer);
          // case DESTROY: return new DestroyEvent(readBuffer);
          // case ESCORT: return new EscortEvent(readBuffer);
          // case PURSUIT: return new PursuitEvent(readBuffer);
        default:
          throw new IllegalStateException("Invalid event type: " + type.name());
      }
    } catch (Exception exception) {
      LogManager.logException("Failed to deserialize EventData", exception);
    }
    return null;
  }
}

package thederpgamer.edencore.data.event.types;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import java.io.IOException;
import org.schema.common.util.linAlg.Vector3i;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.data.event.EventRuleset;
import thederpgamer.edencore.data.event.EventTarget;
import thederpgamer.edencore.data.event.target.DefenseTarget;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/19/2021]
 */
public class DefenseEvent extends EventData {

  public DefenseEvent(
      String name,
      String description,
      EventRuleset ruleset,
      Vector3i sector,
      EventTarget... targets) {
    super(name, description, EventType.DEFENSE, ruleset, sector, targets);
  }

  public DefenseEvent(PacketReadBuffer readBuffer) throws IOException {
    super(readBuffer);
  }

  @Override
  public void deserialize(PacketReadBuffer readBuffer) throws IOException {
    name = readBuffer.readString();
    description = readBuffer.readString();
    eventType = EventType.DEFENSE;
    ruleset = new EventRuleset(readBuffer);
    sector = readBuffer.readVector();
    int size = readBuffer.readInt();
    if (size > 0) {
      targets = new EventTarget[size];
      for (int i = 0; i < size; i++) targets[i] = new DefenseTarget(readBuffer);
    }
  }

  @Override
  public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
    writeBuffer.writeString(name);
    writeBuffer.writeString(description);
    ruleset.serialize(writeBuffer);
    writeBuffer.writeVector(sector);
    writeBuffer.writeInt(targets.length);
    if (targets.length > 0) for (EventTarget target : targets) target.serialize(writeBuffer);
  }

  @Override
  public int getStatus() {
    return 0;
  }

  @Override
  public void start() {}

  @Override
  public void update(float deltaTime) {}

  @Override
  public void end() {}
}

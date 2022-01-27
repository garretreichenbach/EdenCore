package thederpgamer.edencore.data.event.target;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import java.io.IOException;
import thederpgamer.edencore.data.event.EventTarget;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/19/2021]
 */
public class DefenseTarget extends EventTarget {

  public DefenseTarget(String targetEntityUID) {
    super(targetEntityUID);
  }

  public DefenseTarget(PacketReadBuffer readBuffer) throws IOException {
    super(readBuffer);
  }

  @Override
  public int getProgress() {
    return 0; // Todo
  }

  @Override
  public void deserialize(PacketReadBuffer readBuffer) throws IOException {
    target = readBuffer.readString();
  }

  @Override
  public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
    writeBuffer.writeString((String) target);
  }
}

package thederpgamer.edencore.network.client.exchange;

import api.mod.config.PersistentObjectUtil;
import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import java.io.IOException;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.exchange.BlueprintExchangeItem;
import thederpgamer.edencore.data.exchange.ExchangeItem;
import thederpgamer.edencore.data.exchange.ItemExchangeItem;
import thederpgamer.edencore.data.exchange.ResourceExchangeItem;

/**
 * Requests the creation of an exchange item from the server.
 *
 * <p>[CLIENT -> SERVER]
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/18/2021]
 */
public class ExchangeItemCreatePacket extends Packet {

  private int type;
  private ExchangeItem item;

  public ExchangeItemCreatePacket() {}

  public ExchangeItemCreatePacket(int type, ExchangeItem item) {
    this.type = type;
    this.item = item;
  }

  @Override
  public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
    type = packetReadBuffer.readInt();
    if (type == 0) item = new BlueprintExchangeItem(packetReadBuffer);
    else if (type == 1) item = new ResourceExchangeItem(packetReadBuffer);
    else item = new ItemExchangeItem(packetReadBuffer);
  }

  @Override
  public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
    packetWriteBuffer.writeInt(type);
    item.serialize(packetWriteBuffer);
  }

  @Override
  public void processPacketOnClient() {}

  @Override
  public void processPacketOnServer(PlayerState playerState) {
    assert playerState.isAdmin();
    PersistentObjectUtil.addObject(EdenCore.getInstance().getSkeleton(), item);
    PersistentObjectUtil.save(EdenCore.getInstance().getSkeleton());
    EdenCore.getInstance().updateClientCacheData();
  }
}

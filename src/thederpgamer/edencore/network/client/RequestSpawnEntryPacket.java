package thederpgamer.edencore.network.client;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.game.PlayerUtils;
import java.io.IOException;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.server.controller.BluePrintController;
import org.schema.game.server.controller.EntityNotFountException;
import org.schema.game.server.data.blueprintnw.BlueprintEntry;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.utils.EntityUtils;

/**
 * Requests an entity to be spawned from an exchange purchase.
 *
 * <p>[CLIENT] -> [SERVER]
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/20/2021]
 */
public class RequestSpawnEntryPacket extends Packet {

  private String entryName;
  private boolean docked;
  private boolean enemy;

  public RequestSpawnEntryPacket() {}

  public RequestSpawnEntryPacket(String entryName, boolean docked, boolean enemy) {
    this.entryName = entryName;
    this.docked = docked;
    this.enemy = enemy;
  }

  @Override
  public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
    entryName = packetReadBuffer.readString();
    docked = packetReadBuffer.readBoolean();
    enemy = packetReadBuffer.readBoolean();
  }

  @Override
  public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
    packetWriteBuffer.writeString(entryName);
    packetWriteBuffer.writeBoolean(docked);
    packetWriteBuffer.writeBoolean(enemy);
  }

  @Override
  public void processPacketOnClient() {}

  @Override
  public void processPacketOnServer(PlayerState playerState) {
    try {
      BlueprintEntry entry = BluePrintController.active.getBlueprint(entryName);
      if (docked) EntityUtils.spawnEntryOnDock(playerState, entry);
      else {
        if (enemy) EntityUtils.spawnEnemy(playerState, entry);
        else EntityUtils.spawnEntry(playerState, entry);
      }
    } catch (EntityNotFountException exception) {
      exception.printStackTrace();
      PlayerUtils.sendMessage(
          playerState,
          "There was a severe error in spawning your entity! Please notify an admin ASAP!");
    }
    EdenCore.getInstance().updateClientCacheData();
  }
}

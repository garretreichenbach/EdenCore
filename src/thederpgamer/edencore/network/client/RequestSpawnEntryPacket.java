package thederpgamer.edencore.network.client;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.game.PlayerUtils;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.server.controller.BluePrintController;
import org.schema.game.server.controller.EntityNotFountException;
import org.schema.game.server.data.blueprintnw.BlueprintEntry;
import thederpgamer.edencore.utils.EntityUtils;

import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/20/2021]
 */
public class RequestSpawnEntryPacket extends Packet {

    private String entryName;

    public RequestSpawnEntryPacket() {

    }

    public RequestSpawnEntryPacket(String entryName) {
        this.entryName = entryName;
    }

    @Override
    public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
        entryName = packetReadBuffer.readString();
    }

    @Override
    public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
        packetWriteBuffer.writeString(entryName);
    }

    @Override
    public void processPacketOnClient() {

    }

    @Override
    public void processPacketOnServer(PlayerState playerState) {
        try {
            BlueprintEntry entry = BluePrintController.active.getBlueprint(entryName);
            EntityUtils.spawnEntry(playerState, entry, false);
        } catch(EntityNotFountException exception) {
            exception.printStackTrace();
            PlayerUtils.sendMessage(playerState, "There was a severe error in spawning your entity! Please notify an admin ASAP!");
        }
    }
}

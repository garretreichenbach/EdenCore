package thederpgamer.edencore.data.event;

import api.common.GameCommon;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.faction.Faction;
import org.schema.game.server.controller.BluePrintController;
import org.schema.game.server.data.blueprintnw.BlueprintEntry;
import thederpgamer.edencore.data.SerializableData;

import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/26/2021]
 */
public class SquadMemberData implements SerializableData {

    public String playerName;
    public String shipName;
    public int factionId;
    public byte factionRank;
    public double shipMass;
    public boolean ready;

    public SquadMemberData(PlayerState playerState) {
        this.playerName = playerState.getName();
        this.shipName = null;
        this.factionId = playerState.getFactionId();
        this.factionRank = playerState.getFactionController().getFactionRank();
        this.shipMass = 0.0;
        this.ready = false;
    }

    public SquadMemberData(PacketReadBuffer readBuffer) throws IOException {
        deserialize(readBuffer);
    }

    @Override
    public void deserialize(PacketReadBuffer readBuffer) throws IOException {
        playerName = readBuffer.readString();
        shipName = readBuffer.readString();
        factionId = readBuffer.readInt();
        factionRank = readBuffer.readByte();
        shipMass = readBuffer.readDouble();
        ready = readBuffer.readBoolean();
    }

    @Override
    public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
        writeBuffer.writeString(playerName);
        writeBuffer.writeString(shipName);
        writeBuffer.writeInt(factionId);
        writeBuffer.writeByte(factionRank);
        writeBuffer.writeDouble(shipMass);
        writeBuffer.writeBoolean(ready);
    }

    @Override
    public void updateClients() {

    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean hasSelectedShip() {
        return shipName != null && shipMass > 0.0;
    }

    public String getShipName() {
        return shipName;
    }

    public int getFactionId() {
        return factionId;
    }

    public Faction getFaction() {
        return GameCommon.getGameState().getFactionManager().getFaction(factionId);
    }

    public double getShipMass() {
        return shipMass;
    }

    public boolean isReady() {
        return ready && hasSelectedShip();
    }

    public void setReady(boolean ready) {
        if(hasSelectedShip()) this.ready = ready;
        else this.ready = false;
    }

    public void setShip(String name) {
        assert name != null && !name.isEmpty() : "Ship name cannot be empty!";
        try {
            BlueprintEntry entry = BluePrintController.active.getBlueprint(name);
            shipName = entry.getName();
            shipMass = entry.getMass();
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }
}

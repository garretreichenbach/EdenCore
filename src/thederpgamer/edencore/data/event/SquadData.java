package thederpgamer.edencore.data.event;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import thederpgamer.edencore.data.SerializableData;
import thederpgamer.edencore.manager.SquadManager;

import java.io.IOException;
import java.util.ArrayList;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/13/2021]
 */
public class SquadData implements SerializableData {

    public ArrayList<SquadMemberData> squadMembers;
    public int squadID;

    public SquadData() {
        squadID = SquadManager.squadIDCounter;
        SquadManager.squadIDCounter ++;
        SquadManager.addSquadData(this);
    }

    public boolean ready() {
        for(SquadMemberData memberData : squadMembers) if(!memberData.ready) return false;
        return true;
    }

    public boolean isPlayerInSquad(String playerName) {
        for(SquadMemberData memberData : squadMembers) if(memberData.playerName.equals(playerName)) return true;
        return false;
    }

    @Override
    public void deserialize(PacketReadBuffer readBuffer) throws IOException {

    }

    @Override
    public void serialize(PacketWriteBuffer writeBuffer) throws IOException {

    }
}

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
		SquadManager.squadIDCounter++;
		SquadManager.addSquadData(this);
	}

	public SquadData(PacketReadBuffer readBuffer) throws IOException {
		deserialize(readBuffer);
	}

	@Override
	public void deserialize(PacketReadBuffer readBuffer) throws IOException {
		squadID = readBuffer.readInt();
		int size = readBuffer.readInt();
		squadMembers = new ArrayList<>();
		for(int i = 0; i < size; i++) squadMembers.add(new SquadMemberData(readBuffer));
	}

	@Override
	public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
		writeBuffer.writeInt(squadID);
		writeBuffer.writeInt(squadMembers.size());
		for(SquadMemberData memberData : squadMembers) memberData.serialize(writeBuffer);
	}

	@Override
	public void updateClients() {
	}

	public boolean ready() {
		for(SquadMemberData memberData : squadMembers) if(!memberData.ready) return false;
		return true;
	}

	public boolean isPlayerInSquad(String playerName) {
		for(SquadMemberData memberData : squadMembers) if(memberData.playerName.equals(playerName)) return true;
		return false;
	}
}

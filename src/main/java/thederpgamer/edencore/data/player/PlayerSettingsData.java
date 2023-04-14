package thederpgamer.edencore.data.player;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import thederpgamer.edencore.data.SerializableData;
import thederpgamer.edencore.data.event.EventData;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Contains personal player settings.
 *
 * @author TheDerpGamer (MrGoose#0027)
 */
public class PlayerSettingsData implements SerializableData {
	private final ArrayList<Integer> subscribedEvents = new ArrayList<>();
	private String playerName;

	public PlayerSettingsData(PlayerData playerData) {
		this.playerName = playerData.playerName;
		setDefaults();
	}

	private void setDefaults() {
		subscribedEvents.add(EventData.DAILY | EventData.PVE);
		subscribedEvents.add(EventData.DAILY | EventData.PVP);
		subscribedEvents.add(EventData.WEEKLY | EventData.PVE);
		subscribedEvents.add(EventData.WEEKLY | EventData.PVP);
		subscribedEvents.add(EventData.MONTHLY | EventData.PVE);
		subscribedEvents.add(EventData.MONTHLY | EventData.PVP);
	}

	public PlayerSettingsData(PacketReadBuffer readBuffer) throws Exception {
		deserialize(readBuffer);
	}

	@Override
	public void deserialize(PacketReadBuffer readBuffer) throws IOException {
		subscribedEvents.clear();
		subscribedEvents.addAll(readBuffer.readIntList());
	}

	@Override
	public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
		writeBuffer.writeIntList(subscribedEvents);
	}

	@Override
	public void updateClients() {
	}

	public String getPlayerName() {
		return playerName;
	}

	public ArrayList<Integer> getSubscribedEvents() {
		return subscribedEvents;
	}
}

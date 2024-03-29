package thederpgamer.edencore.data.event;

import api.common.GameCommon;
import api.common.GameServer;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.network.packets.PacketUtil;
import api.utils.game.PlayerUtils;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.SerializableData;
import thederpgamer.edencore.data.event.types.defense.DefenseEvent;
import thederpgamer.edencore.network.server.event.ServerSendEventDataPacket;

import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/21/2021]
 */
public abstract class EventData implements EventUpdater, SerializableData, Serializable {
	public static final int NONE = 0;
	public static final int WAITING = 1;
	public static final int ACTIVE = 2;
	public static final int FINISHED = 3;
	public static final int PVE = 0;
	public static final int PVP = 1;
	public static final int DAILY = 2;
	public static final int WEEKLY = 4;
	public static final int MONTHLY = 8;
	public int code;
	public long waitingTime;
	protected long id;
	protected String name;
	protected String description;
	protected EventType eventType;
	protected EventTarget[] targets;
	protected SquadData squadData;
	protected int difficulty;
	protected int maxPlayers;
	protected int status;
	public EventData(String name, String description, EventType eventType, EventTarget... targets) {
		this.id = System.currentTimeMillis();
		this.name = name;
		this.description = description;
		this.eventType = eventType;
		this.targets = targets;
		this.maxPlayers = 4;
		this.waitingTime = 30000;
	}

	public EventData(PacketReadBuffer readBuffer) throws IOException {
		deserialize(readBuffer);
	}

	public abstract void deserialize(PacketReadBuffer readBuffer) throws IOException;

	public abstract void serialize(PacketWriteBuffer writeBuffer) throws IOException;

	@Override
	public void updateClients() {
		assert GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer();
		for(PlayerState playerState : GameServer.getServerState().getPlayerStatesByName().values()) PacketUtil.sendPacket(playerState, new ServerSendEventDataPacket(this));
	}

	public static EventData fromPacket(PacketReadBuffer readBuffer) {
		try {
			EventType type = EventType.values()[readBuffer.readInt()];
			//case CAPTURE: return new CaptureEvent(readBuffer);
			if(type == EventType.DEFENSE) {
				return new DefenseEvent(readBuffer);
				//case DESTROY: return new DestroyEvent(readBuffer);
				//case ESCORT: return new EscortEvent(readBuffer);
				//case PURSUIT: return new PursuitEvent(readBuffer);
			}
			throw new IllegalStateException("Invalid event type: " + type.name());
		} catch(Exception exception) {
			EdenCore.getInstance().logException("Failed to deserialize EventData", exception);
		}
		return null;
	}

	public static EventData createRandom(int code) {
		EventType type = EventType.values()[new Random().nextInt(EventType.values().length - 1) + 1];
		switch(type) {
			//case CAPTURE: return new CaptureEvent(code);
			//case DEFENSE: return DefenseEvent.getRandom();
			//case DESTROY: return new DestroyEvent(code);
			//case ESCORT: return new EscortEvent(code);
			//case PURSUIT: return new PursuitEvent(code);
			//default: throw new IllegalStateException("Invalid event type: " + type.name());
		}
		return DefenseEvent.getRandom(); //Temp
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof EventData) {
			EventData eventData = (EventData) object;
			return eventData.id == id;
		}
		return false;
	}

	public long getId() {
		return id;
	}

	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public EventType getEventType() {
		return eventType;
	}

	public EventTarget[] getTargets() {
		return targets;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public SquadData getSquadData() {
		return squadData;
	}

	public int getStatus() {
		return status;
	}

	public String getStatusDisplay() {
		switch(status) {
			case WAITING:
				return "WAITING";
			case ACTIVE:
				return "ACTIVE";
			case FINISHED:
				return "FINISHED";
			default:
				return "ERROR";
		}
	}

	public int getCurrentPlayers() {
		return squadData.squadMembers.size();
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void joinEvent(PlayerState playerState) {
		if(status == WAITING) {
			if(squadData.squadMembers.size() < maxPlayers) {
				squadData.squadMembers.add(new SquadMemberData(playerState));
				EdenCore.getInstance().logInfo("Player " + playerState.getName() + " joined event " + name + ".");
				PlayerUtils.sendMessage(playerState, "Joined event: " + name);
			} else {
				EdenCore.getInstance().logInfo("Event " + name + " is full!");
				PlayerUtils.sendMessage(playerState, "Cannot join event " + name + " - Event is full!");
			}
		} else {
			EdenCore.getInstance().logInfo("Event " + name + " is already started!");
			PlayerUtils.sendMessage(playerState, "Cannot join event " + name + " - Event has already started!");
		}
		updateClients();
	}

	public void leaveEvent(PlayerState playerState) {
		if(status == WAITING) {
			for(SquadMemberData squadMemberData : squadData.squadMembers) {
				if(squadMemberData.playerName.equals(playerState.getName())) {
					squadData.squadMembers.remove(squadMemberData);
					EdenCore.getInstance().logInfo("Player " + playerState.getName() + " left event " + name + ".");
					PlayerUtils.sendMessage(playerState, "Left event: " + name);
					break;
				}
			}
		} else {
			EdenCore.getInstance().logInfo("Event " + name + " is already started!");
			PlayerUtils.sendMessage(playerState, "Cannot leave event " + name + " - Event has already started!");
		}
	}

	public SquadMemberData getSquadMember(PlayerState playerState) {
		for(SquadMemberData squadMemberData : squadData.squadMembers) {
			if(squadMemberData.playerName.equals(playerState.getName())) return squadMemberData;
		}
		return null;
	}

	public abstract void initializeEvent();

	public abstract String getAnnouncement();

	public enum EventType {ALL, CAPTURE, DEFENSE, DESTROY, ESCORT, PURSUIT}
}

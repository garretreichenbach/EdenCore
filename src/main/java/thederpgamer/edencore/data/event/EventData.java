package thederpgamer.edencore.data.event;

import api.common.GameCommon;
import api.common.GameServer;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.network.packets.PacketUtil;
import api.utils.game.PlayerUtils;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.data.SerializableData;
import thederpgamer.edencore.data.event.types.defense.DefenseEvent;
import thederpgamer.edencore.manager.LogManager;
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

	public enum EventType {ALL, CAPTURE, DEFENSE, DESTROY, ESCORT, PURSUIT}

	protected long id;
	public int code;
	protected String name;
	protected String description;
	protected EventType eventType;
	protected EventTarget[] targets = new EventTarget[0];
	protected SquadData squadData = new SquadData();
	protected int difficulty;
	protected int maxPlayers;
	protected int status;
	public long waitingTime;

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

	@Override
	public boolean equals(Object object) {
		if(object instanceof EventData) {
			EventData eventData = (EventData) object;
			return eventData.id == id;
		}
		return false;
	}

	@Override
	public void updateClients() {
		assert GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer();
		for(PlayerState playerState : GameServer.getServerState().getPlayerStatesByName().values()) PacketUtil.sendPacket(playerState, new ServerSendEventDataPacket(this));
	}

	public long getId() {
		return id;
	}

	public int getCode() {
		return code;
	}

	public boolean isPvp() {
		return (code & PVP) == PVP;
	}

	public void setPvp(boolean pvp) {
		if(pvp) code |= PVP;
		else code &= ~PVP;
	}

	public boolean isPve() {
		return (code & PVE) == PVE;
	}

	public void setPve(boolean pve) {
		if(pve) code |= PVE;
		else code &= ~PVE;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public EventType getEventType() {
		return eventType;
	}

	public EventTarget[] getTargets() {
		return targets;
	}

	public void addTarget(EventTarget target) {
		EventTarget[] newTargets = new EventTarget[targets.length + 1];
		System.arraycopy(targets, 0, newTargets, 0, targets.length);
		newTargets[targets.length] = target;
		targets = newTargets;
	}

	public void removeTarget(EventTarget target) {
		EventTarget[] newTargets = new EventTarget[targets.length - 1];
		int index = 0;
		for(EventTarget eventTarget : targets) {
			if(eventTarget != target) {
				newTargets[index] = eventTarget;
				index++;
			}
		}
		targets = newTargets;
	}

	public String[] getTargetsStrings() {
		String[] targets = new String[this.targets.length];
		for(int i = 0; i < this.targets.length; i ++) targets[i] = this.targets[i].name;
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
				LogManager.logInfo("Player " + playerState.getName() + " joined event " + name + "!");
				PlayerUtils.sendMessage(playerState, "Joined event: " + name);
			} else {
				LogManager.logInfo("Event " + name + " is full!");
				PlayerUtils.sendMessage(playerState, "Cannot join event " + name + " - Event is full!");
			}
		} else {
			LogManager.logInfo("Event " + name + " is already started!");
			PlayerUtils.sendMessage(playerState, "Cannot join event " + name + " - Event has already started!");
		}
		updateClients();
	}

	public void leaveEvent(PlayerState playerState) {
		if(status == WAITING) {
			for(SquadMemberData squadMemberData : squadData.squadMembers) {
				if(squadMemberData.playerName.equals(playerState.getName())) {
					squadData.squadMembers.remove(squadMemberData);
					LogManager.logInfo("Player " + playerState.getName() + " left event " + name + "!");
					PlayerUtils.sendMessage(playerState, "Left event: " + name);
					break;
				}
			}
		} else {
			LogManager.logInfo("Event " + name + " is already started!");
			PlayerUtils.sendMessage(playerState, "Cannot leave event " + name + " - Event has already started!");
		}
	}

	public SquadMemberData getSquadMember(PlayerState playerState) {
		for(SquadMemberData squadMemberData : squadData.squadMembers) {
			if(squadMemberData.playerName.equals(playerState.getName())) return squadMemberData;
		}
		return null;
	}

	public abstract void deserialize(PacketReadBuffer readBuffer) throws IOException;
	public abstract void serialize(PacketWriteBuffer writeBuffer) throws IOException;
	public abstract void initializeEvent();

	public abstract String getAnnouncement();

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
			LogManager.logException("Failed to deserialize EventData", exception);
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
}

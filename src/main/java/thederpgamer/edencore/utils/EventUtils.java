package thederpgamer.edencore.utils;

import api.common.GameCommon;
import api.common.GameServer;
import api.network.packets.PacketUtil;
import api.utils.game.PlayerUtils;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.faction.Faction;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.data.event.SquadData;
import thederpgamer.edencore.data.event.SquadMemberData;
import thederpgamer.edencore.data.player.PlayerData;
import thederpgamer.edencore.manager.ClientCacheManager;
import thederpgamer.edencore.manager.EventManager;
import thederpgamer.edencore.network.client.event.ClientModifyEventPacket;

import java.util.ArrayList;
import java.util.Random;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class EventUtils {

	public static boolean isPlayerInEvent(PlayerState player, EventData eventData) {
		for(SquadMemberData memberData : eventData.getSquadData().squadMembers) return player.getName().equals(memberData.playerName);
		return false;
	}

	public static boolean isPlayerInEventOther(PlayerState playerState, EventData eventData) {
		//If the player isn't in an event or is in an event that is not the provided event, return false
		return getCurrentEvent(playerState) != null && getCurrentEvent(playerState).equals(eventData);
	}

	public static EventData getCurrentEvent(PlayerState playerState) {
		ArrayList<EventData> allEvents = new ArrayList<>();
		if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) allEvents.addAll(EventManager.getAllEvents());
		else allEvents.addAll(ClientCacheManager.eventData);
		for(EventData eventData : allEvents) {
			for(SquadMemberData memberData : eventData.getSquadData().squadMembers) {
				if(memberData.playerName.equals(playerState.getName())) return eventData;
			}
		}
		return null;
	}

	public static void addPlayerToEvent(PlayerState playerState, EventData eventData) {
		if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) { //On server, just modify directly and send updateClients to clients
			eventData.joinEvent(playerState);
			eventData.updateClients();
		} else { //On client, send packet to server
			ClientModifyEventPacket packet = new ClientModifyEventPacket(eventData, ClientModifyEventPacket.JOIN_EVENT);
			PacketUtil.sendPacketToServer(packet);
		}
	}

	public static void removePlayerFromEvent(PlayerState playerState, EventData eventData) {
		if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) { //On server, just modify directly and send updateClients to clients
			eventData.leaveEvent(playerState);
			eventData.updateClients();
		} else { //On client, send packet to server
			ClientModifyEventPacket packet = new ClientModifyEventPacket(eventData, ClientModifyEventPacket.LEAVE_EVENT);
			PacketUtil.sendPacketToServer(packet);
		}
	}

	public static Vector3i getRandomSector() {
		Vector3i sector = new Vector3i();
		sector.x = new Random().nextInt(10000) - 5000;
		sector.y = new Random().nextInt(10000) - 5000;
		sector.z = new Random().nextInt(10000) - 5000;
		return sector;
	}

	public static void logEventMessage(EventData eventData, String message) {
		for(SquadMemberData memberData : eventData.getSquadData().squadMembers) {
			PlayerState playerState = GameServer.getServerState().getPlayerFromNameIgnoreCaseWOException(memberData.playerName);
			if(playerState != null) PlayerUtils.sendMessage(playerState, "[Event] " + message);
		}
		cancelEvent(eventData);
	}

	public static void cancelEvent(EventData eventData) {
		//Todo: Cancel event
	}

	public static Faction getBlueTeam() {
		for(Faction faction : GameCommon.getGameState().getFactionManager().getFactionCollection()) {
			if(faction.getName().equals("Blue Team")) return faction;
		}
		return null;
	}

	public static Faction getRedTeam() {
		for(Faction faction : GameCommon.getGameState().getFactionManager().getFactionCollection()) {
			if(faction.getName().equals("Red Team")) return faction;
		}
		return null;
	}

	public static void setTeam(SquadData squadData, Faction team) {
		for(SquadMemberData memberData : squadData.squadMembers) {
			PlayerState playerState = GameServer.getServerState().getPlayerFromNameIgnoreCaseWOException(memberData.playerName);
			if(playerState != null) {
				playerState.getFactionController().setFactionId(team.getIdFaction());
				PlayerUtils.sendMessage(playerState, "[Event] You have been assigned to" + team.getName());
			}
		}
	}

	/**
	 * Sets all members of the squad back to their actual faction.
	 * @param squadData Squad to reset.
	 */
	public static void resetTeamFaction(SquadData squadData) {
		for(SquadMemberData memberData : squadData.squadMembers) {
			PlayerState playerState = GameServer.getServerState().getPlayerFromNameIgnoreCaseWOException(memberData.playerName);
			if(playerState != null) {
				playerState.getFactionController().setFactionId(memberData.factionId);
				//Todo: Figure out how to set the player's faction rank back to what it was before the event
				PlayerUtils.sendMessage(playerState, "[Event] You have been reset to your original faction.");
			}
		}
	}

	public static void resetPlayerFaction(PlayerState playerState, PlayerData playerData) {
		playerState.getFactionController().setFactionId(playerData.factionId);
		//Todo: Figure out how to set the player's faction rank back to what it was before the event
		PlayerUtils.sendMessage(playerState, "[Event] You have been reset to your original faction.");
	}
}

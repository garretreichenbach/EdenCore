package thederpgamer.edencore.utils;

import api.common.GameCommon;
import api.network.packets.PacketUtil;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.data.event.SquadMemberData;
import thederpgamer.edencore.manager.ClientCacheManager;
import thederpgamer.edencore.manager.EventManager;
import thederpgamer.edencore.network.old.client.event.ClientModifyEventPacket;

import java.util.ArrayList;

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
}

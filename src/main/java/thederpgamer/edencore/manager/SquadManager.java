package thederpgamer.edencore.manager;

import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.data.event.SquadData;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/21/2021]
 */
public class SquadManager {
	private static final ConcurrentHashMap<Integer, SquadData> squadDataMap = new ConcurrentHashMap<>();
	public static int squadIDCounter = 0;

	public static SquadData getPlayerSquadData(PlayerState playerState) {
		for(SquadData squadData : squadDataMap.values()) {
			if(squadData.isPlayerInSquad(playerState.getName())) return squadData;
		}
		return null;
	}

	public static void addSquadData(SquadData squadData) {
		squadDataMap.put(squadData.squadID, squadData);
	}
}

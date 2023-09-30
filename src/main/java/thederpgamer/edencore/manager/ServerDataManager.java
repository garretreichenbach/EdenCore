package thederpgamer.edencore.manager;

import api.mod.config.PersistentObjectUtil;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.other.WaypointMarker;

import java.util.ArrayList;
import java.util.List;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class ServerDataManager {
	public static List<WaypointMarker> getWaypointMarkers(PlayerState playerState) {
		List<WaypointMarker> waypointMarkers = new ArrayList<>();
		for(Object obj : PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), WaypointMarker.class)) {
			WaypointMarker waypointMarker = (WaypointMarker) obj;
			if(waypointMarker.canView(playerState)) waypointMarkers.add(waypointMarker);
		}
		return waypointMarkers;
	}
}

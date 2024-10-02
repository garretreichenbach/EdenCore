package thederpgamer.edencore.drawer;

import api.network.packets.PacketUtil;
import libpackage.drawer.MapDrawer;
import libpackage.markers.ClickableMapMarker;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.other.WaypointMarker;
import thederpgamer.edencore.manager.ResourceManager;
import thederpgamer.edencore.network.old.client.misc.NavigationUpdateRequestPacket;

import java.util.HashMap;
import java.util.List;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class EdenMapDrawer extends MapDrawer {
	private static EdenMapDrawer instance;

	public static EdenMapDrawer getInstance() {
		return instance;
	}

	private final HashMap<WaypointMarker, WaypointMarkerDrawData> waypointMarkers = new HashMap<>();

	public EdenMapDrawer() {
		super(EdenCore.getInstance());
		instance = this;
		PacketUtil.sendPacketToServer(new NavigationUpdateRequestPacket());
	}

	public void addMarkers(List<WaypointMarker> markers) {
//		clearMarkers();
//		for(WaypointMarker waypointMarker : markers) {
//			if(!waypointMarkers.containsKey(waypointMarker)) {
//				waypointMarkers.put(waypointMarker, new WaypointMarkerDrawData(getMarker(waypointMarker), waypointMarker.getName()));
//				addMarker(waypointMarkers.get(waypointMarker).marker);
//				drawText(waypointMarker.getSector(), waypointMarker.getName());
//			}
//		}
	}

	private ClickableMapMarker getMarker(WaypointMarker markerData) {
		return new ClickableMapMarker(ResourceManager.getSprite("map-sprites"), markerData.getType().ordinal(), markerData.getColor(), markerData.getSector().toVector3f());
	}
}

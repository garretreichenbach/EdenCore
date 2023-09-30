package thederpgamer.edencore.drawer;

import libpackage.markers.ClickableMapMarker;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class WaypointMarkerDrawData {

	public ClickableMapMarker marker;
	public String text;

	public WaypointMarkerDrawData(ClickableMapMarker marker, String text) {
		this.marker = marker;
		this.text = text;
	}
}

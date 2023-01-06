package thederpgamer.edencore.navigation;

import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.view.gamemap.GameMapDrawer;

import javax.vecmath.Vector4f;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 26.09.2021
 * TIME: 14:54
 */
public class GateMarker extends MapMarker implements LineDrawer {
    public static Vector4f publicGateColor = new Vector4f(0,0.667f,1,1);
    public static Vector4f publicFTLColor = new Vector4f(1,0.333f,0,1);

    private final HashSet<Vector3i> connectionTargetSectors = new HashSet<>();

    /**
     * make a new mapmarker for a warpgate.
     *
     * @param name name displayed on mouseover
     * @param icon   icon to show on map, enum
     * @param color
     */
    public GateMarker(Vector3i sector, String name, MapIcon icon, Vector4f color) {
        super(sector, name, icon, color);
    }

    @Override
    public void addToDrawList(boolean isPublic) {
        super.addToDrawList(isPublic);
    }

    @Override
    public void removeFromDrawList() {
        super.removeFromDrawList();
    }

    @Override
    public void drawLines(GameMapDrawer gameMapDrawer) {
        for (Vector3i to: connectionTargetSectors) {
            EdenMapDrawer.instance.drawLinesSector(sector,to,new Vector4f(publicFTLColor),new Vector4f(publicFTLColor));
        }
    }

    public void addLine(Vector3i line) {
        connectionTargetSectors.add(line);
    }

    public HashSet<Vector3i> getConnectionTargetSectors() {
        return connectionTargetSectors;
    }

    public void removeLine(SectorConnection line) {
        connectionTargetSectors.remove(line);
    }
}

package thederpgamer.edencore.navigation;

import api.listener.fastevents.GameMapDrawListener;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.view.gamemap.GameMapDrawer;

import javax.vecmath.Vector4f;
import java.util.ArrayList;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 26.09.2021
 * TIME: 14:54
 */
public class GateMarker extends MapMarker implements LineDrawer {
    private ArrayList<SectorConnection> lines = new ArrayList<>();

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
        for (SectorConnection line: lines) {
            EdenMapDrawer.instance.drawLinesSector(line.getStart(),line.getEnd(),line.getStartColor(),line.getEndColor());
        }
    }

    public void addLine(SectorConnection line) {
        lines.add(line);
    }

    public void removeLine(SectorConnection line) {
        lines.remove(line);
    }
}

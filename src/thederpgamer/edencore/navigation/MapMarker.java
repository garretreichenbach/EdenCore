package thederpgamer.edencore.navigation;

import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.data.gamemap.entry.SelectableMapEntry;
import org.schema.game.client.view.camera.GameMapCamera;
import org.schema.game.client.view.gamemap.GameMapDrawer;
import org.schema.schine.graphicsengine.forms.PositionableSubColorSprite;
import org.schema.schine.graphicsengine.forms.SelectableSprite;
import org.schema.schine.graphicsengine.forms.Sprite;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 25.09.2021
 * TIME: 11:31
 * container class that is basically:
 * name, position and icon to be drawn on map
 */
public class MapMarker implements PositionableSubColorSprite, SelectableSprite, SelectableMapEntry {
    /**
     * make a new mapmarker.
     * @param icon icon to show on map, enum
     */
    public MapMarker(Vector3i sector, String name, MapIcon icon, Vector4f color) {
        this.color = color;
        this.sector = sector;
        this.name = name;
        this.icon = icon;
        this.pos = EdenMapDrawer.posFromSector(sector,true);
    }

    MapIcon icon;
    Vector3i sector;
    String name;
    Vector4f color;
    Vector3f pos;

    transient private boolean selected;
    transient private boolean drawIndication;
    private float scale = 0.3f;
    public float scaleFactor = 1;

    /**
     * code that gets called before the marker is drawn.
     */
    public void preDraw(GameMapDrawer drawer) {
        autoScale(drawer.getCamera());
        if (selected)
            EdenMapDrawer.instance.drawText(pos,name);
    }

    public void addToDrawList(boolean isPublic) {
        EdenMapDrawer.instance.addMarker(this,isPublic);
    }

    public void removeFromDrawList() {
        //TODO remove method in drawer
        //EdenMapDrawer.instance.
    }

    private void autoScale(GameMapCamera camera) {
        Vector3f distanceToCam = new Vector3f(camera.getPos());
        distanceToCam.sub(pos);
        float dist = distanceToCam.length();
        scaleFactor = Math.min(10,Math.max(1,dist/300));
    }

    public Sprite getSprite() {
        if (icon == null)
            return null;
        return icon.getSprite();
    }

    @Override
    public Vector4f getColor() {
        return color;
    }

    @Override
    public float getScale(long l) {
        return scale * scaleFactor * (selected?2:1);
    }

    @Override
    public int getSubSprite(Sprite sprite) {
        return icon.subSpriteIndex;
    }

    @Override
    public boolean canDraw() {
        return true;
    }

    @Override
    public Vector3f getPos() {
        return pos;
    }

    @Override
    public boolean isDrawIndication() {
        return drawIndication;
    } //??

    @Override
    public void setDrawIndication(boolean b) {

    }//??

    public MapIcon getIcon() {
        return icon;
    }

    public void setIcon(MapIcon icon) {
        this.icon = icon;
    }

    public Vector3i getSector() {
        return sector;
    }

    public void setSector(Vector3i sector) {
        this.sector = sector;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(Vector4f color) {
        this.color = color;
    }

    public void setPos(Vector3f pos) {
        this.pos = pos;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    @Override
    public float getSelectionDepth() {
        return 0;
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public void onSelect(float v) {
        selected = true;
        if (EdenMapDrawer.instance != null)
            EdenMapDrawer.instance.setSelected(this);
    }

    @Override
    public void onUnSelect() {
        selected = false;
        EdenMapDrawer.instance.unSelect(this);
    }

    @Override
    public String toString() {
        return "MapMarker{" +
                "icon=" + icon +
                ", sector=" + sector +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", pos=" + pos +
                ", scale=" + scale +
                ", scaleFactor=" + scaleFactor +
                ", drawIndication=" + drawIndication +
                '}';
    }

    public boolean getSelected() {
        return selected;
    }
}

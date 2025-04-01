package thederpgamer.edencore.gui.elements;

import org.schema.game.client.view.gui.advanced.tools.AdvResult;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.schine.graphicsengine.forms.gui.GUIOverlay;

import javax.vecmath.Vector4f;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public abstract class MetaItemDisplayResult extends AdvResult<MetaItemSelectCallback> {

	protected MetaItemDisplayResult() {
	}

	public abstract short getCurrentValue();

	protected void initDefault() {
	}

	public String getName() {
		return isBlockInit() ? "-" : getInfo().getName();
	}

	public int getCurrentBuildIconNum() {
		return isBlockInit() ? getInfo().getBuildIconNum() : 0;
	}

	public abstract short getDefault();

	public ElementInformation getInfo() {
		return ElementKeyMap.getInfoFast(getCurrentValue());
	}

	public boolean isBlockInit() {
		return ElementKeyMap.isInit() && ElementKeyMap.exists(getCurrentValue());
	}

	public Vector4f getBackgroundColor() {
		return HALF_TRANS;
	}

	public float getIconScale() {
		return 1.0F;
	}

	public void beforeBlockDraw(GUIOverlay guiOverlay) {
	}

	public void afterBlockDraw(GUIOverlay guiOverlay) {
	}

	public void afterInit(GUIOverlay blockOverlay) {
	}
}

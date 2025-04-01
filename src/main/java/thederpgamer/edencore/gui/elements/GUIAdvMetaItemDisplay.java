package thederpgamer.edencore.gui.elements;

import org.schema.game.client.view.gui.GUIBlockSprite;
import org.schema.game.client.view.gui.advanced.tools.BlockDisplayResult;
import org.schema.game.client.view.gui.advanced.tools.GUIAdvBlockDisplay;
import org.schema.game.client.view.gui.advanced.tools.GUIAdvTool;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.input.InputState;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class GUIAdvMetaItemDisplay extends GUIAdvTool<MetaItemDisplayResult> {

	private final GUIBlockSprite blockOverlay;
	private short currentValue;

	public GUIAdvMetaItemDisplay(InputState state, GUIElement dependent, final MetaItemDisplayResult res) {
		super(state, dependent, res);
		currentValue = res.getCurrentValue();
		blockOverlay = new GUIBlockSprite(getState(), res.getCurrentValue()) {
			@Override
			public void draw() {
				if(res.getCurrentValue() != currentValue) {
					type = res.getCurrentValue(); // Update the type to the new value
					currentValue = res.getCurrentValue(); // Store the new value
				}
				setScale(getIconScale(), getIconScale(), 0.0F);
				getSprite().setSelectedMultiSprite(currentValue);
				getRes().beforeBlockDraw(this);
				getSprite().draw();
				getRes().afterBlockDraw(this);
			}
		};
		blockOverlay.setLayer(-1);
		attach(blockOverlay);
		getRes().afterInit(blockOverlay);
	}

	@Override
	public int getElementHeight() {
		return (int) (blockOverlay.getHeight() * getIconScale());
	}

	protected float getIconScale() {
		return getRes().getIconScale();
	}
}

package thederpgamer.edencore.gui.elements;

import org.newdawn.slick.UnicodeFont;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.input.InputState;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/21/2022]
 */
public class GUITextArea extends GUIElementList {

	private GUIAncor dependent;

	public GUITextArea(InputState state) {
		super(state);
	}

	public GUITextArea(InputState state, GUIAncor dependent) {
		super(state);
		this.dependent = dependent;
		this.dependent.attach(this);
	}

	public void addText(String text, UnicodeFont font) {
		addText(size(), text, font);
	}

	public void addText(int pos, String text, UnicodeFont font) {
		GUITextOverlay textOverlay = new GUITextOverlay(10, 10, getState());
		textOverlay.setTextSimple(text);
		textOverlay.setFont(font);
		addText(pos, textOverlay);
	}

	public void addText(GUITextOverlay textOverlay) {
		addText(size(), textOverlay);
	}

	public void addText(int pos, GUITextOverlay textOverlay) {
		if(dependent != null) textOverlay.autoWrapOn = dependent;
		textOverlay.onInit();
		add(pos, new GUIListElement(textOverlay, getState()));
		updateDim();
	}
}

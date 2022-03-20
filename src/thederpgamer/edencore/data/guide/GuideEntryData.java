package thederpgamer.edencore.data.guide;

import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/19/2022]
 */
public abstract class GuideEntryData {

	public String name;
	public GuideEntryCategory category;

	public GuideEntryData(String name, GuideEntryCategory category) {
		this.name = name;
		this.category = category;
	}

	public abstract void createEntryPane(GUIContentPane contentPane);
}

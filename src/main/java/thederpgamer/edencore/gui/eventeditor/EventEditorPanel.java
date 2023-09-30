package thederpgamer.edencore.gui.eventeditor;

import thederpgamer.edencore.data.event.EventData;

import javax.swing.*;

/**
 * Panel for editing a specific event.
 *
 * @author TheDerpGamer
 */
public class EventEditorPanel extends JPanel {

	private final EventData eventData;

	public EventEditorPanel(EventData eventData) {
		this.eventData = eventData;
	}

	public EventData getEventData() {
		return eventData;
	}
}

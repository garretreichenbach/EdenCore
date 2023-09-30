package thederpgamer.edencore.gui.eventeditor;

import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.manager.EventManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;

/**
 * Java swing GUI for editing events.
 *
 * @author TheDerpGamer
 */
public class EventEditor extends JFrame {

	public static void open(PlayerState sender) {
		open(null, sender);
	}

	public static void open(String eventName, PlayerState sender) {
		EventData eventData = null;
		if(eventName != null) eventData = EventManager.getEventByName(eventName);
		EventEditor editor = new EventEditor();
		editor.setVisible(true);
		if(eventData != null) editor.loadEvent(eventData);
	}

	private EventEditorPanel editorPanel;
	private final JList<String> eventList = new JList<>();

	public EventEditor() {
		setTitle("Event Editor");
		setSize(1000, 800);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		JPanel eventListPanel = new JPanel();
		eventListPanel.setLayout(new BoxLayout(eventListPanel, BoxLayout.Y_AXIS));
		JScrollPane eventListScrollPane = new JScrollPane(eventList);
		eventListScrollPane.setPreferredSize(new Dimension(200, 0));
		eventListPanel.add(eventListScrollPane);
		add(eventListPanel, BorderLayout.WEST);
		eventList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				if(evt.getClickCount() == 2) {
					int index = eventList.locationToIndex(evt.getPoint());
					EventData eventData = EventManager.getAllEvents().get(index);
					loadEvent(eventData);
				}
			}
		});
		refreshEvents();
	}

	public void loadEvent(EventData eventData) {
		if(editorPanel != null) remove(editorPanel);
		editorPanel = new EventEditorPanel(eventData);
		add(editorPanel, BorderLayout.CENTER);
	}

	public void refreshEvents() {
		eventList.removeAll();
		final ArrayList<EventData> allEvents = EventManager.getAllEvents();
		eventList.setModel(new AbstractListModel<String>() {
			@Override
			public int getSize() {
				return allEvents.size();
			}

			@Override
			public String getElementAt(int index) {
				return allEvents.get(index).getName();
			}
		});
	}
}

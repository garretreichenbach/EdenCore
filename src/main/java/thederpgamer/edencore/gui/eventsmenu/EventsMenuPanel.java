package thederpgamer.edencore.gui.eventsmenu;

import api.network.packets.PacketUtil;
import api.utils.gui.GUIMenuPanel;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUITabbedContent;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.network.old.client.misc.RequestClientCacheUpdatePacket;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/08/2021]
 */
public class EventsMenuPanel extends GUIMenuPanel {
	private GUITabbedContent pveTabbedContent;
	private GUITabbedContent pvpTabbedContent;

	public EventsMenuPanel(InputState inputState) {
		super(inputState, "EventsMenu", 800, 500);
	}

	@Override
	public void recreateTabs() {
		PacketUtil.sendPacketToServer(new RequestClientCacheUpdatePacket());
		int lastTab = guiWindow.getSelectedTab();
		if(guiWindow.getTabs().size() > 0) guiWindow.clearTabs();
		GUIContentPane pveTab = guiWindow.addTab("PVE");
		pveTab.setTextBoxHeightLast((int) (guiWindow.getInnerHeigth() / 1.5));
		createPVETab(pveTab);
		GUIContentPane pvpTab = guiWindow.addTab("PVP");
		pvpTab.setTextBoxHeightLast((int) (guiWindow.getInnerHeigth() / 1.5));
		createPVPTab(pvpTab);
		guiWindow.setSelectedTab(lastTab);
	}

	private void createPVETab(GUIContentPane contentPane) {
		int lastTab = 0;
		if(pveTabbedContent == null) (pveTabbedContent = new GUITabbedContent(getState(), contentPane.getContent(0))).onInit();
		else {
			lastTab = pveTabbedContent.getSelectedTab();
			pveTabbedContent.clearTabs();
		}
		{ //Daily Tab
			GUIContentPane subTab = pveTabbedContent.addTab("DAILY");
			subTab.setTextBoxHeightLast((int) (guiWindow.getInnerHeigth() / 1.5));
			subTab.orientateInsideFrame();
			EventsScrollableList eventsList = new EventsScrollableList(getState(), subTab.getTextboxes().get(0), this, EventData.PVE | EventData.DAILY);
			eventsList.onInit();
			subTab.getTextboxes().get(0).attach(eventsList);
		}
		{ //Weekly Tab
			GUIContentPane subTab = pveTabbedContent.addTab("WEEKLY");
			subTab.setTextBoxHeightLast((int) (guiWindow.getInnerHeigth() / 1.5));
			subTab.orientateInsideFrame();
			EventsScrollableList eventsList = new EventsScrollableList(getState(), subTab.getTextboxes().get(0), this, EventData.PVE | EventData.WEEKLY);
			eventsList.onInit();
			subTab.getTextboxes().get(0).attach(eventsList);
		}
		{ //Monthly Tab
			GUIContentPane subTab = pveTabbedContent.addTab("MONTHLY");
			subTab.setTextBoxHeightLast((int) (guiWindow.getInnerHeigth() / 1.5));
			subTab.orientateInsideFrame();
			EventsScrollableList eventsList = new EventsScrollableList(getState(), subTab.getTextboxes().get(0), this, EventData.PVE | EventData.MONTHLY);
			eventsList.onInit();
			subTab.getTextboxes().get(0).attach(eventsList);
		}
		pveTabbedContent.setSelectedTab(lastTab);
	}

	private void createPVPTab(GUIContentPane contentPane) {
		int lastTab = 0;
		if(pvpTabbedContent == null) (pvpTabbedContent = new GUITabbedContent(getState(), contentPane.getContent(0))).onInit();
		else {
			lastTab = pvpTabbedContent.getSelectedTab();
			pvpTabbedContent.clearTabs();
		}
		{ //Daily Tab
			GUIContentPane subTab = pvpTabbedContent.addTab("DAILY");
			subTab.setTextBoxHeightLast((int) (guiWindow.getInnerHeigth() / 1.5));
			subTab.orientateInsideFrame();
			EventsScrollableList eventsList = new EventsScrollableList(getState(), subTab.getTextboxes().get(0), this, EventData.PVP | EventData.DAILY);
			eventsList.onInit();
			subTab.getTextboxes().get(0).attach(eventsList);
		}
		{ //Weekly Tab
			GUIContentPane subTab = pvpTabbedContent.addTab("WEEKLY");
			subTab.setTextBoxHeightLast((int) (guiWindow.getInnerHeigth() / 1.5));
			subTab.orientateInsideFrame();
			EventsScrollableList eventsList = new EventsScrollableList(getState(), subTab.getTextboxes().get(0), this, EventData.PVP | EventData.WEEKLY);
			eventsList.onInit();
			subTab.getTextboxes().get(0).attach(eventsList);
		}
		{ //Monthly Tab
			GUIContentPane subTab = pvpTabbedContent.addTab("MONTHLY");
			subTab.setTextBoxHeightLast((int) (guiWindow.getInnerHeigth() / 1.5));
			subTab.orientateInsideFrame();
			EventsScrollableList eventsList = new EventsScrollableList(getState(), subTab.getTextboxes().get(0), this, EventData.PVP | EventData.MONTHLY);
			eventsList.onInit();
			subTab.getTextboxes().get(0).attach(eventsList);
		}
		pvpTabbedContent.setSelectedTab(lastTab);
	}
}

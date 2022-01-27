package thederpgamer.edencore.gui.eventsmenu;

import api.network.packets.PacketUtil;
import api.utils.gui.GUIMenuPanel;
import org.schema.schine.graphicsengine.core.GLFrame;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUITabbedContent;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.network.client.RequestClientCacheUpdatePacket;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/08/2021]
 */
public class EventsMenuPanel extends GUIMenuPanel {

  public static final int PVE = 0;
  public static final int PVP = 1;
  public static final int DAILY = 2;
  public static final int WEEKLY = 4;
  public static final int MONTHLY = 8;

  private GUITabbedContent pveTabbedContent;
  private GUITabbedContent pvpTabbedContent;

  public EventsMenuPanel(InputState inputState) {
    super(
        inputState,
        "EventsMenu",
        (int) (GLFrame.getWidth() / 1.5),
        (int) (GLFrame.getHeight() / 1.5));
  }

  @Override
  public void recreateTabs() {
    PacketUtil.sendPacketToServer(new RequestClientCacheUpdatePacket());
    int lastTab = guiWindow.getSelectedTab();
    if (guiWindow.getTabs().size() > 0) guiWindow.clearTabs();

    GUIContentPane pveTab = guiWindow.addTab("PVE");
    pveTab.setTextBoxHeightLast((int) (GLFrame.getHeight() / 1.5));
    createPVETab(pveTab);

    GUIContentPane pvpTab = guiWindow.addTab("PVP");
    pvpTab.setTextBoxHeightLast((int) (GLFrame.getHeight() / 1.5));
    createPVPTab(pvpTab);

    guiWindow.setSelectedTab(lastTab);
  }

  private void createPVETab(GUIContentPane contentPane) {
    int lastTab = 0;
    if (pveTabbedContent == null)
      (pveTabbedContent = new GUITabbedContent(getState(), contentPane.getContent(0))).onInit();
    else {
      lastTab = pveTabbedContent.getSelectedTab();
      pveTabbedContent.clearTabs();
    }

    { // Daily Tab
      GUIContentPane subTab = pveTabbedContent.addTab("DAILY");
      subTab.setTextBoxHeightLast((int) (GLFrame.getHeight() / 1.5));
      subTab.orientateInsideFrame();
      EventsScrollableList eventsList =
          new EventsScrollableList(getState(), subTab.getTextboxes().get(0), this, PVE | DAILY);
      eventsList.onInit();
      subTab.getTextboxes().get(0).attach(eventsList);
    }
  }

  private void createPVPTab(GUIContentPane contentPane) {}
}

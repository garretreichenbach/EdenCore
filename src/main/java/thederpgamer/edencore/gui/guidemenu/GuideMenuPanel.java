package thederpgamer.edencore.gui.guidemenu;

import api.utils.gui.GUIMenuPanel;
import org.schema.schine.graphicsengine.core.GLFrame;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.input.InputState;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/19/2022]
 */
public class GuideMenuPanel extends GUIMenuPanel {

  public GuideMenuPanel(InputState inputState) {
    super(
        inputState,
        "GuideMenu",
        (int) (GLFrame.getWidth() / 1.5),
        (int) (GLFrame.getHeight() / 1.5));
  }

  @Override
  public void recreateTabs() {
    int lastTab = guiWindow.getSelectedTab();
    if (guiWindow.getTabs().size() > 0) guiWindow.clearTabs();
    GUIContentPane guideTab = guiWindow.addTab("GUIDE");
    guideTab.setTextBoxHeightLast((int) (GLFrame.getHeight() / 1.5));
    createGuideTab(guideTab);
    guiWindow.setSelectedTab(lastTab);
  }

  private void createGuideTab(GUIContentPane guideTab) {
    (new GuideEntryScrollableList(getState(), guideTab.getContent(0), this)).onInit();
  }
}

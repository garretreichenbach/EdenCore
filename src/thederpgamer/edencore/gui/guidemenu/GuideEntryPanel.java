package thederpgamer.edencore.gui.guidemenu;

import api.utils.gui.GUIInputDialogPanel;
import org.schema.schine.graphicsengine.core.GLFrame;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.guide.GuideEntryData;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/19/2022]
 */
public class GuideEntryPanel extends GUIInputDialogPanel {

  public GuideEntryPanel(InputState inputState, GUICallback guiCallback) {
    super(
        inputState,
        "GuideEntryPanel",
        "Guide",
        "",
        (int) (GLFrame.getWidth() / 1.5),
        (int) (GLFrame.getHeight() / 1.5),
        guiCallback);
    setCancelButtonText("EXIT");
    setOkButton(false);
    getButtonCancel().setUserPointer("EXIT");
  }

  public void createPanel(GuideEntryData guideEntry) {
    GUIContentPane contentPane = ((GUIDialogWindow) background).getMainContentPane();
    contentPane.setTextBoxHeightLast((int) getHeight());
    guideEntry.createEntryPane(contentPane);
  }
}

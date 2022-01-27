package thederpgamer.edencore.gui.squadmenu;

import api.utils.gui.GUIInputDialogPanel;
import org.schema.schine.graphicsengine.core.GLFrame;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;
import org.schema.schine.input.InputState;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/21/2021]
 */
public class SquadInputPanel extends GUIInputDialogPanel {

  public SquadInputPanel(InputState inputState, GUICallback guiCallback) {
    super(
        inputState,
        "SquadInputPanel",
        "SQUAD",
        "",
        (int) (GLFrame.getWidth() / 1.5),
        (int) (GLFrame.getHeight() / 1.5),
        guiCallback);
  }

  @Override
  public void onInit() {
    super.onInit();
    GUIContentPane contentPane = ((GUIDialogWindow) background).getMainContentPane();
    contentPane.setTextBoxHeightLast((int) (GLFrame.getHeight() / 1.5));
  }
}

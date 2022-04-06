package thederpgamer.edencore.gui.guidemenu;

import api.utils.gui.GUIInputDialog;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/19/2022]
 */
public class GuideEntryDialog extends GUIInputDialog {

  @Override
  public GuideEntryPanel createPanel() {
    return new GuideEntryPanel(getState(), this);
  }

  @Override
  public void callback(GUIElement callingElement, MouseEvent mouseEvent) {
    if (!isOccluded() && mouseEvent.pressedLeftMouse()) {
      switch ((String) callingElement.getUserPointer()) {
        case "X":
        case "EXIT":
          deactivate();
          break;
      }
    }
  }

  @Override
  public GuideEntryPanel getInputPanel() {
    return (GuideEntryPanel) super.getInputPanel();
  }
}

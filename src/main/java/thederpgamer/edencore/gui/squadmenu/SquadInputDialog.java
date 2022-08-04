package thederpgamer.edencore.gui.squadmenu;

import api.utils.gui.GUIInputDialog;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/21/2021]
 */
public class SquadInputDialog extends GUIInputDialog {

    @Override
    public SquadInputPanel createPanel() {
        return new SquadInputPanel(getState(), this);
    }

    @Override
    public void callback(GUIElement callingElement, MouseEvent mouseEvent) {
        if(!isOccluded() && mouseEvent.pressedLeftMouse() && callingElement.getUserPointer() != null) {
            switch((String) callingElement.getUserPointer()) {
                case "X":
                case "CANCEL":
                    deactivate();
                    break;
                case "OK":
                    //Todo
                    deactivate();
                    break;
            }
        }
    }
}

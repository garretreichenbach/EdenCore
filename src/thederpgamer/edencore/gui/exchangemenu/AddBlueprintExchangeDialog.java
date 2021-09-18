package thederpgamer.edencore.gui.exchangemenu;

import api.mod.config.PersistentObjectUtil;
import api.utils.gui.GUIInputDialog;
import org.apache.commons.lang3.math.NumberUtils;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.exchange.BlueprintExchangeItem;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/17/2021]
 */
public class AddBlueprintExchangeDialog extends GUIInputDialog {

    @Override
    public AddBlueprintExchangePanel createPanel() {
        return new AddBlueprintExchangePanel(getState(), this);
    }

    @Override
    public void callback(GUIElement callingElement, MouseEvent mouseEvent) {
        if(!isOccluded() && mouseEvent.pressedLeftMouse()) {
            switch((String) callingElement.getUserPointer()) {
                case "X":
                case "CANCEL":
                    deactivate();
                    break;
                case "OK":
                    if(createItem() != null) {
                        deactivate();
                        EdenCore.getInstance().exchangeMenuControlManager.getMenuPanel().recreateTabs();
                    }
                    break;
            }
        }
    }

    @Override
    public AddBlueprintExchangePanel getInputPanel() {
        return (AddBlueprintExchangePanel) super.getInputPanel();
    }

    private BlueprintExchangeItem createItem() {
        if(NumberUtils.isNumber(getInputPanel().currentBarText) && getInputPanel().blueprintEntry != null && getInputPanel().barId != 0) {
            BlueprintExchangeItem item = new BlueprintExchangeItem(getInputPanel().blueprintEntry, getInputPanel().barId, Integer.parseInt(getInputPanel().currentBarText), "");
            PersistentObjectUtil.addObject(EdenCore.getInstance().getSkeleton(), item);
            PersistentObjectUtil.save(EdenCore.getInstance().getSkeleton());
            return item;
        } else return null;
    }
}

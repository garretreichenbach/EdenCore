package thederpgamer.edencore.gui.exchangemenu;

import api.network.packets.PacketUtil;
import api.utils.gui.GUIInputDialog;
import org.apache.commons.lang3.math.NumberUtils;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.exchange.ResourceExchangeItem;
import thederpgamer.edencore.network.client.ExchangeItemCreatePacket;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/18/2021]
 */
public class AddResourceExchangeDialog extends GUIInputDialog {

    @Override
    public AddResourceExchangePanel createPanel() {
        return new AddResourceExchangePanel(getState(), this);
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
    public AddResourceExchangePanel getInputPanel() {
        return (AddResourceExchangePanel) super.getInputPanel();
    }

    private ResourceExchangeItem createItem() {
        if(NumberUtils.isNumber(getInputPanel().currentBarText) && NumberUtils.isNumber(getInputPanel().currentItemAmountText) && getInputPanel().barId > 0 && getInputPanel().itemId > 0) {
            ElementInformation itemInfo = ElementKeyMap.getInfo(getInputPanel().itemId);
            if(itemInfo != null) {
                int amount = Math.abs(Integer.parseInt(getInputPanel().currentItemAmountText));
                ResourceExchangeItem item = new ResourceExchangeItem(getInputPanel().barId, Math.abs(Integer.parseInt(getInputPanel().currentBarText)), "x" + amount + " " + itemInfo.getName(), itemInfo.description, itemInfo.getId(), amount);
                PacketUtil.sendPacketToServer(new ExchangeItemCreatePacket(1, item));
                return item;
            }
        }
        return null;
    }
}

package thederpgamer.edencore.gui.exchangemenu;

import api.network.packets.PacketUtil;
import api.utils.gui.GUIInputDialog;
import org.apache.commons.lang3.math.NumberUtils;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.exchange.ItemExchangeItem;
import thederpgamer.edencore.data.exchange.ResourceExchangeItem;
import thederpgamer.edencore.network.client.ExchangeItemCreatePacket;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/24/2021]
 */
public class AddItemExchangeDialog extends GUIInputDialog {

    @Override
    public AddItemExchangePanel createPanel() {
        return new AddItemExchangePanel(getState(), this);
    }

    @Override
    public AddItemExchangePanel getInputPanel() {
        return (AddItemExchangePanel) super.getInputPanel();
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

    private ItemExchangeItem createItem() {
        if(NumberUtils.isNumber(getInputPanel().currentBarText) && getInputPanel().barId > 0 && getInputPanel().itemId > 0) {
            ElementInformation itemInfo = ElementKeyMap.getInfo(getInputPanel().itemId);
            if(itemInfo != null) {
                ItemExchangeItem item = new ItemExchangeItem(getInputPanel().barId, Math.abs(Integer.parseInt(getInputPanel().currentBarText)), "x1 " + itemInfo.getName(), itemInfo.description, itemInfo.getId(), );
                PacketUtil.sendPacketToServer(new ExchangeItemCreatePacket(1, item));
                return item;
            }
        }
        return null;
    }
}

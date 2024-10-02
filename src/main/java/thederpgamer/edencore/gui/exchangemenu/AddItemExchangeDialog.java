package thederpgamer.edencore.gui.exchangemenu;

import api.network.packets.PacketUtil;
import api.utils.gui.GUIInputDialog;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.schema.game.common.data.element.meta.MetaObjectManager;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.exchange.ItemExchangeItem;
import thederpgamer.edencore.network.old.client.exchange.ExchangeItemCreatePacket;

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
		if(NumberUtils.isNumber(getInputPanel().currentBarText) && getInputPanel().barId > 0) {
			ItemExchangeItem item = new ItemExchangeItem(getInputPanel().barId, Math.abs(Integer.parseInt(getInputPanel().currentBarText)), "x1 " + WordUtils.capitalize(getInputPanel().subType.name().toLowerCase().replace("_", " ") + " Weapon"), "", getInputPanel().itemId, MetaObjectManager.MetaObjectType.WEAPON.type, getInputPanel().subType.type);
			PacketUtil.sendPacketToServer(new ExchangeItemCreatePacket(2, item));
			return item;
		}
		return null;
	}
}

package thederpgamer.edencore.gui.exchangemenu;

import api.network.packets.PacketUtil;
import api.utils.gui.GUIInputDialog;
import org.apache.commons.lang3.math.NumberUtils;
import org.schema.game.common.data.player.catalog.CatalogPermission;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.exchange.BlueprintExchangeItem;
import thederpgamer.edencore.network.client.exchange.ExchangeItemCreatePacket;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/17/2021]
 */
public class AddBlueprintExchangeDialog extends GUIInputDialog {
	public boolean community = true;

	@Override
	public AddBlueprintExchangePanel createPanel() {
		return new AddBlueprintExchangePanel(getState(), this);
	}

	@Override
	public AddBlueprintExchangePanel getInputPanel() {
		return (AddBlueprintExchangePanel) super.getInputPanel();
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

	private BlueprintExchangeItem createItem() {
		if(NumberUtils.isNumber(getInputPanel().currentBarText) && getInputPanel().catalogEntry != null && getInputPanel().barId > 0) {
			String iconPath = "";
			String description = "";
			CatalogPermission permission = getInputPanel().catalogEntry;
			if(permission != null) description = permission.description;
			if(getInputPanel().currentIconText != null && getInputPanel().currentIconText.startsWith("https://") && getInputPanel().currentIconText.endsWith(".png")) iconPath = getInputPanel().currentIconText;
			BlueprintExchangeItem item = new BlueprintExchangeItem(getInputPanel().catalogEntry, getInputPanel().barId, Math.abs(Integer.parseInt(getInputPanel().currentBarText)), description, iconPath);
			item.community = community;
			PacketUtil.sendPacketToServer(new ExchangeItemCreatePacket(0, item));
			return item;
		} else return null;
	}
}

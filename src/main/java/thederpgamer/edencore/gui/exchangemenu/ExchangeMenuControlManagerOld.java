package thederpgamer.edencore.gui.exchangemenu;

import api.common.GameClient;
import api.utils.gui.GUIControlManager;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/17/2021]
 */
public class ExchangeMenuControlManagerOld extends GUIControlManager {
	public ExchangeMenuControlManagerOld() {
		super(GameClient.getClientState());
	}

	@Override
	public ExchangeMenuPanelOld createMenuPanel() {
		return new ExchangeMenuPanelOld(getState());
	}
}
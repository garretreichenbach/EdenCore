package thederpgamer.edencore.gui.exchangemenu;

import api.common.GameClient;
import api.utils.gui.GUIControlManager;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/17/2021]
 */
public class ExchangeMenuControlManager extends GUIControlManager {
	public ExchangeMenuControlManager() {
		super(GameClient.getClientState());
	}

	@Override
	public ExchangeMenuPanel createMenuPanel() {
		return new ExchangeMenuPanel(getState());
	}
}
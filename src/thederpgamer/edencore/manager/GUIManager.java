package thederpgamer.edencore.manager;

import api.utils.gui.ModGUIHandler;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.gui.exchangemenu.ExchangeMenuControlManager;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/17/2021]
 */
public class GUIManager {

    public static ExchangeMenuControlManager exchangeMenuManager;

    public static void initialize() {
        ModGUIHandler.registerNewControlManager(EdenCore.getInstance().getSkeleton(), exchangeMenuManager = new ExchangeMenuControlManager());
    }
}

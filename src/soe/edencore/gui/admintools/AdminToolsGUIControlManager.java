package soe.edencore.gui.admintools;

import api.utils.gui.GUIControlManager;
import org.schema.game.client.data.GameClientState;

/**
 * AdminToolsGUIControlManager.java
 * Control manager for admin tools GUI.
 *
 * @author TheDerpGamer
 * @since 03/20/2021
 */
public class AdminToolsGUIControlManager extends GUIControlManager {

    public AdminToolsGUIControlManager(GameClientState clientState) {
        super(clientState);
    }

    @Override
    public AdminToolsMenuPanel createMenuPanel() {
        return new AdminToolsMenuPanel(getState());
    }
}

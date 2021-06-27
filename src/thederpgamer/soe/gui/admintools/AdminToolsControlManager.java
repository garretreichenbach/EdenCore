package thederpgamer.soe.gui.admintools;

import api.utils.gui.GUIControlManager;
import api.utils.gui.GUIMenuPanel;
import org.schema.game.client.data.GameClientState;

/**
 * GUIControlManager for Admin Tools menu.
 *
 * @author TheDerpGamer
 * @since 06/27/2021
 */
public class AdminToolsControlManager extends GUIControlManager {

    public AdminToolsControlManager(GameClientState clientState) {
        super(clientState);
    }

    @Override
    public GUIMenuPanel createMenuPanel() {
        return new AdminToolsMenuPanel(getState());
    }
}

package thederpgamer.edencore.gui.buildtools;

import api.utils.gui.GUIControlManager;
import api.utils.gui.GUIMenuPanel;
import org.schema.game.client.data.GameClientState;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/30/2021
 */
public class BuildToolsControlManager extends GUIControlManager {

    public BuildToolsControlManager(GameClientState clientState) {
        super(clientState);
    }

    @Override
    public GUIMenuPanel createMenuPanel() {
        return new BuildToolsMenuPanel(getState());
    }
}

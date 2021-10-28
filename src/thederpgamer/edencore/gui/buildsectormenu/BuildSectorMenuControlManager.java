package thederpgamer.edencore.gui.buildsectormenu;

import api.common.GameClient;
import api.utils.gui.GUIControlManager;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/22/2021]
 */
public class BuildSectorMenuControlManager extends GUIControlManager {

    public BuildSectorMenuControlManager() {
        super(GameClient.getClientState());
    }

    @Override
    public BuildSectorMenuPanel createMenuPanel() {
        return new BuildSectorMenuPanel(getState());
    }
}

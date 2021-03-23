package soe.edencore.gui.admintools.player.group;

import api.utils.gui.GUIControlManager;
import org.schema.game.client.data.GameClientState;
import soe.edencore.server.permissions.PermissionGroup;

/**
 * GroupEditorControlManager.java
 * <Description>
 *
 * @author TheDerpGamer
 * @since 03/22/2021
 */
public class GroupEditorControlManager extends GUIControlManager {

    public PermissionGroup group = null;

    public GroupEditorControlManager(GameClientState clientState) {
        super(clientState);
    }

    @Override
    public GroupEditorMenuPanel createMenuPanel() {
        return new GroupEditorMenuPanel(getState(), group);
    }
}

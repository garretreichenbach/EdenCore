package soe.edencore.gui.admintools.player;

import api.utils.gui.GUIControlManager;
import api.utils.gui.GUIMenuPanel;
import org.schema.game.client.data.GameClientState;
import soe.edencore.data.player.PlayerData;
import soe.edencore.gui.admintools.player.group.GroupsEditorMenuPanel;
import soe.edencore.gui.admintools.player.permission.PlayerPermissionEditorMenuPanel;
import soe.edencore.gui.admintools.player.rank.PlayerRankEditorMenuPanel;

/**
 * PlayerDataEditorControlManager.java
 * <Description>
 *
 * @author TheDerpGamer
 * @since 03/20/2021
 */
public class PlayerDataEditorControlManager extends GUIControlManager {

    public static final int RANK_EDITOR = 1;
    public static final int GROUP_EDITOR = 2;
    public static final int PERMISSION_EDITOR = 3;

    public PlayerData playerData;
    private int mode = 0;

    public PlayerDataEditorControlManager(GameClientState clientState) {
        super(clientState);
    }

    public void activate(int mode, PlayerData playerData) {
        this.playerData = playerData;
        this.mode = mode;
        this.onInit();
        this.setActive(true);
    }

    @Override
    public GUIMenuPanel createMenuPanel() {
        if(playerData != null) {
            switch(mode) {
                case RANK_EDITOR:
                    return new PlayerRankEditorMenuPanel(getState(), playerData);
                case GROUP_EDITOR:
                    return new GroupsEditorMenuPanel(getState(), playerData);
                case PERMISSION_EDITOR:
                    return new PlayerPermissionEditorMenuPanel(getState(), playerData);
            }
        }
        return null;
    }
}

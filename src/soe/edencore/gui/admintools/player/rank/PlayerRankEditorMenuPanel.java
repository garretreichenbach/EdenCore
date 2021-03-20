package soe.edencore.gui.admintools.player.rank;

import api.utils.gui.GUIMenuPanel;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.input.InputState;
import soe.edencore.data.player.PlayerData;

/**
 * PlayerRankEditorMenuPanel.java
 * <Description>
 *
 * @author TheDerpGamer
 * @since 03/20/2021
 */
public class PlayerRankEditorMenuPanel extends GUIMenuPanel {

    private PlayerData playerData;

    public PlayerRankEditorMenuPanel(InputState inputState, PlayerData playerData) {
        super(inputState, "PlayerRankEditorMenuPanel", 800, 500);
        this.playerData = playerData;
    }

    @Override
    public void recreateTabs() {
        guiWindow.clearTabs();
        GUIContentPane rankTab = guiWindow.addTab("EDIT RANKS");
    }
}

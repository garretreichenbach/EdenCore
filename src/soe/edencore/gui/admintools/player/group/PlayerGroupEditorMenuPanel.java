package soe.edencore.gui.admintools.player.group;

import api.utils.gui.GUIMenuPanel;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.input.InputState;
import soe.edencore.data.player.PlayerData;

/**
 * PlayerGroupEditorMenuPanel.java
 * <Description>
 *
 * @author TheDerpGamer
 * @since 03/20/2021
 */
public class PlayerGroupEditorMenuPanel extends GUIMenuPanel {

    private PlayerData playerData;

    public PlayerGroupEditorMenuPanel(InputState inputState, PlayerData playerData) {
        super(inputState, "PlayerGroupEditorMenuPanel", 800, 500);
        this.playerData = playerData;
    }

    @Override
    public void recreateTabs() {
        orientate(GUIElement.ORIENTATION_HORIZONTAL_MIDDLE | GUIElement.ORIENTATION_VERTICAL_MIDDLE);
        guiWindow.clearTabs();

        GUIContentPane groupsTab = guiWindow.addTab("EDIT GROUPS");
        groupsTab.setTextBoxHeightLast((int) (getHeight() - 110));
        //Todo: Groups tab
    }
}

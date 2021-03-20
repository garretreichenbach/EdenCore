package soe.edencore.gui.admintools.player.permission;

import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUIElementList;
import org.schema.schine.graphicsengine.forms.gui.newgui.ScrollableTableList;
import org.schema.schine.input.InputState;
import soe.edencore.data.player.PlayerData;
import soe.edencore.server.ServerDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * PlayerPermissionList.java
 * <Description>
 *
 * @author TheDerpGamer
 * @since 03/20/2021
 */
public class PlayerPermissionList extends ScrollableTableList<String> {

    private PlayerData playerData;

    public PlayerPermissionList(InputState inputState, GUIElement guiElement, PlayerData playerData) {
        super(inputState, 739, 300, guiElement);
        this.playerData = playerData;
    }

    @Override
    public ArrayList<String> getElementList() {
        return playerData.getPermissions();
    }

    @Override
    public void initColumns() {

    }

    @Override
    public void updateListEntries(GUIElementList guiElementList, Set<String> set) {

    }
}

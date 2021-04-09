package soe.edencore.gui.admintools.player.group;

import api.utils.gui.GUIMenuPanel;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIActivationCallback;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalArea;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalButtonTablePane;
import org.schema.schine.input.InputState;
import soe.edencore.data.player.PlayerData;
import soe.edencore.server.ServerDatabase;

/**
 * GroupsEditorMenuPanel.java
 * <Description>
 *
 * @author TheDerpGamer
 * @since 03/20/2021
 */
public class GroupsEditorMenuPanel extends GUIMenuPanel {

    private PlayerData playerData;
    private PlayerGroupsList playerGroupsList;
    private ServerGroupsList serverGroupsList;

    public GroupsEditorMenuPanel(InputState inputState, PlayerData playerData) {
        super(inputState, "GroupsEditorMenuPanel", 800, 500);
        this.playerData = playerData;
    }

    @Override
    public void recreateTabs() {
        orientate(GUIElement.ORIENTATION_HORIZONTAL_MIDDLE | GUIElement.ORIENTATION_VERTICAL_MIDDLE);
        guiWindow.clearTabs();
        GUIContentPane groupsTab = guiWindow.addTab("EDIT GROUPS");
        groupsTab.setTextBoxHeightLast((int) (getHeight() - 110));

        (playerGroupsList = new PlayerGroupsList(getState(), groupsTab.getContent(0, 0), playerData, 350)).onInit();
        groupsTab.getContent(0, 0).attach(playerGroupsList);

        groupsTab.addDivider(470);
        groupsTab.setTextBoxHeight(1, 0, 445);
        (serverGroupsList = new ServerGroupsList(getState(), groupsTab.getContent(1, 0), playerData, 350)).onInit();
        groupsTab.getContent(1, 0).attach(serverGroupsList);

        groupsTab.addNewTextBox(1, 30);
        GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 1, 1, groupsTab.getContent(1, 1));
        buttonPane.onInit();
        buttonPane.addButton(0, 0, "CREATE GROUP", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent event) {
                if(event.pressedLeftMouse()) {
                    //Todo: Group creation dialog
                }
            }

            @Override
            public boolean isOccluded() {
                return false;
            }
        }, new GUIActivationCallback() {
            @Override
            public boolean isVisible(InputState inputState) {
                return true;
            }

            @Override
            public boolean isActive(InputState inputState) {
                return true;
            }
        });
        buttonPane.addButton(1, 0, "DELETE GROUP", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent event) {
                if(event.pressedLeftMouse() && serverGroupsList.getSelectedRow() != null && serverGroupsList.getSelectedRow().f != null) {
                    ServerDatabase.removeGroup(serverGroupsList.getSelectedRow().f);
                }
            }

            @Override
            public boolean isOccluded() {
                return false;
            }
        }, new GUIActivationCallback() {
            @Override
            public boolean isVisible(InputState inputState) {
                return true;
            }

            @Override
            public boolean isActive(InputState inputState) {
                return true;
            }
        });
        groupsTab.getContent(1, 1).attach(buttonPane);
    }
}

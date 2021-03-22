package soe.edencore.gui.admintools.player.permission;

import api.utils.gui.GUIMenuPanel;
import api.utils.gui.SimplePlayerTextInput;
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
 * PlayerPermissionEditorMenuPanel.java
 * <Description>
 *
 * @author TheDerpGamer
 * @since 03/20/2021
 */
public class PlayerPermissionEditorMenuPanel extends GUIMenuPanel {

    private PlayerData playerData;

    public PlayerPermissionEditorMenuPanel(InputState inputState, PlayerData playerData) {
        super(inputState, "PlayerPermissionEditorMenuPanel", 800, 500);
        this.playerData = playerData;
    }

    @Override
    public void recreateTabs() {
        orientate(GUIElement.ORIENTATION_HORIZONTAL_MIDDLE | GUIElement.ORIENTATION_VERTICAL_MIDDLE);
        guiWindow.clearTabs();
        GUIContentPane permissionsTab = guiWindow.addTab("EDIT PERMISSIONS");
        permissionsTab.setTextBoxHeightLast((int) (getHeight() - 110));

        final PlayerPermissionList permissionsList = new PlayerPermissionList(getState(), permissionsTab.getContent(0), playerData);
        permissionsList.onInit();
        permissionsTab.getContent(0).attach(permissionsList);

        permissionsTab.addNewTextBox(30);
        GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 1, 1, permissionsTab.getContent(1));
        buttonPane.onInit();

        buttonPane.addButton(0, 0, "ADD PERMISSION", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent event) {
                if(event.pressedLeftMouse()) {
                    getState().getController().queueUIAudio("0022_menu_ui - select 1");
                    new SimplePlayerTextInput("Add New Permission", "Permission Node") {
                        @Override
                        public boolean onInput(String s) {
                            if(s != null && s.length() > 0) {
                                if(!playerData.hasPermission(s)) {
                                    playerData.getPermissions().add(s);
                                    ServerDatabase.updatePlayerData(playerData);
                                    permissionsList.flagDirty();
                                    permissionsList.handleDirty();
                                }
                                return true;
                            } else {
                                return false;
                            }
                        }
                    };
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

        buttonPane.addButton(1, 0, "REMOVE PERMISSION", GUIHorizontalArea.HButtonColor.ORANGE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent event) {
                if(event.pressedLeftMouse() && permissionsList.getSelectedRow() != null) {
                    getState().getController().queueUIAudio("0022_menu_ui - select 1");
                    playerData.getPermissions().remove(permissionsList.getSelectedRow().f);
                    ServerDatabase.updatePlayerData(playerData);
                    permissionsList.flagDirty();
                    permissionsList.handleDirty();
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

        permissionsTab.getContent(1).attach(buttonPane);
    }
}

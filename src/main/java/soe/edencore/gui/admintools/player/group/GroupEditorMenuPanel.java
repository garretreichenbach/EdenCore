package soe.edencore.gui.admintools.player.group;

import api.utils.gui.GUIMenuPanel;
import api.utils.gui.SimplePlayerTextInput;
import org.schema.schine.common.TextAreaInput;
import org.schema.schine.common.TextCallback;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.core.settings.PrefixNotFoundException;
import org.schema.schine.graphicsengine.forms.gui.GUIActivationCallback;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUITextInput;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalArea;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalButtonTablePane;
import org.schema.schine.input.InputState;
import soe.edencore.gui.admintools.player.permission.GroupPermissionList;
import soe.edencore.server.ServerDatabase;
import soe.edencore.server.permissions.PermissionGroup;

/**
 * GroupEditorMenuPanel.java
 * <Description>
 *
 * @author TheDerpGamer
 * @since 03/22/2021
 */
public class GroupEditorMenuPanel extends GUIMenuPanel {

    private PermissionGroup group;
    private GUITextInput groupNameInput;

    public GroupEditorMenuPanel(InputState inputState, PermissionGroup group) {
        super(inputState, "GroupEditorMenuPanel", 800, 500);
        this.group = group;
    }

    @Override
    public void recreateTabs() {
        orientate(GUIElement.ORIENTATION_HORIZONTAL_MIDDLE | GUIElement.ORIENTATION_VERTICAL_MIDDLE);
        guiWindow.clearTabs();
        GUIContentPane groupEditTab = guiWindow.addTab("EDIT GROUP");
        groupEditTab.setTextBoxHeightLast((int) (getHeight() - 110));

        (groupNameInput = new GUITextInput(730, 30, getState())).setTextBox(true);
        groupNameInput.setTextInput(new TextAreaInput(15, 1, new TextCallback() {
            @Override
            public String[] getCommandPrefixes() {
                return new String[0];
            }

            @Override
            public String handleAutoComplete(String s, TextCallback textCallback, String s1) throws PrefixNotFoundException {
                return null;
            }

            @Override
            public void onFailedTextCheck(String s) {

            }

            @Override
            public void onTextEnter(String s, boolean b, boolean b1) {

            }

            @Override
            public void newLine() {

            }
        }));
        groupNameInput.setUserPointer("NAME_INPUT");
        groupNameInput.setMouseUpdateEnabled(true);
        groupNameInput.setDrawCarrier(false);
        groupNameInput.setCallback(new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent event) {
                if(event.pressedLeftMouse()) {
                    if(guiElement != null && guiElement.getUserPointer() != null) {
                        groupNameInput.setDrawCarrier(guiElement.getUserPointer().equals("NAME_INPUT"));
                    } else {
                        groupNameInput.setDrawCarrier(false);
                    }
                }
            }

            @Override
            public boolean isOccluded() {
                return false;
            }
        });
        groupNameInput.getTextInput().append(group.getGroupName());
        groupEditTab.getContent(0, 0).attach(groupNameInput);

        groupEditTab.addNewTextBox(0, 450);
        final GroupPermissionList permissionsList = new GroupPermissionList(getState(), groupEditTab.getContent(0, 1), group);
        permissionsList.onInit();
        groupEditTab.getContent(0, 1).attach(permissionsList);

        groupEditTab.addNewTextBox(0, 30);
        GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 1, 1, groupEditTab.getContent(0, 2));
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
                                if(!group.getPermissions().contains(s)) {
                                    group.getPermissions().add(s);
                                    ServerDatabase.updateGroup(group);
                                    ServerDatabase.updateGUIs();
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
                    group.getPermissions().remove(permissionsList.getSelectedRow().f);
                    ServerDatabase.updateGroup(group);
                    ServerDatabase.updateGUIs();
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
        buttonPane.addButton(2, 0, "EDIT INHERITANCES", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent event) {
                if(event.pressedLeftMouse()) {
                    //Todo: Edit Inheritances
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
        groupEditTab.getContent(0, 2).attach(buttonPane);
    }
}

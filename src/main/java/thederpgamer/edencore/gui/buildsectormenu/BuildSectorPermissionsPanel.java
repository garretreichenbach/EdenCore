package thederpgamer.edencore.gui.buildsectormenu;

import api.utils.gui.GUIInputDialogPanel;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIActivationCallback;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalArea;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalButtonTablePane;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.other.BuildSectorData;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/30/2021]
 */
public class BuildSectorPermissionsPanel extends GUIInputDialogPanel {

    public BuildSectorPermissionsPanel(InputState inputState, GUICallback guiCallback) {
        super(inputState, "build_sector_permissions_panel", "Manage Permissions", "", 800, 300, guiCallback);
    }

    public void createPanel(final BuildSectorData sectorData, final String targetName) {
        GUIContentPane contentPane = ((GUIDialogWindow) background).getMainContentPane();
        contentPane.setTextBoxHeightLast((int) getHeight());

        GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 2, 7, contentPane);
        buttonPane.onInit();

        { //ENTER
            buttonPane.addButton(0, 0, "ALLOW ENTRY", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        getState().getController().queueUIAudio("0022_menu_ui - enter");
                        sectorData.allowPermission(targetName, "ENTER");
                    }
                }

                @Override
                public boolean isOccluded() {
                    return sectorData.hasPermission(targetName, "ENTER");
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return true;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return !sectorData.hasPermission(targetName, "ENTER");
                }
            });

            buttonPane.addButton(1, 0, "DENY ENTRY", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        getState().getController().queueUIAudio("0022_menu_ui - cancel");
                        sectorData.denyPermission(targetName, "ENTER");
                    }
                }

                @Override
                public boolean isOccluded() {
                    return !sectorData.hasPermission(targetName, "ENTER");
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return true;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return sectorData.hasPermission(targetName, "ENTER");
                }
            });
        }

        { //EDIT
            buttonPane.addButton(0, 1, "ALLOW EDITING", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        getState().getController().queueUIAudio("0022_menu_ui - enter");
                        sectorData.allowPermission(targetName, "EDIT");
                    }
                }

                @Override
                public boolean isOccluded() {
                    return sectorData.hasPermission(targetName, "EDIT");
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return true;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return !sectorData.hasPermission(targetName, "EDIT");
                }
            });

            buttonPane.addButton(1, 1, "DENY EDITING", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        getState().getController().queueUIAudio("0022_menu_ui - cancel");
                        sectorData.denyPermission(targetName, "EDIT");
                    }
                }

                @Override
                public boolean isOccluded() {
                    return !sectorData.hasPermission(targetName, "EDIT");
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return true;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return sectorData.hasPermission(targetName, "EDIT");
                }
            });
        }

        { //PICKUP
            buttonPane.addButton(0, 2, "ALLOW PICKUP", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        getState().getController().queueUIAudio("0022_menu_ui - enter");
                        sectorData.allowPermission(targetName, "PICKUP");
                    }
                }

                @Override
                public boolean isOccluded() {
                    return sectorData.hasPermission(targetName, "PICKUP");
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return true;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return !sectorData.hasPermission(targetName, "PICKUP");
                }
            });

            buttonPane.addButton(1, 2, "DENY PICKUP", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        getState().getController().queueUIAudio("0022_menu_ui - cancel");
                        sectorData.denyPermission(targetName, "PICKUP");
                    }
                }

                @Override
                public boolean isOccluded() {
                    return !sectorData.hasPermission(targetName, "PICKUP");
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return true;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return sectorData.hasPermission(targetName, "PICKUP");
                }
            });
        }

        { //SPAWN
            buttonPane.addButton(0, 3, "ALLOW SPAWNING", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        getState().getController().queueUIAudio("0022_menu_ui - enter");
                        sectorData.allowPermission(targetName, "SPAWN");
                    }
                }

                @Override
                public boolean isOccluded() {
                    return sectorData.hasPermission(targetName, "SPAWN");
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return true;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return !sectorData.hasPermission(targetName, "SPAWN");
                }
            });

            buttonPane.addButton(1, 3, "DENY SPAWNING", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        getState().getController().queueUIAudio("0022_menu_ui - cancel");
                        sectorData.denyPermission(targetName, "SPAWN");
                    }
                }

                @Override
                public boolean isOccluded() {
                    return !sectorData.hasPermission(targetName, "SPAWN");
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return true;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return sectorData.hasPermission(targetName, "SPAWN");
                }
            });
        }

        { //SPAWN ENEMIES
            buttonPane.addButton(0, 4, "ALLOW ENEMY SPAWNING", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        getState().getController().queueUIAudio("0022_menu_ui - enter");
                        sectorData.allowPermission(targetName, "SPAWN_ENEMIES");
                    }
                }

                @Override
                public boolean isOccluded() {
                    return sectorData.hasPermission(targetName, "SPAWN_ENEMIES");
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return true;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return !sectorData.hasPermission(targetName, "SPAWN_ENEMIES");
                }
            });

            buttonPane.addButton(1, 4, "DENY ENEMY SPAWNING", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        getState().getController().queueUIAudio("0022_menu_ui - cancel");
                        sectorData.denyPermission(targetName, "SPAWN_ENEMIES");
                    }
                }

                @Override
                public boolean isOccluded() {
                    return !sectorData.hasPermission(targetName, "SPAWN_ENEMIES");
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return true;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return sectorData.hasPermission(targetName, "SPAWN_ENEMIES");
                }
            });
        }

        { //DELETE
            buttonPane.addButton(0, 5, "ALLOW DELETION", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        getState().getController().queueUIAudio("0022_menu_ui - enter");
                        sectorData.allowPermission(targetName, "DELETE");
                    }
                }

                @Override
                public boolean isOccluded() {
                    return sectorData.hasPermission(targetName, "DELETE");
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return true;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return !sectorData.hasPermission(targetName, "DELETE");
                }
            });

            buttonPane.addButton(1, 5, "DENY DELETION", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        getState().getController().queueUIAudio("0022_menu_ui - cancel");
                        sectorData.denyPermission(targetName, "DELETE");
                    }
                }

                @Override
                public boolean isOccluded() {
                    return !sectorData.hasPermission(targetName, "DELETE");
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return true;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return sectorData.hasPermission(targetName, "DELETE");
                }
            });
        }

        { //TOGGLE_AI
            buttonPane.addButton(0, 6, "ALLOW AI TOGGLING", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        getState().getController().queueUIAudio("0022_menu_ui - enter");
                        sectorData.allowPermission(targetName, "TOGGLE_AI");
                    }
                }

                @Override
                public boolean isOccluded() {
                    return sectorData.hasPermission(targetName, "TOGGLE_AI");
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return true;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return !sectorData.hasPermission(targetName, "TOGGLE_AI");
                }
            });

            buttonPane.addButton(1, 6, "DENY AI TOGGLING", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        getState().getController().queueUIAudio("0022_menu_ui - cancel");
                        sectorData.denyPermission(targetName, "TOGGLE_AI");
                    }
                }

                @Override
                public boolean isOccluded() {
                    return !sectorData.hasPermission(targetName, "TOGGLE_AI");
                }
            }, new GUIActivationCallback() {
                @Override
                public boolean isVisible(InputState inputState) {
                    return true;
                }

                @Override
                public boolean isActive(InputState inputState) {
                    return sectorData.hasPermission(targetName, "TOGGLE_AI");
                }
            });
        }

        contentPane.getContent(0).attach(buttonPane);
    }
}

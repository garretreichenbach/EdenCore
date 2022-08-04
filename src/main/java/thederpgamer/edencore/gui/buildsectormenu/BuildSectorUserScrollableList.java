package thederpgamer.edencore.gui.buildsectormenu;

import api.common.GameClient;
import api.network.packets.PacketUtil;
import api.utils.gui.SimplePlayerTextInput;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.manager.ConfigManager;
import thederpgamer.edencore.manager.LogManager;
import thederpgamer.edencore.network.client.RequestBuildSectorBanPacket;
import thederpgamer.edencore.network.client.RequestBuildSectorKickPacket;
import thederpgamer.edencore.network.client.RequestClientCacheUpdatePacket;
import thederpgamer.edencore.utils.DataUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/28/2021]
 */
public class BuildSectorUserScrollableList extends ScrollableTableList<String> {

    private final GUIElement p;
    private final BuildSectorMenuPanel panel;
    private BuildSectorData sectorData;

    public BuildSectorUserScrollableList(InputState state, GUIElement p, BuildSectorMenuPanel panel) {
        super(state, 800, 500, p);
        this.panel = panel;
        this.p = p;
        this.sectorData = DataUtils.getBuildSector(GameClient.getClientPlayerState().getName());
        p.attach(this);
    }

    private GUIHorizontalButtonTablePane redrawButtonPane(final String playerName, GUIAncor anchor) {
        GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 3, 1, anchor);
        buttonPane.onInit();

        buttonPane.addButton(0, 0, "PERMISSIONS", GUIHorizontalArea.HButtonColor.YELLOW, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if(mouseEvent.pressedLeftMouse()) {
                    if(GameClient.getClientPlayerState().getName().equals(sectorData.ownerName)) {
                        getState().getController().queueUIAudio("0022_menu_ui - select 1");
                        BuildSectorPermissionsDialog permissionsDialog = new BuildSectorPermissionsDialog();
                        permissionsDialog.sectorData = sectorData;
                        permissionsDialog.targetName = playerName;
                        permissionsDialog.getInputPanel().createPanel(sectorData, playerName);
                        permissionsDialog.activate();
                    } else getState().getController().queueUIAudio("0022_menu_ui - error 1");
                }
            }

            @Override
            public boolean isOccluded() {
                return (playerName.equals(sectorData.ownerName) && !ConfigManager.getMainConfig().getBoolean("debug-mode")) || !getState().getController().getPlayerInputs().isEmpty();
            }
        }, new GUIActivationCallback() {
            @Override
            public boolean isVisible(InputState inputState) {
                return true;
            }

            @Override
            public boolean isActive(InputState inputState) {
                return (!playerName.equals(sectorData.ownerName) || ConfigManager.getMainConfig().getBoolean("debug-mode")) && getState().getController().getPlayerInputs().isEmpty();
            }
        });

        buttonPane.addButton(1, 0, "KICK", GUIHorizontalArea.HButtonColor.ORANGE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if(mouseEvent.pressedLeftMouse()) {
                    if(GameClient.getClientPlayerState().getName().equals(sectorData.ownerName)) {
                        getState().getController().queueUIAudio("0022_menu_ui - select 2");
                        (new SimplePlayerTextInput("Enter Kick Reason", "") {
                            @Override
                            public boolean onInput(String s) {
                                if(!s.equals(sectorData.ownerName)) {
                                    PacketUtil.sendPacketToServer(new RequestBuildSectorKickPacket(sectorData.ownerName, playerName, s));
                                    panel.recreateTabs();
                                    return true;
                                } else {
                                    getState().getController().queueUIAudio("0022_menu_ui - error 2");
                                    return false;
                                }
                            }
                        }).activate();
                    } else getState().getController().queueUIAudio("0022_menu_ui - error 1");
                }
            }

            @Override
            public boolean isOccluded() {
                return (playerName.equals(sectorData.ownerName) && !ConfigManager.getMainConfig().getBoolean("debug-mode")) || !getState().getController().getPlayerInputs().isEmpty();
            }
        }, new GUIActivationCallback() {
            @Override
            public boolean isVisible(InputState inputState) {
                return true;
            }

            @Override
            public boolean isActive(InputState inputState) {
                return (!playerName.equals(sectorData.ownerName) || ConfigManager.getMainConfig().getBoolean("debug-mode")) && getState().getController().getPlayerInputs().isEmpty();
            }
        });

        buttonPane.addButton(2, 0, "BAN", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if(mouseEvent.pressedLeftMouse()) {
                    if(GameClient.getClientPlayerState().getName().equals(sectorData.ownerName)) {
                        getState().getController().queueUIAudio("0022_menu_ui - select 3");
                        (new SimplePlayerTextInput("Enter Ban Reason", "") {
                            @Override
                            public boolean onInput(String s) {
                                if(!s.equals(sectorData.ownerName)) {
                                    PacketUtil.sendPacketToServer(new RequestBuildSectorBanPacket(sectorData.ownerName, playerName, s));
                                    panel.recreateTabs();
                                    return true;
                                } else {
                                    getState().getController().queueUIAudio("0022_menu_ui - error 2");
                                    return false;
                                }
                            }
                        }).activate();
                    } else getState().getController().queueUIAudio("0022_menu_ui - error 1");
                }
            }

            @Override
            public boolean isOccluded() {
                return (playerName.equals(sectorData.ownerName) && !ConfigManager.getMainConfig().getBoolean("debug-mode")) || !getState().getController().getPlayerInputs().isEmpty();
            }
        }, new GUIActivationCallback() {
            @Override
            public boolean isVisible(InputState inputState) {
                return true;
            }

            @Override
            public boolean isActive(InputState inputState) {
                return (!playerName.equals(sectorData.ownerName) || ConfigManager.getMainConfig().getBoolean("debug-mode")) && getState().getController().getPlayerInputs().isEmpty();
            }
        });

        LogManager.logDebug("" + buttonPane.getWidth());
        return buttonPane;
    }

    @Override
    protected Collection<String> getElementList() {
        ArrayList<String> permissions = new ArrayList<>();
        if(sectorData == null) {
            PacketUtil.sendPacketToServer(new RequestClientCacheUpdatePacket());
            sectorData = DataUtils.getBuildSector(GameClient.getClientPlayerState().getName());
        }
        if(sectorData != null) permissions.addAll(sectorData.permissions.keySet());
        return permissions;
    }

    @Override
    public void initColumns() {
        addColumn("Users", 15.0f, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        addTextFilter(new GUIListFilterText<String>() {
            @Override
            public boolean isOk(String s, String name) {
                return name.toLowerCase().contains(s.toLowerCase());
            }
        }, "SEARCH BY NAME", ControllerElement.FilterRowStyle.FULL);
    }

    @Override
    public void updateListEntries(GUIElementList guiElementList, Set<String> set) {
        guiElementList.deleteObservers();
        guiElementList.addObserver(this);
        for(String playerName : set) {
            if(sectorData.hasPermission(playerName, "ENTER")) {
                String name = (playerName.equals(sectorData.ownerName)) ? playerName + " (Owner)" : playerName;
                GUITextOverlayTable nameTextElement;
                (nameTextElement = new GUITextOverlayTable(10, 10, this.getState())).setTextSimple(name);
                GUIClippedRow nameRowElement;
                (nameRowElement = new GUIClippedRow(this.getState())).attach(nameTextElement);

                BuildSectorUserScrollableListRow listRow = new BuildSectorUserScrollableListRow(getState(), playerName, nameRowElement);
                GUIAncor anchor = new GUIAncor(getState(), p.getWidth() - 28.0f, 28.0f);
                anchor.attach(redrawButtonPane(playerName, anchor));
                listRow.expanded = new GUIElementList(getState());
                listRow.expanded.add(new GUIListElement(anchor, getState()));
                listRow.expanded.attach(anchor);
                listRow.onInit();
                guiElementList.addWithoutUpdate(listRow);
            }
        }
        guiElementList.updateDim();
    }

    public class BuildSectorUserScrollableListRow extends ScrollableTableList<String>.Row {

        public BuildSectorUserScrollableListRow(InputState state, String playerName, GUIElement... elements) {
            super(state, playerName, elements);
            this.highlightSelect = true;
            this.highlightSelectSimple = true;
            this.setAllwaysOneSelected(true);
        }

        @Override
        public void extended() {
            if(!isOccluded()) super.extended();
            else super.unexpend();
        }

        @Override
        public void collapsed() {
            if(!isOccluded()) super.collapsed();
            else super.extended();
        }

        @Override
        public boolean isOccluded() {
            return panel.textInput != null && panel.textInput.isActive();
        }
    }
}

package soe.edencore.gui.admintools.player;

import api.common.GameClient;
import api.utils.gui.ControlManagerHandler;
import org.hsqldb.lib.StringComparator;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import soe.edencore.EdenCore;
import soe.edencore.data.player.PlayerData;
import soe.edencore.server.ServerDatabase;
import soe.edencore.server.permissions.PermissionGroup;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

/**
 * PlayerDataList.java
 * <Description>
 *
 * @author TheDerpGamer
 * @since 03/20/2021
 */
public class PlayerDataList extends ScrollableTableList<PlayerData> {

    public PlayerDataList(InputState inputState, GUIElement guiElement) {
        super(inputState, 739, 300, guiElement);
    }

    @Override
    public ArrayList<PlayerData> getElementList() {
        return ServerDatabase.getAllPlayerData();
    }

    @Override
    public void initColumns() {
        new StringComparator();

        addColumn("Name", 8.0f, new Comparator<PlayerData>() {
            public int compare(PlayerData o1, PlayerData o2) {
                return o1.getPlayerName().compareTo(o2.getPlayerName());
            }
        });

        addColumn("Faction", 8.0f, new Comparator<PlayerData>() {
            @Override
            public int compare(PlayerData o1, PlayerData o2) {
                return o1.getFactionName().compareTo(o2.getFactionName());
            }
        });

        addColumn("Rank", 7.0f, new Comparator<PlayerData>() {
            @Override
            public int compare(PlayerData o1, PlayerData o2) {
                return o1.getRank().compareTo(o2.getRank());
            }
        });

        addColumn("Play Time", 6.5f, new Comparator<PlayerData>() {
            @Override
            public int compare(PlayerData o1, PlayerData o2) {
                return Double.compare(o1.getHoursPlayed(), o2.getHoursPlayed());
            }
        });

        addTextFilter(new GUIListFilterText<PlayerData>() {
            public boolean isOk(String s, PlayerData playerData) {
                return playerData.getPlayerName().toLowerCase().contains(s.toLowerCase());
            }
        }, "PLAYER NAME", ControllerElement.FilterRowStyle.LEFT);

        addTextFilter(new GUIListFilterText<PlayerData>() {
            public boolean isOk(String s, PlayerData playerData) {
                for(PermissionGroup group : playerData.getGroups()) {
                    if(group.getGroupName().toLowerCase().contains(s.toLowerCase())) return true;
                }
                return false;
            }
        }, "PERMISSION GROUP", ControllerElement.FilterRowStyle.RIGHT);

        activeSortColumnIndex = 0;
    }

    @Override
    public void updateListEntries(GUIElementList guiElementList, Set<PlayerData> set) {
        guiElementList.deleteObservers();
        guiElementList.addObserver(this);

        for(PlayerData playerData : set) {
            GUITextOverlayTable nameTextElement;
            (nameTextElement = new GUITextOverlayTable(10, 10, getState())).setTextSimple(playerData.getPlayerName());
            GUIClippedRow nameRowElement;
            (nameRowElement = new GUIClippedRow(getState())).attach(nameTextElement);

            GUITextOverlayTable factionTextElement;
            (factionTextElement = new GUITextOverlayTable(10, 10, getState())).setTextSimple(playerData.getFactionName());
            GUIClippedRow factionRowElement;
            (factionRowElement = new GUIClippedRow(getState())).attach(factionTextElement);

            GUITextOverlayTable rankTextElement;
            (rankTextElement = new GUITextOverlayTable(10, 10, getState())).setTextSimple(playerData.getRank().rankName);
            GUIClippedRow rankRowElement;
            (rankRowElement = new GUIClippedRow(getState())).attach(rankTextElement);

            GUITextOverlayTable playTimeTextElement;
            (playTimeTextElement = new GUITextOverlayTable(10, 10, getState())).setTextSimple(playerData.getHoursPlayed() + " hours");
            GUIClippedRow playTimeRowElement;
            (playTimeRowElement = new GUIClippedRow(getState())).attach(playTimeTextElement);

            PlayerDataListRow playerDataListRow = new PlayerDataListRow(getState(), playerData, nameRowElement, factionRowElement, rankRowElement, playTimeRowElement);
            GUIAncor anchor = new GUIAncor(getState(), 739.0f, 28.0f);
            anchor.attach(redrawButtonPane(playerData, anchor));
            playerDataListRow.expanded = new GUIElementList(getState());
            playerDataListRow.expanded.add(new GUIListElement(anchor, getState()));
            playerDataListRow.expanded.attach(anchor);
            playerDataListRow.onInit();
            guiElementList.add(playerDataListRow);
        }
        guiElementList.updateDim();
    }

    public GUIHorizontalButtonTablePane redrawButtonPane(final PlayerData playerData, GUIAncor anchor) {
        GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 1, 1, anchor);
        buttonPane.onInit();

        buttonPane.addButton(0, 0, "ACTIONS", GUIHorizontalArea.HButtonColor.ORANGE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent event) {
                if(event.pressedLeftMouse()) {
                    getState().getController().queueUIAudio("0022_menu_ui - enter");
                    //Todo: Player actions panel
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

        buttonPane.addButton(1, 0, "EDIT RANK", GUIHorizontalArea.HButtonColor.YELLOW, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent event) {
                if(event.pressedLeftMouse()) {
                    getState().getController().queueUIAudio("0022_menu_ui - enter");
                    if(EdenCore.instance.playerDataEditorControlManager == null) {
                        EdenCore.instance.playerDataEditorControlManager = new PlayerDataEditorControlManager(GameClient.getClientState());
                        ControlManagerHandler.registerNewControlManager(EdenCore.instance.getSkeleton(), EdenCore.instance.playerDataEditorControlManager);
                    }
                    EdenCore.instance.playerDataEditorControlManager.activate(PlayerDataEditorControlManager.RANK_EDITOR, playerData);
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

        buttonPane.addButton(2, 0, "EDIT GROUPS", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent event) {
                if(event.pressedLeftMouse()) {
                    getState().getController().queueUIAudio("0022_menu_ui - enter");
                    if(EdenCore.instance.playerDataEditorControlManager == null) {
                        EdenCore.instance.playerDataEditorControlManager = new PlayerDataEditorControlManager(GameClient.getClientState());
                        ControlManagerHandler.registerNewControlManager(EdenCore.instance.getSkeleton(), EdenCore.instance.playerDataEditorControlManager);
                    }
                    EdenCore.instance.playerDataEditorControlManager.activate(PlayerDataEditorControlManager.GROUP_EDITOR, playerData);
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

        buttonPane.addButton(3, 0, "EDIT PERMISSIONS", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent event) {
                if(event.pressedLeftMouse()) {
                    getState().getController().queueUIAudio("0022_menu_ui - enter");
                    if(EdenCore.instance.playerDataEditorControlManager == null) {
                        EdenCore.instance.playerDataEditorControlManager = new PlayerDataEditorControlManager(GameClient.getClientState());
                        ControlManagerHandler.registerNewControlManager(EdenCore.instance.getSkeleton(), EdenCore.instance.playerDataEditorControlManager);
                    }
                    EdenCore.instance.playerDataEditorControlManager.activate(PlayerDataEditorControlManager.PERMISSION_EDITOR, playerData);
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

        return buttonPane;
    }

    public class PlayerDataListRow extends ScrollableTableList<PlayerData>.Row {

        public PlayerDataListRow(InputState inputState, PlayerData playerData, GUIElement... guiElements) {
            super(inputState, playerData, guiElements);
            highlightSelect = true;
            highlightSelectSimple = true;
            setAllwaysOneSelected(true);
        }
    }
}

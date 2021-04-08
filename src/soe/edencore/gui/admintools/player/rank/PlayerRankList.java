package soe.edencore.gui.admintools.player.rank;

import org.hsqldb.lib.StringComparator;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import soe.edencore.data.player.PlayerData;
import soe.edencore.data.player.PlayerRank;
import soe.edencore.server.ServerDatabase;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

/**
 * PlayerRankList.java
 * <Description>
 *
 * @author TheDerpGamer
 * @since 03/21/2021
 */
public class PlayerRankList extends ScrollableTableList<PlayerRank> {

    private PlayerData playerData;

    public PlayerRankList(InputState inputState, GUIElement guiElement, PlayerData playerData) {
        super(inputState, 739, 300, guiElement);
        this.playerData = playerData;
        ServerDatabase.guiLists.add(this);
    }

    @Override
    public ArrayList<PlayerRank> getElementList() {
        return ServerDatabase.getAllRanks();
    }

    @Override
    public void initColumns() {
        new StringComparator();

        addColumn("Name", 7.5f, new Comparator<PlayerRank>() {
            public int compare(PlayerRank o1, PlayerRank o2) {
                return o1.rankName.compareTo(o2.rankName);
            }
        });

        addColumn("Prefix", 8.0f, new Comparator<PlayerRank>() {
            @Override
            public int compare(PlayerRank o1, PlayerRank o2) {
                return o1.chatPrefix.compareTo(o2.chatPrefix);
            }
        });

        addColumn("Level", 4.0f, new Comparator<PlayerRank>() {
            @Override
            public int compare(PlayerRank o1, PlayerRank o2) {
                return Integer.compare(o1.rankLevel, o2.rankLevel);
            }
        });

        addTextFilter(new GUIListFilterText<PlayerRank>() {
            public boolean isOk(String s, PlayerRank playerRank) {
                return playerRank.rankName.toLowerCase().contains(s.toLowerCase());
            }
        }, "SEARCH BY NAME", ControllerElement.FilterRowStyle.LEFT);

        addDropdownFilter(new GUIListFilterDropdown<PlayerRank, PlayerRank.RankType>(PlayerRank.RankType.values()) {
            public boolean isOk(PlayerRank.RankType rankType, PlayerRank playerRank) {
                switch(rankType) {
                    case ALL:
                        return true;
                    case PLAYER:
                        return playerRank.rankType.equals(PlayerRank.RankType.PLAYER);
                    case DONATOR:
                        return playerRank.rankType.equals(PlayerRank.RankType.DONATOR);
                    case STAFF:
                        return playerRank.rankType.equals(PlayerRank.RankType.STAFF);
                }
                return true;
            }

        }, new CreateGUIElementInterface<PlayerRank.RankType>() {
            @Override
            public GUIElement create(PlayerRank.RankType rankType) {
                GUIAncor anchor = new GUIAncor(getState(), 10.0f, 24.0f);
                GUITextOverlayTableDropDown dropDown = new GUITextOverlayTableDropDown(10, 10, getState());
                dropDown.setTextSimple(rankType.toString());
                dropDown.setPos(4.0f, 4.0f, 0.0f);
                anchor.setUserPointer(rankType);
                anchor.attach(dropDown);
                return anchor;
            }

            @Override
            public GUIElement createNeutral() {
                return null;
            }
        }, ControllerElement.FilterRowStyle.RIGHT);

        activeSortColumnIndex = 0;
    }

    @Override
    public void updateListEntries(GUIElementList guiElementList, Set<PlayerRank> set) {
        guiElementList.deleteObservers();
        guiElementList.addObserver(this);
        PlayerRankListRow selected = null;

        for(PlayerRank playerRank : set) {
            GUITextOverlayTable nameTextElement;
            (nameTextElement = new GUITextOverlayTable(10, 10, getState())).setTextSimple(playerRank.rankName);
            GUIClippedRow nameRowElement;
            (nameRowElement = new GUIClippedRow(getState())).attach(nameTextElement);

            GUITextOverlayTable prefixTextElement;
            (prefixTextElement = new GUITextOverlayTable(10, 10, getState())).setTextSimple(playerRank.rankName);
            GUIClippedRow prefixRowElement;
            (prefixRowElement = new GUIClippedRow(getState())).attach(prefixTextElement);

            GUITextOverlayTable levelTextElement;
            (levelTextElement = new GUITextOverlayTable(10, 10, getState())).setTextSimple("Level " + playerRank.rankLevel);
            GUIClippedRow levelRowElement;
            (levelRowElement = new GUIClippedRow(getState())).attach(levelTextElement);

            PlayerRankListRow playerRankListRow = new PlayerRankListRow(getState(), playerRank, nameRowElement, prefixRowElement, levelRowElement);
            GUIAncor anchor = new GUIAncor(getState(), getWidth() - 4, 28.0f);
            anchor.attach(redrawButtonPane(playerRank, anchor));
            playerRankListRow.expanded = new GUIElementList(getState());
            playerRankListRow.expanded.add(new GUIListElement(anchor, getState()));
            playerRankListRow.expanded.attach(anchor);
            playerRankListRow.onInit();
            guiElementList.add(playerRankListRow);
            if(playerRankListRow.f.equals(playerData.getRank())) selected = playerRankListRow;
        }

        if(playerData.getRank() != null && selected != null) selected.setHighlighted(true);
        guiElementList.updateDim();
    }

    public GUIHorizontalButtonTablePane redrawButtonPane(final PlayerRank playerRank, GUIAncor anchor) {
        GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 1, 1, anchor);
        buttonPane.onInit();

        if(!playerData.getRank().equals(playerRank)) {
            buttonPane.addButton(0, 0, "SET RANK", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent event) {
                    if(event.pressedLeftMouse()) {
                        getState().getController().queueUIAudio("0022_menu_ui - enter");
                        playerData.setRank(playerRank);
                        ServerDatabase.updatePlayerData(playerData);
                        redraw();
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
        } else {
            if(playerRank.equals(ServerDatabase.getDefaultRank())) {
                buttonPane.addButton(0, 0, "CANNOT REMOVE DEFAULT", GUIHorizontalArea.HButtonType.BUTTON_GREY_LIGHT, new GUICallback() {
                    @Override
                    public void callback(GUIElement guiElement, MouseEvent event) {
                        if(event.pressedLeftMouse()) {
                            getState().getController().queueUIAudio("0022_menu_ui - error 1");
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
            } else {
                buttonPane.addButton(0, 0, "REMOVE RANK", GUIHorizontalArea.HButtonColor.ORANGE, new GUICallback() {
                    @Override
                    public void callback(GUIElement guiElement, MouseEvent event) {
                        if(event.pressedLeftMouse()) {
                            getState().getController().queueUIAudio("0022_menu_ui - cancel");
                            playerData.setRank(ServerDatabase.getDefaultRank());
                            ServerDatabase.updatePlayerData(playerData);
                            redraw();
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
            }
        }

        return buttonPane;
    }

    public void redraw() {
        flagDirty();
        handleDirty();
    }

    public class PlayerRankListRow extends ScrollableTableList<PlayerRank>.Row {

        public PlayerRankListRow(InputState inputState, PlayerRank playerRank, GUIElement... guiElements) {
            super(inputState, playerRank, guiElements);
            highlightSelect = true;
            highlightSelectSimple = true;
            setAllwaysOneSelected(true);
        }
    }
}

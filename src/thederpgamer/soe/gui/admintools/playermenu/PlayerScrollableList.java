package thederpgamer.soe.gui.admintools.playermenu;

import api.common.GameServer;
import org.schema.game.common.data.player.PlayerState;
import org.schema.schine.graphicsengine.forms.gui.GUIAncor;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUIElementList;
import org.schema.schine.graphicsengine.forms.gui.GUIListElement;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.soe.utils.PlayerUtils;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

/**
 * Player List for Admin Tools menu.
 *
 * @author TheDerpGamer
 * @since 06/27/2021
 */
public class PlayerScrollableList extends ScrollableTableList<PlayerState> implements GUIActiveInterface {

    public PlayerScrollableList(InputState state, float width, float height, GUIElement contentPane) {
        super(state, width, height, contentPane);
    }

    @Override
    public void initColumns() {
        activeSortColumnIndex = 0;

        addColumn("Name", 10.0f, new Comparator<PlayerState>() {
            @Override
            public int compare(PlayerState o1, PlayerState o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        addColumn("Faction", 10.0f, new Comparator<PlayerState>() {
            @Override
            public int compare(PlayerState o1, PlayerState o2) {
                return o1.getFactionName().compareTo(o2.getFactionName());
            }
        });

        addColumn("Sector", 5.0f, new Comparator<PlayerState>() {
            @Override
            public int compare(PlayerState o1, PlayerState o2) {
                return o1.getCurrentSector().compareTo(o2.getCurrentSector());
            }
        });

        addColumn("Type", 7.0f, new Comparator<PlayerState>() {
            @Override
            public int compare(PlayerState o1, PlayerState o2) {
                return PlayerUtils.getPlayerType(o1).compareTo(PlayerUtils.getPlayerType(o2));
            }
        });

        addTextFilter(new GUIListFilterText<PlayerState>() {
            @Override
            public boolean isOk(String s, PlayerState playerState) {
                return playerState.getName().toLowerCase().contains(s.toLowerCase());
            }
        }, "SEARCH BY NAME", ControllerElement.FilterRowStyle.LEFT);

        addTextFilter(new GUIListFilterText<PlayerState>() {
            @Override
            public boolean isOk(String s, PlayerState playerState) {
                return playerState.getFactionName().toLowerCase().contains(s.toLowerCase());
            }
        }, "SEARCH BY FACTION", ControllerElement.FilterRowStyle.RIGHT);
    }

    @Override
    public Collection<PlayerState> getElementList() {
        return GameServer.getServerState().getPlayerStatesByName().values();
    }

    @Override
    public void updateListEntries(GUIElementList guiElementList, Set<PlayerState> set) {
        guiElementList.deleteObservers();
        guiElementList.addObserver(this);
        for(PlayerState playerState : set) {
            GUITextOverlayTable nameTextElement;
            (nameTextElement = new GUITextOverlayTable(10, 10, this.getState())).setTextSimple(playerState.getName());
            GUIClippedRow nameRowElement;
            (nameRowElement = new GUIClippedRow(this.getState())).attach(nameTextElement);


            final PlayerListRow playerListRow = new PlayerListRow(this.getState(), playerState, nameRowElement);
            GUIAncor anchor = new GUIAncor(getState(), getWidth() - 49.0f, 28.0f);
            anchor.attach(redrawButtonPane(playerState, anchor));
            playerListRow.expanded = new GUIElementList(getState());
            playerListRow.expanded.add(new GUIListElement(anchor, getState()));
            playerListRow.expanded.attach(anchor);
            playerListRow.onInit();
            guiElementList.add(playerListRow);
        }
        guiElementList.updateDim();
    }

    public GUIHorizontalButtonTablePane redrawButtonPane(PlayerState playerState, GUIAncor anchor) {
        GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 1, 1, anchor);
        buttonPane.onInit();

        //Todo: Player actions button pane

        return buttonPane;
    }

    public class PlayerListRow extends ScrollableTableList<PlayerState>.Row {

        public PlayerListRow(InputState inputState, PlayerState playerState, GUIElement... guiElements) {
            super(inputState, playerState, guiElements);
            this.highlightSelect = true;
            this.highlightSelectSimple = true;
            this.setAllwaysOneSelected(true);
        }
    }
}

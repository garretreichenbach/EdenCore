package thederpgamer.edencore.gui.buildtools.buildsector;

import api.common.GameClient;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.ControllerStateUnit;
import org.schema.game.common.data.player.PlayerState;
import org.schema.schine.graphicsengine.forms.gui.GUIAncor;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUIElementList;
import org.schema.schine.graphicsengine.forms.gui.GUIListElement;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.BuildSectorData;
import thederpgamer.edencore.utils.DataUtils;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/02/2021
 */
public class BuildSectorPlayerList extends ScrollableTableList<PlayerState> {

    private BuildSectorData sectorData;

    public BuildSectorPlayerList(InputState state, float width, float height, GUIElement contentPane) {
        super(state, width, height, contentPane);
        sectorData = DataUtils.getBuildSector(GameClient.getClientPlayerState());
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

        addColumn("Entity", 15.0f, new Comparator<PlayerState>() {
            @Override
            public int compare(PlayerState o1, PlayerState o2) {
                return getEntityString(o1).compareTo(getEntityString(o2));
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
    protected Collection<PlayerState> getElementList() {
        return DataUtils.getPlayersInBuildSector(sectorData);
    }

    @Override
    public void updateListEntries(GUIElementList guiElementList, Set<PlayerState> set) {
        sectorData = DataUtils.getBuildSector(GameClient.getClientPlayerState());

        guiElementList.deleteObservers();
        guiElementList.addObserver(this);

        for(PlayerState playerState : set) {
            GUITextOverlayTable nameTextElement;
            (nameTextElement = new GUITextOverlayTable(10, 10, getState())).setTextSimple(playerState.getName());
            GUIClippedRow nameRowElement;
            (nameRowElement = new GUIClippedRow(getState())).attach(nameTextElement);

            GUITextOverlayTable factionTextElement;
            (factionTextElement = new GUITextOverlayTable(10, 10, getState())).setTextSimple(playerState.getFactionName());
            GUIClippedRow factionRowElement;
            (factionRowElement = new GUIClippedRow(getState())).attach(factionTextElement);

            GUITextOverlayTable entityTextElement;
            (entityTextElement = new GUITextOverlayTable(10, 10, getState())).setTextSimple(getEntityString(playerState));
            GUIClippedRow entityRowElement;
            (entityRowElement = new GUIClippedRow(getState())).attach(entityTextElement);

            final PlayerListRow playerListRow = new PlayerListRow(getState(), playerState, nameRowElement, factionRowElement, entityRowElement);
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

        int pos = 0;
        //Todo: Player actions button pane


        return buttonPane;
    }

    private String getEntityString(PlayerState player) {
        SegmentController control = getCurrentControl(player);
        if(control != null) return control.getName();
        else return "NONE";
    }

    private SegmentController getCurrentControl(PlayerState player) {
        Set<ControllerStateUnit> units = player.getControllerState().getUnits();
        if(units.isEmpty()) return null;
        ControllerStateUnit unit = units.iterator().next();
        return (SegmentController) unit.playerControllable;
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

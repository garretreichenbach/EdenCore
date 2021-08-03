package thederpgamer.edencore.gui.buildtools.buildsector;

import api.common.GameClient;
import api.common.GameCommon;
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
public class BuildSectorEntityList extends ScrollableTableList<SegmentController> {

    private BuildSectorData sectorData;

    public BuildSectorEntityList(InputState state, float width, float height, GUIElement contentPane) {
        super(state, width, height, contentPane);
        sectorData = DataUtils.getBuildSector(GameClient.getClientPlayerState());
    }

    @Override
    public void initColumns() {
        activeSortColumnIndex = 0;

        addColumn("Name", 10.0f, new Comparator<SegmentController>() {
            @Override
            public int compare(SegmentController o1, SegmentController o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        addColumn("Faction", 10.0f, new Comparator<SegmentController>() {
            @Override
            public int compare(SegmentController o1, SegmentController o2) {
                return getFactionName(o1.getFactionId()).compareTo(getFactionName(o2.getFactionId()));
            }
        });

        addTextFilter(new GUIListFilterText<SegmentController>() {
            @Override
            public boolean isOk(String s, SegmentController entity) {
                return entity.getName().toLowerCase().contains(s.toLowerCase());
            }
        }, "SEARCH BY NAME", ControllerElement.FilterRowStyle.LEFT);

        addTextFilter(new GUIListFilterText<SegmentController>() {
            @Override
            public boolean isOk(String s, SegmentController entity) {
                return getFactionName(entity.getFactionId()).toLowerCase().contains(s.toLowerCase());
            }
        }, "SEARCH BY FACTION", ControllerElement.FilterRowStyle.RIGHT);
    }

    @Override
    protected Collection<SegmentController> getElementList() {
        return DataUtils.getEntitiesInBuildSector(sectorData);
    }

    @Override
    public void updateListEntries(GUIElementList guiElementList, Set<SegmentController> set) {
        sectorData = DataUtils.getBuildSector(GameClient.getClientPlayerState());

        guiElementList.deleteObservers();
        guiElementList.addObserver(this);

        for(SegmentController entity : set) {
            GUITextOverlayTable nameTextElement;
            (nameTextElement = new GUITextOverlayTable(10, 10, getState())).setTextSimple(entity.getName());
            GUIClippedRow nameRowElement;
            (nameRowElement = new GUIClippedRow(getState())).attach(nameTextElement);

            GUITextOverlayTable factionTextElement;
            (factionTextElement = new GUITextOverlayTable(10, 10, getState())).setTextSimple(getFactionName(entity.getFactionId()));
            GUIClippedRow factionRowElement;
            (factionRowElement = new GUIClippedRow(getState())).attach(factionTextElement);

            final EntityListRow entityListRow = new EntityListRow(getState(), entity, nameRowElement, factionRowElement);
            GUIAncor anchor = new GUIAncor(getState(), getWidth() - 49.0f, 28.0f);
            anchor.attach(redrawButtonPane(entity, anchor));
            entityListRow.expanded = new GUIElementList(getState());
            entityListRow.expanded.add(new GUIListElement(anchor, getState()));
            entityListRow.expanded.attach(anchor);
            entityListRow.onInit();
            guiElementList.add(entityListRow);
        }
        guiElementList.updateDim();
    }

    private String getFactionName(int factionId) {
        if(factionId == 0) return "NO FACTION";
        else return GameCommon.getGameState().getFactionManager().getFaction(factionId).getName();
    }

    public GUIHorizontalButtonTablePane redrawButtonPane(SegmentController entity, GUIAncor anchor) {
        GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 1, 1, anchor);
        buttonPane.onInit();

        //Todo: Player actions button pane

        return buttonPane;
    }

    private SegmentController getCurrentControl(PlayerState player) {
        Set<ControllerStateUnit> units = player.getControllerState().getUnits();
        if(units.isEmpty()) return null;
        ControllerStateUnit unit = units.iterator().next();
        return (SegmentController) unit.playerControllable;
    }

    public class EntityListRow extends ScrollableTableList<SegmentController>.Row {

        public EntityListRow(InputState inputState, SegmentController entity, GUIElement... guiElements) {
            super(inputState, entity, guiElements);
            this.highlightSelect = true;
            this.highlightSelectSimple = true;
            this.setAllwaysOneSelected(true);
        }
    }
}
package thederpgamer.edencore.gui.buildsectormenu;

import api.common.GameClient;
import org.schema.schine.graphicsengine.core.GLFrame;
import org.schema.schine.graphicsengine.forms.gui.GUIAncor;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUIElementList;
import org.schema.schine.graphicsengine.forms.gui.GUIListElement;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.utils.DataUtils;

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

    private final BuildSectorMenuPanel panel;
    private final BuildSectorData sectorData;

    public BuildSectorUserScrollableList(InputState state, GUIElement p, BuildSectorMenuPanel panel) {
        super(state, (float) GLFrame.getWidth() / 2, (float) GLFrame.getHeight() / 2, p);
        this.panel = panel;
        this.sectorData = DataUtils.getBuildSector(GameClient.getClientPlayerState().getName());
        p.attach(this);
    }

    private GUIHorizontalButtonTablePane redrawButtonPane(final String playerName, GUIAncor anchor) {
        GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 2, 1, anchor);
        buttonPane.onInit();

        return buttonPane;
    }

    @Override
    protected Collection<String> getElementList() {
        return sectorData.permissions.keySet();
    }

    @Override
    public void initColumns() {
        addColumn("Name", 15.0f, new Comparator<String>() {
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
                GUITextOverlayTable nameTextElement;
                (nameTextElement = new GUITextOverlayTable(10, 10, this.getState())).setTextSimple(playerName);
                GUIClippedRow nameRowElement;
                (nameRowElement = new GUIClippedRow(this.getState())).attach(nameTextElement);

                BuildSectorUserScrollableListRow listRow = new BuildSectorUserScrollableListRow(getState(), playerName, nameRowElement);
                GUIAncor anchor = new GUIAncor(getState(), (float) GLFrame.getWidth() / 2.5f, 28.0f);
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
    }
}

package thederpgamer.edencore.gui.buildsectormenu;

import api.common.GameClient;
import api.network.packets.PacketUtil;
import org.schema.schine.graphicsengine.core.GLFrame;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.manager.ClientCacheManager;
import thederpgamer.edencore.network.client.RequestMoveFromBuildSectorPacket;
import thederpgamer.edencore.network.client.RequestMoveToBuildSectorPacket;
import thederpgamer.edencore.utils.DataUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/22/2021]
 */
public class BuildSectorScrollableList extends ScrollableTableList<BuildSectorData> {

    private final BuildSectorMenuPanel panel;

    public BuildSectorScrollableList(InputState state, GUIElement p, BuildSectorMenuPanel panel) {
        super(state, (float) GLFrame.getWidth() / 1.5f, (float) GLFrame.getHeight() / 2.0f, p);
        this.panel = panel;
        p.attach(this);
    }

    private GUIHorizontalButtonTablePane redrawButtonPane(final BuildSectorData sectorData, GUIAncor anchor) {
        GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 2, 1, anchor);
        buttonPane.onInit();
        final boolean inSector = DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState());

        buttonPane.addButton(0, 0, "ENTER SECTOR", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if(mouseEvent.pressedLeftMouse() && sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "ENTER") && !inSector) {
                    PacketUtil.sendPacketToServer(new RequestMoveToBuildSectorPacket(sectorData));
                    panel.recreateTabs();
                }
            }

            @Override
            public boolean isOccluded() {
                return inSector;
            }
        }, new GUIActivationCallback() {
            @Override
            public boolean isVisible(InputState inputState) {
                return true;
            }

            @Override
            public boolean isActive(InputState inputState) {
                return !inSector;
            }
        });

        buttonPane.addButton(1, 0, "LEAVE SECTOR", GUIHorizontalArea.HButtonColor.ORANGE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if(mouseEvent.pressedLeftMouse() && inSector) {
                    PacketUtil.sendPacketToServer(new RequestMoveFromBuildSectorPacket());
                    panel.recreateTabs();
                }
            }

            @Override
            public boolean isOccluded() {
                return !inSector;
            }
        }, new GUIActivationCallback() {
            @Override
            public boolean isVisible(InputState inputState) {
                return true;
            }

            @Override
            public boolean isActive(InputState inputState) {
                return inSector;
            }
        });
        return buttonPane;
    }

    @Override
    protected Collection<BuildSectorData> getElementList() {
        return ClientCacheManager.accessibleSectors;
    }

    @Override
    public void initColumns() {
        addColumn("Owner", 15.0f, new Comparator<BuildSectorData>() {
            @Override
            public int compare(BuildSectorData o1, BuildSectorData o2) {
                return o1.ownerName.compareTo(o2.ownerName);
            }
        });

        /*
        addColumn("Type", 10.0f, new Comparator<BuildSectorData>() {
            @Override
            public int compare(BuildSectorData o1, BuildSectorData o2) {
                return 0;
            }
        });
         */

        addTextFilter(new GUIListFilterText<BuildSectorData>() {
            @Override
            public boolean isOk(String s, BuildSectorData buildSectorData) {
                return buildSectorData.ownerName.toLowerCase().contains(s.toLowerCase());
            }
        }, "SEARCH BY OWNER", ControllerElement.FilterRowStyle.FULL);
    }

    @Override
    public void updateListEntries(GUIElementList guiElementList, Set<BuildSectorData> set) {
        guiElementList.deleteObservers();
        guiElementList.addObserver(this);
        for(BuildSectorData sectorData : set) {
            if(sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "ENTER")) {
                GUITextOverlayTable ownerTextElement;
                (ownerTextElement = new GUITextOverlayTable(10, 10, this.getState())).setTextSimple(sectorData.ownerName);
                GUIClippedRow ownerRowElement;
                (ownerRowElement = new GUIClippedRow(this.getState())).attach(ownerTextElement);

                BuildSectorScrollableListRow listRow = new BuildSectorScrollableListRow(getState(), sectorData, ownerRowElement);
                GUIAncor anchor = new GUIAncor(getState(), (float) GLFrame.getWidth() / 2.5f, 28.0f);
                anchor.attach(redrawButtonPane(sectorData, anchor));
                listRow.expanded = new GUIElementList(getState());
                listRow.expanded.add(new GUIListElement(anchor, getState()));
                listRow.expanded.attach(anchor);
                listRow.onInit();
                guiElementList.addWithoutUpdate(listRow);
            }
        }
        guiElementList.updateDim();
    }

    public class BuildSectorScrollableListRow extends ScrollableTableList<BuildSectorData>.Row {

        public BuildSectorScrollableListRow(InputState state, BuildSectorData sectorData, GUIElement... elements) {
            super(state, sectorData, elements);
            this.highlightSelect = true;
            this.highlightSelectSimple = true;
            this.setAllwaysOneSelected(true);
        }
    }
}

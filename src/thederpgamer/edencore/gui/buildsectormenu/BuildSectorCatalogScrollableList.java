package thederpgamer.edencore.gui.buildsectormenu;

import api.common.GameClient;
import api.network.packets.PacketUtil;
import org.schema.common.util.StringTools;
import org.schema.game.common.data.player.catalog.CatalogPermission;
import org.schema.game.server.data.blueprintnw.BlueprintType;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.network.client.RequestSpawnEntryPacket;
import thederpgamer.edencore.utils.DataUtils;

import java.util.*;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/29/2021]
 */
public class BuildSectorCatalogScrollableList extends ScrollableTableList<CatalogPermission> {

    private final BuildSectorMenuPanel panel;
    private final BuildSectorData sectorData;

    public BuildSectorCatalogScrollableList(InputState state, BuildSectorData sectorData, GUIElement p, BuildSectorMenuPanel panel) {
        super(state, 800, 500, p);
        this.panel = panel;
        this.sectorData = sectorData;
        p.attach(this);
    }

    private GUIHorizontalButtonTablePane redrawButtonPane(final CatalogPermission catalogPermission, GUIAncor anchor) {
        GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 3, 1, anchor);
        buttonPane.onInit();

        buttonPane.addButton(0, 0, "SPAWN", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if(mouseEvent.pressedLeftMouse()) {
                    if(sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "SPAWN")) {
                        getState().getController().queueUIAudio("0022_menu_ui - select 1");
                        PacketUtil.sendPacketToServer(new RequestSpawnEntryPacket(catalogPermission.getUid(), false, false));
                    } else getState().getController().queueUIAudio("0022_menu_ui - error 1");
                }
            }

            @Override
            public boolean isOccluded() {
                return !getState().getController().getPlayerInputs().isEmpty() || !sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "SPAWN");
            }
        }, new GUIActivationCallback() {
            @Override
            public boolean isVisible(InputState inputState) {
                return true;
            }

            @Override
            public boolean isActive(InputState inputState) {
                return getState().getController().getPlayerInputs().isEmpty() && sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "SPAWN");
            }
        });

        buttonPane.addButton(1, 0, "SPAWN ON DOCK", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if(mouseEvent.pressedLeftMouse()) {
                    if(sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "SPAWN")) {
                        getState().getController().queueUIAudio("0022_menu_ui - select 2");
                        PacketUtil.sendPacketToServer(new RequestSpawnEntryPacket(catalogPermission.getUid(), true, false));
                    } else getState().getController().queueUIAudio("0022_menu_ui - error 1");
                }
            }

            @Override
            public boolean isOccluded() {
                return !getState().getController().getPlayerInputs().isEmpty() || !sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "SPAWN");
            }
        }, new GUIActivationCallback() {
            @Override
            public boolean isVisible(InputState inputState) {
                return true;
            }

            @Override
            public boolean isActive(InputState inputState) {
                return getState().getController().getPlayerInputs().isEmpty() && sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "SPAWN");
            }
        });

        buttonPane.addButton(2, 0, "SPAWN ENEMY", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if(mouseEvent.pressedLeftMouse()) {
                    if(sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "SPAWN_ENEMIES")) {
                        getState().getController().queueUIAudio("0022_menu_ui - select 3");
                        PacketUtil.sendPacketToServer(new RequestSpawnEntryPacket(catalogPermission.getUid(), false, true));
                    } else getState().getController().queueUIAudio("0022_menu_ui - error 1");
                }
            }

            @Override
            public boolean isOccluded() {
                return !getState().getController().getPlayerInputs().isEmpty() || !sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "SPAWN_ENEMIES");
            }
        }, new GUIActivationCallback() {
            @Override
            public boolean isVisible(InputState inputState) {
                return true;
            }

            @Override
            public boolean isActive(InputState inputState) {
                return getState().getController().getPlayerInputs().isEmpty() && sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "SPAWN_ENEMIES");
            }
        });

        return buttonPane;
    }

    @Override
    protected Collection<CatalogPermission> getElementList() {
        return GameClient.getClientPlayerState().getCatalog().getAvailableCatalog();
    }

    @Override
    public void initColumns() {
        addColumn(Lng.str("Name"), 15.0f, new Comparator<CatalogPermission>() {
            @Override
            public int compare(CatalogPermission o1, CatalogPermission o2) {
                return o1.getUid().compareTo(o2.getUid());
            }
        });

        addColumn(Lng.str("Type"), 7.0f, new Comparator<CatalogPermission>() {
            @Override
            public int compare(CatalogPermission o1, CatalogPermission o2) {
                return o1.type.name().compareTo(o2.type.name());
            }
        });

        addColumn(Lng.str("Class"), 8.0f, new Comparator<CatalogPermission>() {
            @Override
            public int compare(CatalogPermission o1, CatalogPermission o2) {
                return o1.getClassification().ordinal() - o2.getClassification().ordinal();
            }
        });

        addColumn(Lng.str("Mass"), 7.5f, new Comparator<CatalogPermission>() {
            @Override
            public int compare(CatalogPermission o1, CatalogPermission o2) {
                return Float.compare(o1.mass, o2.mass);
            }
        });

        addTextFilter(new GUIListFilterText<CatalogPermission>() {
            @Override
            public boolean isOk(String input, CatalogPermission listElement) {
                return listElement.getUid().toLowerCase().contains(input.toLowerCase());
            }
        }, Lng.str("SEARCH BY NAME"), ControllerElement.FilterRowStyle.LEFT);

        addDropdownFilter(new GUIListFilterDropdown<CatalogPermission, BlueprintType>(BlueprintType.SHIP, BlueprintType.SPACE_STATION) {

            @Override
            public boolean isOk(BlueprintType input, CatalogPermission f) {
                return f.type == input;
            }
        }, new CreateGUIElementInterface<BlueprintType>() {
            @Override
            public GUIElement create(BlueprintType o) {
                GUIAncor c = new GUIAncor(getState(), 10, 20);
                GUITextOverlayTableDropDown a = new GUITextOverlayTableDropDown(10, 10, getState());
                a.setTextSimple(o.name().toUpperCase().replaceAll("_", " "));
                a.setPos(4, 4, 0);
                c.setUserPointer(o);
                c.attach(a);
                return c;
            }

            @Override
            public GUIElement createNeutral() {
                GUIAncor c = new GUIAncor(getState(), 10, 20);
                GUITextOverlayTableDropDown a = new GUITextOverlayTableDropDown(10, 10, getState());
                a.setTextSimple("ALL");
                a.setPos(4, 4, 0);
                c.attach(a);
                return c;
            }
        }, ControllerElement.FilterRowStyle.RIGHT);
    }

    @Override
    public void updateListEntries(GUIElementList guiElementList, Set<CatalogPermission> set) {
        guiElementList.deleteObservers();
        guiElementList.addObserver(this);
        if(DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
            for(CatalogPermission catalogPermission : set) {
                GUITextOverlayTable nameTextElement;
                (nameTextElement = new GUITextOverlayTable(10, 10, this.getState())).setTextSimple(catalogPermission.getUid());
                GUIClippedRow nameRowElement;
                (nameRowElement = new GUIClippedRow(this.getState())).attach(nameTextElement);

                GUITextOverlayTable typeTextElement;
                (typeTextElement = new GUITextOverlayTable(10, 10, this.getState())).setTextSimple(catalogPermission.type.name().replaceAll("_", " "));
                GUIClippedRow typeRowElement;
                (typeRowElement = new GUIClippedRow(this.getState())).attach(typeTextElement);

                GUITextOverlayTable classTextElement;
                (classTextElement = new GUITextOverlayTable(10, 10, this.getState())).setTextSimple(catalogPermission.getClassification().getName());
                GUIClippedRow classRowElement;
                (classRowElement = new GUIClippedRow(this.getState())).attach(classTextElement);

                GUITextOverlayTable massTextElement;
                (massTextElement = new GUITextOverlayTable(10, 10, this.getState())).setTextSimple(StringTools.massFormat(catalogPermission.mass));
                GUIClippedRow massRowElement;
                (massRowElement = new GUIClippedRow(this.getState())).attach(massTextElement);

                BuildSectorCatalogListRow listRow = new BuildSectorCatalogListRow(getState(), catalogPermission, nameRowElement, typeRowElement, classRowElement, massRowElement);
                GUIAncor anchor = new GUIAncor(getState(), 1160, 28.0f);
                anchor.attach(redrawButtonPane(catalogPermission, anchor));
                listRow.expanded = new GUIElementList(getState());
                listRow.expanded.add(new GUIListElement(anchor, getState()));
                listRow.expanded.attach(anchor);
                listRow.onInit();
                guiElementList.addWithoutUpdate(listRow);
            }
        }
        guiElementList.updateDim();
    }

    public class BuildSectorCatalogListRow extends ScrollableTableList<CatalogPermission>.Row {

        public BuildSectorCatalogListRow(InputState state, CatalogPermission catalogPermission, GUIElement... elements) {
            super(state, catalogPermission, elements);
            this.highlightSelect = true;
            this.highlightSelectSimple = true;
            this.setAllwaysOneSelected(true);
        }
    }
}

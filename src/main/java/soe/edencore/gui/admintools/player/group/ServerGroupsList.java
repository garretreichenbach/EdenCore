package soe.edencore.gui.admintools.player.group;

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
 * ServerGroupsList.java
 * <Description>
 *
 * @author TheDerpGamer
 * @since 03/22/2021
 */
public class ServerGroupsList extends ScrollableTableList<PermissionGroup> {

    private PlayerData playerData;

    public ServerGroupsList(InputState inputState, GUIElement guiElement, PlayerData playerData, int width) {
        super(inputState, width, 300, guiElement);
        this.playerData = playerData;
        ServerDatabase.guiLists.add(this);
    }

    @Override
    public ArrayList<PermissionGroup> getElementList() {
        ArrayList<PermissionGroup> groupsList = new ArrayList<>();
        for(PermissionGroup group : ServerDatabase.getAllGroups()) {
            if(!group.getMembers().contains(playerData)) groupsList.add(group);
        }
        return groupsList;
    }

    @Override
    public void initColumns() {
        new StringComparator();

        addColumn("Group Name", 10.0f, new Comparator<PermissionGroup>() {
            @Override
            public int compare(PermissionGroup o1, PermissionGroup o2) {
                return o1.getGroupName().compareTo(o2.getGroupName());
            }
        });

        addColumn("Permissions", 12.0f, new Comparator<PermissionGroup>() {
            @Override
            public int compare(PermissionGroup o1, PermissionGroup o2) {
                return Integer.compare(o1.getPermissions().size(), o2.getPermissions().size());
            }
        });

        addColumn("Inheritances", 12.0f, new Comparator<PermissionGroup>() {
            @Override
            public int compare(PermissionGroup o1, PermissionGroup o2) {
                return Integer.compare(o1.getInheritedGroups().size(), o2.getInheritedGroups().size());
            }
        });

        addColumn("Members", 8.5f, new Comparator<PermissionGroup>() {
            @Override
            public int compare(PermissionGroup o1, PermissionGroup o2) {
                return Integer.compare(o1.getMembers().size(), o2.getMembers().size());
            }
        });

        addTextFilter(new GUIListFilterText<PermissionGroup>() {
            public boolean isOk(String s, PermissionGroup group) {
                return group.getGroupName().toLowerCase().contains(s.toLowerCase());
            }
        }, "SEARCH BY NAME", ControllerElement.FilterRowStyle.LEFT);

        addTextFilter(new GUIListFilterText<PermissionGroup>() {
            public boolean isOk(String s, PermissionGroup group) {
                return group.getPermissions().contains(s.toLowerCase());
            }
        }, "SEARCH BY PERMISSION", ControllerElement.FilterRowStyle.RIGHT);

        activeSortColumnIndex = 0;
    }

    @Override
    public void updateListEntries(GUIElementList guiElementList, Set<PermissionGroup> set) {
        guiElementList.deleteObservers();
        guiElementList.addObserver(this);

        for(PermissionGroup group : set) {
            GUITextOverlayTable nameTextElement;
            (nameTextElement = new GUITextOverlayTable(10, 10, getState())).setTextSimple(group.getGroupName());
            GUIClippedRow nameRowElement;
            (nameRowElement = new GUIClippedRow(getState())).attach(nameTextElement);

            GUITextOverlayTable permsTextElement;
            (permsTextElement = new GUITextOverlayTable(10, 10, getState())).setTextSimple(group.getPermissions().size() + " Permissions");
            GUIClippedRow permsRowElement;
            (permsRowElement = new GUIClippedRow(getState())).attach(permsTextElement);

            GUITextOverlayTable inheritedTextElement;
            (inheritedTextElement = new GUITextOverlayTable(10, 10, getState())).setTextSimple(group.getInheritedGroups().size() + " Inheritances");
            GUIClippedRow inheritedRowElement;
            (inheritedRowElement = new GUIClippedRow(getState())).attach(inheritedTextElement);

            GUITextOverlayTable membersTextElement;
            (membersTextElement = new GUITextOverlayTable(10, 10, getState())).setTextSimple(group.getMembers().size() + " Members");
            GUIClippedRow membersRowElement;
            (membersRowElement = new GUIClippedRow(getState())).attach(membersTextElement);

            ServerGroupsListRow serverGroupsListRow = new ServerGroupsListRow(getState(), group, nameRowElement, permsRowElement, inheritedRowElement, membersRowElement);
            GUIAncor anchor = new GUIAncor(getState(), getWidth() - 4, 28.0f);
            anchor.attach(redrawButtonPane(group, anchor));
            serverGroupsListRow.expanded = new GUIElementList(getState());
            serverGroupsListRow.expanded.add(new GUIListElement(anchor, getState()));
            serverGroupsListRow.expanded.attach(anchor);
            serverGroupsListRow.onInit();
            guiElementList.add(serverGroupsListRow);
        }
        guiElementList.updateDim();
    }

    public GUIHorizontalButtonTablePane redrawButtonPane(final PermissionGroup group, GUIAncor anchor) {
        GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 1, 1, anchor);
        buttonPane.onInit();

        buttonPane.addButton(0, 0, "ADD TO GROUP", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent event) {
                if(event.pressedLeftMouse()) {
                    playerData.getGroups().add(group);
                    group.getMembers().add(playerData);
                    ServerDatabase.updatePlayerData(playerData);
                    ServerDatabase.updateGroup(group);
                    ServerDatabase.updateGUIs();
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

        buttonPane.addButton(1, 0, "EDIT GROUP", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent event) {
                if(event.pressedLeftMouse()) {
                    if(EdenCore.instance.groupEditorControlManager == null) {
                        EdenCore.instance.groupEditorControlManager = new GroupEditorControlManager(GameClient.getClientState());
                        ControlManagerHandler.registerNewControlManager(EdenCore.instance.getSkeleton(), EdenCore.instance.groupEditorControlManager);
                    }
                    EdenCore.instance.groupEditorControlManager.group = group;
                    EdenCore.instance.groupEditorControlManager.setActive(true);
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

    public class ServerGroupsListRow extends ScrollableTableList<PermissionGroup>.Row {

        public ServerGroupsListRow(InputState inputState, PermissionGroup group, GUIElement... guiElements) {
            super(inputState, group, guiElements);
            highlightSelect = true;
            highlightSelectSimple = true;
            setAllwaysOneSelected(true);
        }
    }
}

package soe.edencore.gui.admintools.player.group;

import org.hsqldb.lib.StringComparator;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import soe.edencore.data.player.PlayerData;
import soe.edencore.server.ServerDatabase;
import soe.edencore.server.permissions.PermissionGroup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

/**
 * PlayerGroupsList.java
 * <Description>
 *
 * @author TheDerpGamer
 * @since 03/22/2021
 */
public class PlayerGroupsList extends ScrollableTableList<PermissionGroup> {

    private PlayerData playerData;

    public PlayerGroupsList(InputState inputState, GUIElement guiElement, PlayerData playerData, int width) {
        super(inputState, width, 300, guiElement);
        this.playerData = playerData;
        ServerDatabase.guiLists.add(this);
    }

    @Override
    public ArrayList<PermissionGroup> getElementList() {
        return playerData.getGroups();
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

            PlayerGroupsListRow playerGroupsListRow = new PlayerGroupsListRow(getState(), group, nameRowElement, permsRowElement, inheritedRowElement, membersRowElement);
            GUIAncor anchor = new GUIAncor(getState(), getWidth() - 12, 28.0f);
            anchor.attach(redrawButtonPane(group, anchor));
            playerGroupsListRow.expanded = new GUIElementList(getState());
            playerGroupsListRow.expanded.add(new GUIListElement(anchor, getState()));
            playerGroupsListRow.expanded.attach(anchor);
            playerGroupsListRow.onInit();
            guiElementList.add(playerGroupsListRow);
        }
        guiElementList.updateDim();
    }

    public GUIHorizontalButtonTablePane redrawButtonPane(final PermissionGroup group, GUIAncor anchor) {
        GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 1, 1, anchor);
        buttonPane.onInit();

        buttonPane.addButton(0, 0, "REMOVE FROM GROUP", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent event) {
                if(event.pressedLeftMouse()) {
                    playerData.getGroups().remove(group);
                    group.getMembers().remove(playerData);
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

        return buttonPane;
    }

    public class PlayerGroupsListRow extends ScrollableTableList<PermissionGroup>.Row {

        public PlayerGroupsListRow(InputState inputState, PermissionGroup group, GUIElement... guiElements) {
            super(inputState, group, guiElements);
            highlightSelect = true;
            highlightSelectSimple = true;
            setAllwaysOneSelected(true);
        }
    }
}

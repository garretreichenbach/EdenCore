package soe.edencore.gui.admintools.player.permission;

import org.hsqldb.lib.StringComparator;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUIElementList;
import org.schema.schine.graphicsengine.forms.gui.newgui.ControllerElement;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIListFilterText;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUITextOverlayTable;
import org.schema.schine.graphicsengine.forms.gui.newgui.ScrollableTableList;
import org.schema.schine.input.InputState;
import soe.edencore.server.ServerDatabase;
import soe.edencore.server.permissions.PermissionGroup;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

/**
 * GroupPermissionList.java
 * <Description>
 *
 * @author TheDerpGamer
 * @since 03/22/2021
 */
public class GroupPermissionList extends ScrollableTableList<String> {

    private PermissionGroup group;

    public GroupPermissionList(InputState inputState, GUIElement guiElement, PermissionGroup group) {
        super(inputState, 739, 300, guiElement);
        this.group = group;
        ServerDatabase.guiLists.add(this);
    }

    @Override
    public ArrayList<String> getElementList() {
        return group.getPermissions();
    }

    @Override
    public void initColumns() {
        new StringComparator();

        addColumn("Permission Node", 15.0f, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        addTextFilter(new GUIListFilterText<String>() {
            public boolean isOk(String s, String permission) {
                return permission.toLowerCase().contains(s.toLowerCase());
            }
        }, ControllerElement.FilterRowStyle.FULL);

        activeSortColumnIndex = 0;
    }

    @Override
    public void updateListEntries(GUIElementList guiElementList, Set<String> set) {
        guiElementList.deleteObservers();
        guiElementList.addObserver(this);

        for(String permission : set) {
            GUITextOverlayTable permissionTextElement;
            (permissionTextElement = new GUITextOverlayTable(10, 10, getState())).setTextSimple(permission);
            GUIClippedRow permissionRowElement;
            (permissionRowElement = new GUIClippedRow(getState())).attach(permissionTextElement);

            GroupPermissionListRow groupPermissionListRow = new GroupPermissionListRow(getState(), permission, permissionRowElement);
            groupPermissionListRow.onInit();
            guiElementList.add(groupPermissionListRow);
        }
        guiElementList.updateDim();
    }

    public class GroupPermissionListRow extends ScrollableTableList<String>.Row {

        public GroupPermissionListRow(InputState inputState, String permission, GUIElement... guiElements) {
            super(inputState, permission, guiElements);
            highlightSelect = true;
            highlightSelectSimple = true;
            setAllwaysOneSelected(true);
        }
    }
}

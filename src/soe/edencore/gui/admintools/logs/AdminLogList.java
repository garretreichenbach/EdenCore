package soe.edencore.gui.admintools.logs;

import org.hsqldb.lib.StringComparator;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import soe.edencore.data.logs.AdminLogEntry;
import soe.edencore.server.ServerDatabase;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

/**
 * AdminLogList.java
 * <Description>
 *
 * @author TheDerpGamer
 * @since 03/20/2021
 */
public class AdminLogList extends ScrollableTableList<AdminLogEntry> {

    public static AdminLogList instance;

    public AdminLogList(InputState inputState, GUIElement guiElement) {
        super(inputState, 739, 300, guiElement);
        instance = this;
    }

    @Override
    public ArrayList<AdminLogEntry> getElementList() {
        instance = this;
        return ServerDatabase.getAdminLog();
    }

    @Override
    public void initColumns() {
        new StringComparator();

        addColumn("Date", 6.5f, new Comparator<AdminLogEntry>() {
            public int compare(AdminLogEntry o1, AdminLogEntry o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        addColumn("Type", 6.5f, new Comparator<AdminLogEntry>() {
            public int compare(AdminLogEntry o1, AdminLogEntry o2) {
                return o1.logType.compareTo(o2.logType);
            }
        });

        addColumn("Admin", 7.5f, new Comparator<AdminLogEntry>() {
            public int compare(AdminLogEntry o1, AdminLogEntry o2) {
                return o1.adminName.compareTo(o2.adminName);
            }
        });

        addTextFilter(new GUIListFilterText<AdminLogEntry>() {
            public boolean isOk(String s, AdminLogEntry logEntry) {
                return logEntry.adminName.toLowerCase().contains(s.toLowerCase());
            }
        }, "ADMIN NAME", ControllerElement.FilterRowStyle.LEFT);

        addDropdownFilter(new GUIListFilterDropdown<AdminLogEntry, AdminLogEntry.LogType>(AdminLogEntry.LogType.values()) {
            public boolean isOk(AdminLogEntry.LogType logType, AdminLogEntry logEntry) {
                switch(logType) {
                    case ALL:
                        return true;
                    case SERVER:
                        return logEntry.logType.equals(AdminLogEntry.LogType.SERVER);
                    case PLAYER:
                        return logEntry.logType.equals(AdminLogEntry.LogType.PLAYER);
                    case FACTION:
                        return logEntry.logType.equals(AdminLogEntry.LogType.FACTION);
                    case ENTITY:
                        return logEntry.logType.equals(AdminLogEntry.LogType.ENTITY);
                    case OTHER:
                        return logEntry.logType.equals(AdminLogEntry.LogType.OTHER);
                }
                return true;
            }

        }, new CreateGUIElementInterface<AdminLogEntry.LogType>() {
            @Override
            public GUIElement create(AdminLogEntry.LogType logType) {
                GUIAncor anchor = new GUIAncor(getState(), 10.0f, 24.0f);
                GUITextOverlayTableDropDown dropDown;
                (dropDown = new GUITextOverlayTableDropDown(10, 10, getState())).setTextSimple(logType.toString());
                dropDown.setPos(4.0f, 4.0f, 0.0f);
                anchor.setUserPointer(logType);
                anchor.attach(dropDown);
                return anchor;
            }

            @Override
            public GUIElement createNeutral() {
                GUIAncor anchor = new GUIAncor(getState(), 10.0f, 24.0f);
                GUITextOverlayTableDropDown dropDown;
                (dropDown = new GUITextOverlayTableDropDown(10, 10, getState())).setTextSimple(AdminLogEntry.LogType.ALL.toString());
                dropDown.setPos(4.0f, 4.0f, 0.0f);
                anchor.setUserPointer(AdminLogEntry.LogType.ALL);
                anchor.attach(dropDown);
                return anchor;
            }
        }, ControllerElement.FilterRowStyle.RIGHT);

        activeSortColumnIndex = 0;
    }

    @Override
    public void updateListEntries(GUIElementList guiElementList, Set<AdminLogEntry> set) {
        guiElementList.deleteObservers();
        guiElementList.addObserver(this);

        for(AdminLogEntry logEntry : set) {
            GUITextOverlayTable dateTextElement;
            (dateTextElement = new GUITextOverlayTable(10, 10, getState())).setTextSimple(logEntry.getDateString());
            GUIClippedRow dateRowElement;
            (dateRowElement = new GUIClippedRow(getState())).attach(dateTextElement);

            GUITextOverlayTable typeTextElement;
            (typeTextElement = new GUITextOverlayTable(10, 10, getState())).setTextSimple(logEntry.logType.toString());
            GUIClippedRow typeRowElement;
            (typeRowElement = new GUIClippedRow(getState())).attach(typeTextElement);

            AdminLogListRow logListRow = new AdminLogListRow(getState(), logEntry, dateRowElement, typeRowElement);
            GUIAncor anchor = new GUIAncor(getState(), 739, 64);
            GUITextOverlay descriptionOverlay = new GUITextOverlay(735, 60, FontLibrary.FontSize.SMALL, getState());
            descriptionOverlay.setTextSimple(logEntry.description);
            anchor.attach(descriptionOverlay);
            logListRow.expanded = new GUIElementList(getState());
            logListRow.expanded.add(new GUIListElement(anchor, getState()));
            logListRow.expanded.attach(anchor);
            logListRow.onInit();
            guiElementList.add(logListRow);
        }
        guiElementList.updateDim();
    }

    public class AdminLogListRow extends ScrollableTableList<AdminLogEntry>.Row {

        public AdminLogListRow(InputState inputState, AdminLogEntry logEntry, GUIElement... guiElements) {
            super(inputState, logEntry, guiElements);
            highlightSelect = true;
            highlightSelectSimple = true;
            setAllwaysOneSelected(true);
        }
    }
}

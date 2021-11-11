package thederpgamer.edencore.gui.eventsmenu;

import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUIElementList;
import org.schema.schine.graphicsengine.forms.gui.newgui.ScrollableTableList;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.event.EventData;

import java.util.Collection;
import java.util.Set;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/08/2021]
 */
public class EventsScrollableList extends ScrollableTableList<EventData> {

    private EventsMenuPanel panel;
    private int type;

    public EventsScrollableList(InputState state, GUIElement p, EventsMenuPanel panel, int type) {
        super(state, panel.getWidth(), panel.getHeight(), p);
        this.panel = panel;
        this.type = type;
        p.attach(this);
    }

    @Override
    protected Collection<EventData> getElementList() {
        return null;
    }

    @Override
    public void initColumns() {

    }

    @Override
    public void updateListEntries(GUIElementList guiElementList, Set<EventData> set) {

    }

    public class EventsScrollableListRow extends ScrollableTableList<EventData>.Row {

        public EventsScrollableListRow(InputState state, EventData eventData, GUIElement... elements) {
            super(state, eventData, elements);
            this.highlightSelect = true;
            this.highlightSelectSimple = true;
            this.setAllwaysOneSelected(true);
        }
    }
}

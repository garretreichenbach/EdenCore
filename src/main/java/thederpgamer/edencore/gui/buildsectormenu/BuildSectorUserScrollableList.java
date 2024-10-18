package thederpgamer.edencore.gui.buildsectormenu;

import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUIElementList;
import org.schema.schine.graphicsengine.forms.gui.newgui.ScrollableTableList;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.buildsectordata.BuildSectorData;

import java.util.Collection;
import java.util.Set;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class BuildSectorUserScrollableList extends ScrollableTableList<String> {

	private final GUIElement parent;
	private final BuildSectorData buildSectorData;

	public BuildSectorUserScrollableList(InputState state, GUIElement parent, BuildSectorData buildSectorData) {
		super(state, 100, 100, parent);
		this.parent = parent;
		this.buildSectorData = buildSectorData;
	}

	@Override
	protected Collection<String> getElementList() {
		return buildSectorData.getAllUsers();
	}
	
	@Override
	public void initColumns() {
		
	}

	@Override
	public void updateListEntries(GUIElementList guiElementList, Set<String> set) {

	}

	public class BuildSectorUserScrollableListRow extends ScrollableTableList<String>.Row {

		public BuildSectorUserScrollableListRow(InputState state, String username, GUIElement... elements) {
			super(state, username, elements);
			highlightSelect = true;
			highlightSelectSimple = true;
			setAllwaysOneSelected(true);
		}
	}
}

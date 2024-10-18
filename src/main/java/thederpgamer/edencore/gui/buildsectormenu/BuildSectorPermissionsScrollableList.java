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
public class BuildSectorPermissionsScrollableList extends ScrollableTableList<BuildSectorData.BuildSectorPermissionData> {
	
	private final GUIElement parent;
	private final String username;
	private final BuildSectorData buildSectorData;
	
	public BuildSectorPermissionsScrollableList(InputState state, GUIElement parent, String username, BuildSectorData buildSectorData) {
		super(state, 100, 100, parent);
		this.parent = parent;
		this.username = username;
		this.buildSectorData = buildSectorData;
	}

	@Override
	protected Collection<BuildSectorData.BuildSectorPermissionData> getElementList() {
		return buildSectorData.getPermissionsFor(username);
	}

	@Override
	public void initColumns() {
		
	}

	@Override
	public void updateListEntries(GUIElementList guiElementList, Set<BuildSectorData.BuildSectorPermissionData> set) {

	}

	public class BuildSectorPermissionsScrollableListRow extends ScrollableTableList<BuildSectorData.BuildSectorPermissionData>.Row {

		public BuildSectorPermissionsScrollableListRow(InputState state, BuildSectorData.BuildSectorPermissionData data, GUIElement... elements) {
			super(state, data, elements);
			highlightSelect = true;
			highlightSelectSimple = true;
			setAllwaysOneSelected(true);
		}
	}
}

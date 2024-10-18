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
public class BuildSectorEntityScrollableList extends ScrollableTableList<BuildSectorData.BuildSectorEntityData> {
	
	private final GUIElement parent;
	private final BuildSectorData buildSectorData;
	
	public BuildSectorEntityScrollableList(InputState state, GUIElement parent, BuildSectorData buildSectorData) {
		super(state, 100, 100, parent);
		this.parent = parent;
		this.buildSectorData = buildSectorData;
	}

	@Override
	protected Collection<BuildSectorData.BuildSectorEntityData> getElementList() {
		return buildSectorData.getEntities();
	}

	@Override
	public void initColumns() {
		
	}

	@Override
	public void updateListEntries(GUIElementList guiElementList, Set<BuildSectorData.BuildSectorEntityData> set) {

	}

	public class BuildSectorEntityScrollableListRow extends ScrollableTableList<BuildSectorData.BuildSectorEntityData>.Row {

		public BuildSectorEntityScrollableListRow(InputState state, BuildSectorData.BuildSectorEntityData entity, GUIElement... elements) {
			super(state, entity, elements);
			highlightSelect = true;
			highlightSelectSimple = true;
			setAllwaysOneSelected(true);
		}
	}
}

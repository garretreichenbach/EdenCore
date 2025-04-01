package thederpgamer.edencore.gui.buildsectormenu;

import api.common.GameClient;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.buildsectordata.BuildSectorData;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class BuildSectorUserScrollableList extends ScrollableTableList<String> implements GUIActiveInterface {

	private final GUIElement parent;
	private final int entityID;
	private final BuildSectorData buildSectorData;

	public BuildSectorUserScrollableList(InputState state, GUIElement parent, BuildSectorData buildSectorData, int entityID) {
		super(state, 100, 100, parent);
		this.parent = parent;
		this.buildSectorData = buildSectorData;
		this.entityID = entityID;
	}

	public BuildSectorUserScrollableList(InputState state, GUIElement parent, BuildSectorData buildSectorData) {
		this(state, parent, buildSectorData, -1);
	}

	@Override
	protected Collection<String> getElementList() {
		return buildSectorData.getAllUsers();
	}

	@Override
	public void initColumns() {
		addColumn("Username", 5.0f, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2); // Sort usernames alphabetically
			}
		});
		addTextFilter(new GUIListFilterText<String>() {
			@Override
			public boolean isOk(String s, String s2) {
				return s2.trim().toLowerCase().contains(s.trim().toLowerCase());
			}
		}, ControllerElement.FilterRowStyle.FULL);
		activeSortColumnIndex = 0;
	}

	@Override
	public void updateListEntries(GUIElementList guiElementList, Set<String> set) {
		guiElementList.deleteObservers();
		guiElementList.addObserver(this);
		for(final String username : set) {
			GUIClippedRow nameRow = getSimpleRow(username, this);
			final BuildSectorUserScrollableListRow entryListRow = new BuildSectorUserScrollableListRow(getState(), username, nameRow);
			GUIAncor anchor = new GUIAncor(getState(), parent.getWidth() - 28.0f, 28.0f) {
				@Override
				public void draw() {
					setWidth(entryListRow.getWidth());
					super.draw();
				}
			};

			GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 2, 1, anchor);
			buttonPane.onInit();
			buttonPane.addButton(0, 0, "EDIT PERMISSIONS", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						if(entityID != -1) (new BuildSectorEditUserPermissionsDialog(username, buildSectorData)).activate();
						else (new BuildSectorEditEntityUserPermissionsDialog(entityID, username, buildSectorData)).activate();
					}
				}

				@Override
				public boolean isOccluded() {
					if(!getState().getController().getPlayerInputs().isEmpty() && !getState().getController().getPlayerInputs().contains(getDialog())) return true;
					if(entityID != -1) return !buildSectorData.getPermissionForEntityOrGlobal(username, entityID, BuildSectorData.PermissionTypes.EDIT_ENTITY_PERMISSIONS);
					else return !buildSectorData.getPermission(username, BuildSectorData.PermissionTypes.EDIT_PERMISSIONS);
				}
			}, new GUIActivationCallback() {
				@Override
				public boolean isVisible(InputState inputState) {
					return true;
				}

				@Override
				public boolean isActive(InputState inputState) {
					if(entityID != -1) return buildSectorData.getPermissionForEntityOrGlobal(username, entityID, BuildSectorData.PermissionTypes.EDIT_ENTITY_PERMISSIONS);
					return buildSectorData.getPermission(username, BuildSectorData.PermissionTypes.EDIT_PERMISSIONS);
				}
			});
			buttonPane.addButton(1, 0, "REMOVE USER", GUIHorizontalArea.HButtonColor.ORANGE, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						buildSectorData.removePlayer(username, false);
						flagDirty();
					}
				}

				@Override
				public boolean isOccluded() {
					if(!getState().getController().getPlayerInputs().isEmpty() && !getState().getController().getPlayerInputs().contains(getDialog())) return true;
					return !buildSectorData.getPermission(username, BuildSectorData.PermissionTypes.KICK) || buildSectorData.getOwner().equals(GameClient.getClientPlayerState().getName());

				}
			}, new GUIActivationCallback() {
				@Override
				public boolean isVisible(InputState inputState) {
					return true;
				}

				@Override
				public boolean isActive(InputState inputState) {
					return buildSectorData.getPermission(username, BuildSectorData.PermissionTypes.KICK) && !buildSectorData.getOwner().equals(GameClient.getClientPlayerState().getName());
				}
			});

			anchor.attach(buttonPane);
			entryListRow.expanded = new GUIElementList(getState());
			entryListRow.expanded.add(new GUIListElement(anchor, getState()));
			entryListRow.onInit();
			guiElementList.addWithoutUpdate(entryListRow);
		}
		guiElementList.updateDim();
	}

	private BuildSectorDialog getDialog() {
		return BuildSectorDialog.getInstance();
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

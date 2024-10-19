package thederpgamer.edencore.gui.buildsectormenu;

import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.graphicsengine.movie.craterstudio.data.tuples.Pair;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.buildsectordata.BuildSectorData;

import java.util.*;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class BuildSectorPermissionsScrollableList extends ScrollableTableList<Pair<BuildSectorData.PermissionTypes, Boolean>> implements GUIActiveInterface {

	private final GUIElement parent;
	private final int entityID;
	private final String username;
	private final BuildSectorData buildSectorData;

	public BuildSectorPermissionsScrollableList(InputState state, GUIElement parent, String username, BuildSectorData buildSectorData) {
		this(state, parent, username, buildSectorData, -1);
	}

	public BuildSectorPermissionsScrollableList(InputState state, GUIElement parent, String username, BuildSectorData buildSectorData, int entityID) {
		super(state, 100, 100, parent);
		this.parent = parent;
		this.username = username;
		this.buildSectorData = buildSectorData;
		this.entityID = entityID;
	}

	@Override
	public void initColumns() {
		addColumn("Permission", 5.0f, (Comparator.comparing(o -> o.first().getName())));
		addColumn("Value", 5.0f, (Comparator.comparing(o -> o.second().toString())));
		addTextFilter(new GUIListFilterText<Pair<BuildSectorData.PermissionTypes, Boolean>>() {
			@Override
			public boolean isOk(String s, Pair<BuildSectorData.PermissionTypes, Boolean> permissionTypesBooleanPair) {
				return permissionTypesBooleanPair.first().getName().toLowerCase().contains(s.toLowerCase());
			}
		}, ControllerElement.FilterRowStyle.LEFT);
		addDropdownFilter(new GUIListFilterDropdown<Pair<BuildSectorData.PermissionTypes, Boolean>, String>("TRUE", "FALSE") {
			@Override
			public boolean isOk(String o, Pair<BuildSectorData.PermissionTypes, Boolean> permissionTypesBooleanPair) {
				return o == null || o.isEmpty() || o.equals("ANY") || o.equals(permissionTypesBooleanPair.second().toString().toUpperCase());
			}
		}, new CreateGUIElementInterface<String>() {
			@Override
			public GUIElement create(String o) {
				GUIAncor anchor = new GUIAncor(getState(), 10.0F, 24.0F);
				GUITextOverlayTableDropDown dropDown;
				(dropDown = new GUITextOverlayTableDropDown(10, 10, getState())).setTextSimple(o);
				dropDown.setPos(4.0F, 4.0F, 0.0F);
				anchor.setUserPointer(o);
				anchor.attach(dropDown);
				return anchor;
			}

			@Override
			public GUIElement createNeutral() {
				GUIAncor anchor = new GUIAncor(getState(), 10.0F, 24.0F);
				GUITextOverlayTableDropDown dropDown;
				(dropDown = new GUITextOverlayTableDropDown(10, 10, getState())).setTextSimple("ANY");
				dropDown.setPos(4.0F, 4.0F, 0.0F);
				anchor.setUserPointer("ANY");
				anchor.attach(dropDown);
				return anchor;
			}
		}, ControllerElement.FilterRowStyle.RIGHT);
		activeSortColumnIndex = 0;
	}

	@Override
	protected Collection<Pair<BuildSectorData.PermissionTypes, Boolean>> getElementList() {
		if(entityID != -1) {
			HashMap<BuildSectorData.PermissionTypes, Boolean> permissions = buildSectorData.getPermissionsForEntity(entityID, username);
			Set<Pair<BuildSectorData.PermissionTypes, Boolean>> permissionSet = new HashSet<>();
			for(Map.Entry<BuildSectorData.PermissionTypes, Boolean> entry : permissions.entrySet()) permissionSet.add(new Pair<>(entry.getKey(), entry.getValue()));
			return permissionSet;
		} else {
			HashMap<BuildSectorData.PermissionTypes, Boolean> permissions = buildSectorData.getPermissionsForUser(username);
			Set<Pair<BuildSectorData.PermissionTypes, Boolean>> permissionSet = new HashSet<>();
			for(Map.Entry<BuildSectorData.PermissionTypes, Boolean> entry : permissions.entrySet()) permissionSet.add(new Pair<>(entry.getKey(), entry.getValue()));
			return permissionSet;
		}
	}

	@Override
	public void updateListEntries(GUIElementList guiElementList, Set<Pair<BuildSectorData.PermissionTypes, Boolean>> set) {
		guiElementList.deleteObservers();
		guiElementList.addObserver(this);
		for(Pair<BuildSectorData.PermissionTypes, Boolean> permission : set) {
			GUIClippedRow permissionRow = getSimpleRow(permission.first().getName(), this);
			GUIClippedRow valueRow = getSimpleRow(permission.second().toString(), this);
			BuildSectorPermissionsScrollableListRow entryListRow = new BuildSectorPermissionsScrollableListRow(getState(), permission, permissionRow, valueRow);
			guiElementList.add(entryListRow);
			GUIAncor anchor = new GUIAncor(getState(), parent.getWidth() - 107.0f, 28.0f) {
				@Override
				public void draw() {
					setWidth(parent.getWidth() - 107.0f);
					super.draw();
				}
			};
			GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 2, 1, anchor);
			buttonPane.onInit();
			buttonPane.addButton(0, 0, "SET TRUE", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						if(entityID != -1) buildSectorData.setPermissionForEntity(entityID, username, permission.first(), true, false);
						else buildSectorData.setPermission(username, permission.first(), true, false);
						flagDirty();
					}
				}

				@Override
				public boolean isOccluded() {
					if(entityID != -1) return !buildSectorData.getPermissionForEntityOrGlobal(username, entityID, BuildSectorData.PermissionTypes.EDIT_ENTITY_PERMISSIONS);
					return !buildSectorData.getPermission(username, BuildSectorData.PermissionTypes.EDIT_PERMISSIONS);
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
			buttonPane.addButton(1, 0, "SET FALSE", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						if(entityID != -1) buildSectorData.setPermissionForEntity(entityID, username, permission.first(), false, false);
						else buildSectorData.setPermission(username, permission.first(), false, false);
						flagDirty();
					}
				}

				@Override
				public boolean isOccluded() {
					if(entityID != -1) return !buildSectorData.getPermissionForEntityOrGlobal(username, entityID, BuildSectorData.PermissionTypes.EDIT_ENTITY_PERMISSIONS);
					return !buildSectorData.getPermission(username, BuildSectorData.PermissionTypes.EDIT_PERMISSIONS);
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
			anchor.attach(buttonPane);
			entryListRow.expanded = new GUIElementList(getState());
			entryListRow.expanded.add(new GUIListElement(anchor, getState()));
			entryListRow.onInit();
			guiElementList.addWithoutUpdate(entryListRow);
		}
		guiElementList.updateDim();
	}

	public class BuildSectorPermissionsScrollableListRow extends ScrollableTableList<Pair<BuildSectorData.PermissionTypes, Boolean>>.Row {

		public BuildSectorPermissionsScrollableListRow(InputState state, Pair<BuildSectorData.PermissionTypes, Boolean> data, GUIElement... elements) {
			super(state, data, elements);
			highlightSelect = true;
			highlightSelectSimple = true;
			setAllwaysOneSelected(true);
		}
	}
}

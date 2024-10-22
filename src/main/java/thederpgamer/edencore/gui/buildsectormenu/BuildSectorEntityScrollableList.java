package thederpgamer.edencore.gui.buildsectormenu;

import org.schema.common.util.StringTools;
import org.schema.game.client.data.GameClientState;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.buildsectordata.BuildSectorData;
import thederpgamer.edencore.utils.EntityUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class BuildSectorEntityScrollableList extends ScrollableTableList<BuildSectorData.BuildSectorEntityData> implements GUIActiveInterface {

	private final GUIElement parent;
	protected final BuildSectorData buildSectorData;
	private GUIHorizontalButtonTablePane buttonPane;

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
		addColumn("Name", 7.0f, (Comparator.comparing(o -> o.getEntity().getName())));
		addColumn("Type", 5.0f, (Comparator.comparing(o -> o.getEntity().getType().getName())));
		addColumn("Mass", 3.0f, (Comparator.comparing(o -> o.getEntity().getMass())));
		addTextFilter(new GUIListFilterText<BuildSectorData.BuildSectorEntityData>() {
			@Override
			public boolean isOk(String s, BuildSectorData.BuildSectorEntityData buildSectorEntityData) {
				return s.trim().isEmpty() || buildSectorEntityData.getEntity().getName().toLowerCase(Locale.ENGLISH).contains(s.trim().toLowerCase(Locale.ENGLISH));
			}
		}, ControllerElement.FilterRowStyle.LEFT);
		addDropdownFilter(new GUIListFilterDropdown<BuildSectorData.BuildSectorEntityData, BuildSectorData.EntityType>() {

			@Override
			public boolean isOk(BuildSectorData.EntityType entityType, BuildSectorData.BuildSectorEntityData buildSectorEntityData) {
				return entityType == null || entityType == buildSectorEntityData.getEntityType();
			}
		}, new CreateGUIElementInterface<BuildSectorData.EntityType>() {
			@Override
			public GUIElement create(BuildSectorData.EntityType o) {
				GUIAncor anchor = new GUIAncor(getState(), 10.0F, 24.0F);
				GUITextOverlayTableDropDown dropDown;
				(dropDown = new GUITextOverlayTableDropDown(10, 10, getState())).setTextSimple(o.name());
				dropDown.setPos(4.0F, 4.0F, 0.0F);
				anchor.setUserPointer(o);
				anchor.attach(dropDown);
				return anchor;
			}

			@Override
			public GUIElement createNeutral() {
				GUIAncor anchor = new GUIAncor(getState(), 10.0F, 24.0F);
				GUITextOverlayTableDropDown dropDown;
				(dropDown = new GUITextOverlayTableDropDown(10, 10, getState())).setTextSimple("ALL");
				dropDown.setPos(4.0F, 4.0F, 0.0F);
				anchor.setUserPointer(null);
				anchor.attach(dropDown);
				return anchor;
			}
		}, ControllerElement.FilterRowStyle.RIGHT);
		activeSortColumnIndex = 0;
	}

	@Override
	public void updateListEntries(GUIElementList guiElementList, Set<BuildSectorData.BuildSectorEntityData> set) {
		guiElementList.deleteObservers();
		guiElementList.addObserver(this);
		for(BuildSectorData.BuildSectorEntityData entityData : set) {
			GUIClippedRow nameRow = getSimpleRow(entityData.getEntity().getName(), this);
			GUIClippedRow typeRow = getSimpleRow(entityData.getEntity().getType().getName(), this);
			GUIClippedRow massRow = getSimpleRow(StringTools.massFormat(entityData.getEntity().getMass()), this);

			BuildSectorEntityScrollableListRow entryListRow = new BuildSectorEntityScrollableListRow(getState(), entityData, nameRow, typeRow, massRow);
			guiElementList.add(entryListRow);
			GUIAncor anchor = new GUIAncor(getState(), parent.getWidth() - 107.0f, 56.0f) {
				@Override
				public void draw() {
					setWidth(parent.getWidth() - 107.0f);
					super.draw();
				}
			};
			if(buttonPane != null) {
				anchor.detach(buttonPane);
				buttonPane.cleanUp();
			}
			redrawButtonPane(entityData, anchor);
			anchor.attach(buttonPane);
			entryListRow.expanded = new GUIElementList(getState());
			entryListRow.expanded.add(new GUIListElement(anchor, getState()));
			entryListRow.onInit();
			guiElementList.addWithoutUpdate(entryListRow);
		}
		guiElementList.updateDim();
	}

	private void redrawButtonPane(BuildSectorData.BuildSectorEntityData entityData, GUIAncor anchor) {
		buttonPane = new GUIHorizontalButtonTablePane(getState(), 3, 2, anchor);
		buttonPane.onInit();
		String user = ((GameClientState) getState()).getPlayerName();

		buttonPane.addButton(0, 0, "WARP TO", GUIHorizontalArea.HButtonColor.YELLOW, new GUICallback() {
			@Override
			public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
				if(mouseEvent.pressedLeftMouse()) EntityUtils.warpPlayerIntoEntity(entityData.getEntity());
			}

			@Override
			public boolean isOccluded() {
				return !buildSectorData.getPermissionForEntityOrGlobal(user, entityData.getEntityID(), BuildSectorData.PermissionTypes.EDIT_SPECIFIC);
			}
		}, new GUIActivationCallback() {
			@Override
			public boolean isVisible(InputState inputState) {
				return false;
			}

			@Override
			public boolean isActive(InputState inputState) {
				return buildSectorData.getPermissionForEntityOrGlobal(user, entityData.getEntityID(), BuildSectorData.PermissionTypes.EDIT_SPECIFIC);
			}
		});
		buttonPane.addButton(1, 0, "EDIT PERMISSIONS", GUIHorizontalArea.HButtonColor.ORANGE, new GUICallback() {
			@Override
			public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
				if(mouseEvent.pressedLeftMouse()) {
					(new BuildSectorEditEntityPermissionsDialog(entityData, buildSectorData)).activate();
				}
			}

			@Override
			public boolean isOccluded() {
				return !buildSectorData.getPermissionForEntityOrGlobal(user, entityData.getEntityID(), BuildSectorData.PermissionTypes.EDIT_ENTITY_PERMISSIONS);
			}
		}, new GUIActivationCallback() {
			@Override
			public boolean isVisible(InputState inputState) {
				return false;
			}

			@Override
			public boolean isActive(InputState inputState) {
				return buildSectorData.getPermissionForEntityOrGlobal(user, entityData.getEntityID(), BuildSectorData.PermissionTypes.EDIT_ENTITY_PERMISSIONS);
			}
		});
		if(entityData.isAIActive()) {
			buttonPane.addButton(2, 0, "DEACTIVATE AI", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						entityData.setAIActive(false);
						clear();
					}
				}

				@Override
				public boolean isOccluded() {
					return !buildSectorData.getPermissionForEntityOrGlobal(user, entityData.getEntityID(), BuildSectorData.PermissionTypes.TOGGLE_AI_SPECIFIC);
				}
			}, new GUIActivationCallback() {
				@Override
				public boolean isVisible(InputState inputState) {
					return false;
				}

				@Override
				public boolean isActive(InputState inputState) {
					return buildSectorData.getPermissionForEntityOrGlobal(user, entityData.getEntityID(), BuildSectorData.PermissionTypes.TOGGLE_AI_SPECIFIC);
				}
			});
		} else {
			buttonPane.addButton(2, 0, "ACTIVATE AI", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						entityData.setAIActive(true);
						clear();
					}
				}

				@Override
				public boolean isOccluded() {
					return !buildSectorData.getPermissionForEntityOrGlobal(user, entityData.getEntityID(), BuildSectorData.PermissionTypes.TOGGLE_AI_SPECIFIC);
				}
			}, new GUIActivationCallback() {
				@Override
				public boolean isVisible(InputState inputState) {
					return false;
				}

				@Override
				public boolean isActive(InputState inputState) {
					return buildSectorData.getPermissionForEntityOrGlobal(user, entityData.getEntityID(), BuildSectorData.PermissionTypes.TOGGLE_AI_SPECIFIC);
				}
			});
		}
		buttonPane.addButton(0, 1, "DELETE", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
			@Override
			public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
				if(mouseEvent.pressedLeftMouse()) {
					entityData.delete();
					clear();
				}
			}

			@Override
			public boolean isOccluded() {
				return !buildSectorData.getPermissionForEntityOrGlobal(user, entityData.getEntityID(), BuildSectorData.PermissionTypes.DELETE_SPECIFIC);
			}
		}, new GUIActivationCallback() {
			@Override
			public boolean isVisible(InputState inputState) {
				return false;
			}

			@Override
			public boolean isActive(InputState inputState) {
				return buildSectorData.getPermissionForEntityOrGlobal(user, entityData.getEntityID(), BuildSectorData.PermissionTypes.DELETE_SPECIFIC);
			}
		});
		buttonPane.addButton(1, 1, "DELETE TURRETS", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
			@Override
			public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
				if(mouseEvent.pressedLeftMouse()) {
					entityData.deleteTurrets();
					clear();
				}
			}

			@Override
			public boolean isOccluded() {
				return !buildSectorData.getPermissionForEntityOrGlobal(user, entityData.getEntityID(), BuildSectorData.PermissionTypes.DELETE_SPECIFIC);
			}
		}, new GUIActivationCallback() {
			@Override
			public boolean isVisible(InputState inputState) {
				return false;
			}

			@Override
			public boolean isActive(InputState inputState) {
				return buildSectorData.getPermissionForEntityOrGlobal(user, entityData.getEntityID(), BuildSectorData.PermissionTypes.DELETE_SPECIFIC);
			}
		});
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

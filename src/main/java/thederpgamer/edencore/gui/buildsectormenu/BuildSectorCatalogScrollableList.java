package thederpgamer.edencore.gui.buildsectormenu;

import api.common.GameClient;
import api.network.packets.PacketUtil;
import api.utils.gui.SimplePlayerTextInput;
import org.schema.common.util.StringTools;
import org.schema.game.common.data.player.catalog.CatalogPermission;
import org.schema.game.common.data.player.faction.FactionManager;
import org.schema.game.server.data.blueprintnw.BlueprintType;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.network.client.exchange.RequestSpawnEntryPacket;
import thederpgamer.edencore.utils.DataUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/29/2021]
 */
public class BuildSectorCatalogScrollableList extends ScrollableTableList<CatalogPermission> {
	private final GUIElement p;
	private final BuildSectorMenuPanel menuPanel;
	private BuildSectorData sectorData;
	private BuildSectorSpawnEntityDialog spawnEntityDialog;

	public BuildSectorCatalogScrollableList(InputState state, BuildSectorData sectorData, GUIElement p, BuildSectorMenuPanel menuPanel) {
		super(state, 800, 500, p);
		this.sectorData = sectorData;
		this.p = p;
		this.menuPanel = menuPanel;
		p.attach(this);
	}

	@Override
	public void initColumns() {
		addColumn(Lng.str("Name"), 15.0f, new Comparator<CatalogPermission>() {
			@Override
			public int compare(CatalogPermission o1, CatalogPermission o2) {
				return o1.getUid().compareTo(o2.getUid());
			}
		});
		addColumn(Lng.str("Type"), 7.0f, new Comparator<CatalogPermission>() {
			@Override
			public int compare(CatalogPermission o1, CatalogPermission o2) {
				return o1.type.name().compareTo(o2.type.name());
			}
		});
		addColumn(Lng.str("Class"), 8.0f, new Comparator<CatalogPermission>() {
			@Override
			public int compare(CatalogPermission o1, CatalogPermission o2) {
				return o1.getClassification().ordinal() - o2.getClassification().ordinal();
			}
		});
		addColumn(Lng.str("Mass"), 7.5f, new Comparator<CatalogPermission>() {
			@Override
			public int compare(CatalogPermission o1, CatalogPermission o2) {
				return Float.compare(o1.mass, o2.mass);
			}
		});
		addTextFilter(new GUIListFilterText<CatalogPermission>() {
			@Override
			public boolean isOk(String input, CatalogPermission listElement) {
				return listElement.getUid().toLowerCase().contains(input.toLowerCase());
			}
		}, Lng.str("SEARCH BY NAME"), ControllerElement.FilterRowStyle.LEFT);
		addDropdownFilter(new GUIListFilterDropdown<CatalogPermission, BlueprintType>(BlueprintType.SHIP, BlueprintType.SPACE_STATION) {
			@Override
			public boolean isOk(BlueprintType input, CatalogPermission f) {
				return f.type == input;
			}
		}, new CreateGUIElementInterface<BlueprintType>() {
			@Override
			public GUIElement create(BlueprintType o) {
				GUIAncor c = new GUIAncor(getState(), 10, 20);
				GUITextOverlayTableDropDown a = new GUITextOverlayTableDropDown(10, 10, getState());
				a.setTextSimple(o.name().toUpperCase().replaceAll("_", " "));
				a.setPos(4, 4, 0);
				c.setUserPointer(o);
				c.attach(a);
				return c;
			}

			@Override
			public GUIElement createNeutral() {
				GUIAncor c = new GUIAncor(getState(), 10, 20);
				GUITextOverlayTableDropDown a = new GUITextOverlayTableDropDown(10, 10, getState());
				a.setTextSimple("ALL");
				a.setPos(4, 4, 0);
				c.attach(a);
				return c;
			}
		}, ControllerElement.FilterRowStyle.RIGHT);
	}

	@Override
	protected Collection<CatalogPermission> getElementList() {
		return GameClient.getClientPlayerState().getCatalog().getAvailableCatalog();
	}

	@Override
	public void updateListEntries(GUIElementList guiElementList, Set<CatalogPermission> set) {
		guiElementList.deleteObservers();
		guiElementList.addObserver(this);
		if(DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
			for(CatalogPermission catalogPermission : set) {
				GUITextOverlayTable nameTextElement;
				(nameTextElement = new GUITextOverlayTable(10, 10, this.getState())).setTextSimple(catalogPermission.getUid());
				GUIClippedRow nameRowElement;
				(nameRowElement = new GUIClippedRow(this.getState())).attach(nameTextElement);
				GUITextOverlayTable typeTextElement;
				(typeTextElement = new GUITextOverlayTable(10, 10, this.getState())).setTextSimple(catalogPermission.type.name().replaceAll("_", " "));
				GUIClippedRow typeRowElement;
				(typeRowElement = new GUIClippedRow(this.getState())).attach(typeTextElement);
				GUITextOverlayTable classTextElement;
				(classTextElement = new GUITextOverlayTable(10, 10, this.getState())).setTextSimple(catalogPermission.getClassification().getName());
				GUIClippedRow classRowElement;
				(classRowElement = new GUIClippedRow(this.getState())).attach(classTextElement);
				GUITextOverlayTable massTextElement;
				(massTextElement = new GUITextOverlayTable(10, 10, this.getState())).setTextSimple(StringTools.massFormat(catalogPermission.mass));
				GUIClippedRow massRowElement;
				(massRowElement = new GUIClippedRow(this.getState())).attach(massTextElement);
				BuildSectorCatalogListRow listRow = new BuildSectorCatalogListRow(getState(), catalogPermission, nameRowElement, typeRowElement, classRowElement, massRowElement);
				GUIAncor anchor = new GUIAncor(getState(), p.getWidth() - 28.0f, 28.0f);
				anchor.attach(redrawButtonPane(catalogPermission, anchor));
				listRow.expanded = new GUIElementList(getState());
				listRow.expanded.add(new GUIListElement(anchor, getState()));
				listRow.expanded.attach(anchor);
				listRow.onInit();
				guiElementList.addWithoutUpdate(listRow);
			}
		}
		guiElementList.updateDim();
	}

	private GUIHorizontalButtonTablePane redrawButtonPane(final CatalogPermission catalogPermission, GUIAncor anchor) {
		GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 2, 1, anchor);
		buttonPane.onInit();
		buttonPane.addButton(0, 0, "SPAWN", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
			@Override
			public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
				if(mouseEvent.pressedLeftMouse()) {
					if(hasPermission("SPAWN")) {
						getState().getController().queueUIAudio("0022_menu_ui - select 1");
						spawnEntityDialog = new BuildSectorSpawnEntityDialog(menuPanel);
						spawnEntityDialog.sectorData = sectorData;
						spawnEntityDialog.catalogPermission = catalogPermission;
						spawnEntityDialog.activate();
						spawnEntityDialog.getInputPanel().setSpawnName(catalogPermission.getUid());
					} else getState().getController().queueUIAudio("0022_menu_ui - error 1");
				}
			}

			@Override
			public boolean isOccluded() {
				return !getState().getController().getPlayerInputs().isEmpty() || isBlocked() || !hasPermission("SPAWN");
			}
		}, new GUIActivationCallback() {
			@Override
			public boolean isVisible(InputState inputState) {
				return true;
			}

			@Override
			public boolean isActive(InputState inputState) {
				return getState().getController().getPlayerInputs().isEmpty() && !isBlocked() && hasPermission("SPAWN");
			}
		});
		buttonPane.addButton(1, 0, "SPAWN ENEMY", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
			@Override
			public void callback(GUIElement guiElement, final MouseEvent mouseEvent) {
				if(mouseEvent.pressedLeftMouse()) {
					if(hasPermission("SPAWN_ENEMIES")) {
						getState().getController().queueUIAudio("0022_menu_ui - select 3");
						(new SimplePlayerTextInput("Enter Name", "") {
							@Override
							public boolean onInput(String s) {
								if(s == null || s.isEmpty()) s = catalogPermission.getUid();
								PacketUtil.sendPacketToServer(new RequestSpawnEntryPacket(s, catalogPermission.getUid(), false, FactionManager.PIRATES_ID, sectorData.sector));
								return true;
							}
						}).activate();
					} else getState().getController().queueUIAudio("0022_menu_ui - error 1");
				}
			}

			@Override
			public boolean isOccluded() {
				return !getState().getController().getPlayerInputs().isEmpty() || isBlocked() || !hasPermission("SPAWN_ENEMIES");
			}
		}, new GUIActivationCallback() {
			@Override
			public boolean isVisible(InputState inputState) {
				return true;
			}

			@Override
			public boolean isActive(InputState inputState) {
				return getState().getController().getPlayerInputs().isEmpty() && !isBlocked() && hasPermission("SPAWN_ENEMIES");
			}
		});
		return buttonPane;
	}

	private boolean hasPermission(String permission) {
		sectorData = DataUtils.getPlayerCurrentBuildSector(GameClient.getClientPlayerState());
		if(sectorData == null) sectorData = DataUtils.getBuildSector(GameClient.getClientPlayerState().getName());
		return sectorData.hasPermission(GameClient.getClientPlayerState().getName(), permission);
	}

	public boolean isBlocked() {
		return spawnEntityDialog != null && spawnEntityDialog.isActive();
	}

	public class BuildSectorCatalogListRow extends ScrollableTableList<CatalogPermission>.Row {
		public BuildSectorCatalogListRow(InputState state, CatalogPermission catalogPermission, GUIElement... elements) {
			super(state, catalogPermission, elements);
			this.highlightSelect = true;
			this.highlightSelectSimple = true;
			this.setAllwaysOneSelected(true);
		}

		@Override
		public void extended() {
			if(!isOccluded()) super.extended();
			else super.unexpend();
		}

		@Override
		public void collapsed() {
			if(!isOccluded()) super.collapsed();
			else super.extended();
		}

		@Override
		public boolean isOccluded() {
			return spawnEntityDialog != null && spawnEntityDialog.getInputPanel() != null && spawnEntityDialog.getInputPanel().isActive();
		}
	}
}

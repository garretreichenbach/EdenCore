package thederpgamer.edencore.gui.exchangemenu;

import org.schema.game.server.data.blueprintnw.BlueprintClassification;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.forms.gui.GUIAncor;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUIElementList;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.ExchangeItem;

import java.util.*;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class ExchangeItemScrollableList extends ScrollableTableList<ExchangeItem> {

	private final GUIAncor anchor;
	private final int type;

	public ExchangeItemScrollableList(InputState state, GUIAncor anchor, int type) {
		super(state, 10, 10, anchor);
		this.anchor = anchor;
		this.type = type;
	}

	@Override
	public void initColumns() {
		addColumn(Lng.str("Name"), 15.0F, new Comparator<ExchangeItem>() {
			public int compare(ExchangeItem o1, ExchangeItem o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		addColumn(Lng.str("Producer"), 10.0f, new Comparator<ExchangeItem>() {
			@Override
			public int compare(ExchangeItem o1, ExchangeItem o2) {
				return o1.getProducer().compareTo(o2.getProducer());
			}
		});

		addColumn(Lng.str("Price"), 3.0f, new Comparator<ExchangeItem>() {
			@Override
			public int compare(ExchangeItem o1, ExchangeItem o2) {
				return Integer.compare(o1.getPrice(), o2.getPrice());
			}
		});

		addColumn(Lng.str("Category"), 10.0f, new Comparator<ExchangeItem>() {
			@Override
			public int compare(ExchangeItem o1, ExchangeItem o2) {
				return o1.getCategory().compareTo(o2.getCategory());
			}
		});

		addColumn(Lng.str("Mass"), 5.0f, new Comparator<ExchangeItem>() {
			@Override
			public int compare(ExchangeItem o1, ExchangeItem o2) {
				return Float.compare(o1.getMass(), o2.getMass());
			}
		});

		addTextFilter(new GUIListFilterText<ExchangeItem>() {
			public boolean isOk(String s, ExchangeItem item) {
				return item.getName().toLowerCase().contains(s.toLowerCase());
			}
		}, ControllerElement.FilterRowStyle.FULL);

		addTextFilter(new GUIListFilterText<ExchangeItem>() {
			public boolean isOk(String s, ExchangeItem item) {
				return item.getProducer().toLowerCase().contains(s.toLowerCase());
			}
		}, ControllerElement.FilterRowStyle.LEFT);
		switch(type) {
			case ExchangeDialog.SHIPS:
				addDropdownFilter(new GUIListFilterDropdown<ExchangeItem, BlueprintClassification>(getShipClassifications()) {
					public boolean isOk(BlueprintClassification classification, ExchangeItem item) {
						return item.getCategory().equals(classification.name());
					}

				}, new CreateGUIElementInterface<BlueprintClassification>() {
					@Override
					public GUIElement create(BlueprintClassification classification) {
						GUIAncor anchor = new GUIAncor(getState(), 10.0F, 24.0F);
						GUITextOverlayTableDropDown dropDown;
						(dropDown = new GUITextOverlayTableDropDown(10, 10, getState())).setTextSimple(classification.getName());
						dropDown.setPos(4.0F, 4.0F, 0.0F);
						anchor.setUserPointer(classification.name());
						anchor.attach(dropDown);
						return anchor;
					}

					@Override
					public GUIElement createNeutral() {
						GUIAncor anchor = new GUIAncor(getState(), 10.0F, 24.0F);
						GUITextOverlayTableDropDown dropDown;
						(dropDown = new GUITextOverlayTableDropDown(10, 10, getState())).setTextSimple(Lng.str("ALL"));
						dropDown.setPos(4.0F, 4.0F, 0.0F);
						anchor.setUserPointer("ALL");
						anchor.attach(dropDown);
						return anchor;
					}
				}, ControllerElement.FilterRowStyle.RIGHT);
				break;
			case ExchangeDialog.STATIONS:
				addDropdownFilter(new GUIListFilterDropdown<ExchangeItem, BlueprintClassification>(BlueprintClassification.stationValues().toArray(getStationClassifications())) {
					public boolean isOk(BlueprintClassification classification, ExchangeItem item) {
						return item.getCategory().equals(classification.name());
					}

				}, new CreateGUIElementInterface<BlueprintClassification>() {
					@Override
					public GUIElement create(BlueprintClassification classification) {
						GUIAncor anchor = new GUIAncor(getState(), 10.0F, 24.0F);
						GUITextOverlayTableDropDown dropDown;
						(dropDown = new GUITextOverlayTableDropDown(10, 10, getState())).setTextSimple(classification.getName());
						dropDown.setPos(4.0F, 4.0F, 0.0F);
						anchor.setUserPointer(classification.name());
						anchor.attach(dropDown);
						return anchor;
					}

					@Override
					public GUIElement createNeutral() {
						GUIAncor anchor = new GUIAncor(getState(), 10.0F, 24.0F);
						GUITextOverlayTableDropDown dropDown;
						(dropDown = new GUITextOverlayTableDropDown(10, 10, getState())).setTextSimple(Lng.str("ALL"));
						dropDown.setPos(4.0F, 4.0F, 0.0F);
						anchor.setUserPointer("ALL");
						anchor.attach(dropDown);
						return anchor;
					}
				}, ControllerElement.FilterRowStyle.RIGHT);
				break;
			case ExchangeDialog.TURRETS:
				addDropdownFilter(new GUIListFilterDropdown<ExchangeItem, ExchangeItem.TurretType>(ExchangeItem.TurretType.values()) {
					public boolean isOk(ExchangeItem.TurretType turretType, ExchangeItem item) {
						return item.getCategory().equals(turretType.name());
					}

				}, new CreateGUIElementInterface<ExchangeItem.TurretType>() {
					@Override
					public GUIElement create(ExchangeItem.TurretType turretType) {
						GUIAncor anchor = new GUIAncor(getState(), 10.0F, 24.0F);
						GUITextOverlayTableDropDown dropDown;
						(dropDown = new GUITextOverlayTableDropDown(10, 10, getState())).setTextSimple(turretType.displayName);
						dropDown.setPos(4.0F, 4.0F, 0.0F);
						anchor.setUserPointer(turretType.name());
						anchor.attach(dropDown);
						return anchor;
					}

					@Override
					public GUIElement createNeutral() {
						GUIAncor anchor = new GUIAncor(getState(), 10.0F, 24.0F);
						GUITextOverlayTableDropDown dropDown;
						(dropDown = new GUITextOverlayTableDropDown(10, 10, getState())).setTextSimple(Lng.str("ALL"));
						dropDown.setPos(4.0F, 4.0F, 0.0F);
						anchor.setUserPointer("ALL");
						anchor.attach(dropDown);
						return anchor;
					}
				}, ControllerElement.FilterRowStyle.RIGHT);
				break;
		}

		activeSortColumnIndex = 0;
	}

	@Override
	protected Collection<ExchangeItem> getElementList() {
		switch(type) {
			case ExchangeDialog.SHIPS:
				return ExchangeDialog.getShipList();
			case ExchangeDialog.STATIONS:
				return ExchangeDialog.getStationList();
			case ExchangeDialog.TURRETS:
				return ExchangeDialog.getTurretList();
			default:
				return new ArrayList<>();
		}
	}

	@Override
	public void updateListEntries(GUIElementList guiElementList, Set<ExchangeItem> set) {

	}

	private BlueprintClassification[] getShipClassifications() {
		List<BlueprintClassification> classifications = new ArrayList<>();
		for(BlueprintClassification classification : BlueprintClassification.shipValues()) {
			if(classification != BlueprintClassification.NONE && classification != BlueprintClassification.ALL_SHIPS) classifications.add(classification);
		}
		return classifications.toArray(new BlueprintClassification[0]);
	}

	private BlueprintClassification[] getStationClassifications() {
		List<BlueprintClassification> classifications = new ArrayList<>();
		for(BlueprintClassification classification : BlueprintClassification.stationValues()) {
			if(classification != BlueprintClassification.NONE && classification != BlueprintClassification.NONE_STATION) classifications.add(classification);
		}
		return classifications.toArray(new BlueprintClassification[0]);
	}
}

package thederpgamer.edencore.gui.eventsmenu;

import api.common.GameClient;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.manager.ClientCacheManager;
import thederpgamer.edencore.utils.EventUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/08/2021]
 */
public class EventsScrollableList extends ScrollableTableList<EventData> {
	private final EventsMenuPanel panel;

	public EventsScrollableList(InputState state, GUIElement p, EventsMenuPanel panel, int type) {
		super(state, panel.getWidth(), panel.getHeight(), p);
		this.panel = panel;
		p.attach(this);
	}

	@Override
	public void initColumns() {
		addColumn("Name", 10.0f, new Comparator<EventData>() {
			@Override
			public int compare(EventData o1, EventData o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		addColumn("Type", 7.5f, new Comparator<EventData>() {
			@Override
			public int compare(EventData o1, EventData o2) {
				return o1.getEventType().compareTo(o2.getEventType());
			}
		});
		addColumn("Difficulty", 5.0f, new Comparator<EventData>() {
			@Override
			public int compare(EventData o1, EventData o2) {
				return Integer.compare(o1.getDifficulty(), o2.getDifficulty());
			}
		});
		addColumn("Status", 5.0f, new Comparator<EventData>() {
			@Override
			public int compare(EventData o1, EventData o2) {
				return o1.getStatusDisplay().compareTo(o2.getStatusDisplay());
			}
		});
		addColumn("Participants", 5.0f, new Comparator<EventData>() {
			@Override
			public int compare(EventData o1, EventData o2) {
				String first = o1.getCurrentPlayers() + "/" + o1.getMaxPlayers();
				String second = o2.getCurrentPlayers() + "/" + o2.getMaxPlayers();
				return first.compareTo(second);
			}
		});
		addTextFilter(new GUIListFilterText<EventData>() {
			@Override
			public boolean isOk(String s, EventData eventData) {
				return eventData.getName().toLowerCase().contains(s.toLowerCase());
			}
		}, "SEARCH", ControllerElement.FilterRowStyle.FULL);
		addDropdownFilter(new GUIListFilterDropdown<EventData, Integer>(1, 2, 3, 4, 5) {
			@Override
			public boolean isOk(Integer integer, EventData eventData) {
				return integer == eventData.getDifficulty();
			}
		}, new CreateGUIElementInterface<Integer>() {
			@Override
			public GUIElement create(Integer i) {
				GUIAncor anchor = new GUIAncor(getState(), 10.0F, 24.0F);
				GUITextOverlayTableDropDown dropDown;
				(dropDown = new GUITextOverlayTableDropDown(10, 10, getState())).setTextSimple(i);
				dropDown.setPos(4.0F, 4.0F, 0.0F);
				anchor.setUserPointer(i);
				anchor.attach(dropDown);
				return anchor;
			}

			@Override
			public GUIElement createNeutral() {
				return null;
			}
		}, ControllerElement.FilterRowStyle.LEFT);
		addDropdownFilter(new GUIListFilterDropdown<EventData, EventData.EventType>(EventData.EventType.values()) {
			@Override
			public boolean isOk(EventData.EventType eventType, EventData eventData) {
				return eventType == eventData.getEventType() || eventType == EventData.EventType.ALL;
			}
		}, new CreateGUIElementInterface<EventData.EventType>() {
			@Override
			public GUIElement create(EventData.EventType eventType) {
				GUIAncor anchor = new GUIAncor(getState(), 10.0F, 24.0F);
				GUITextOverlayTableDropDown dropDown;
				(dropDown = new GUITextOverlayTableDropDown(10, 10, getState())).setTextSimple(eventType.name());
				dropDown.setPos(4.0F, 4.0F, 0.0F);
				anchor.setUserPointer(eventType);
				anchor.attach(dropDown);
				return anchor;
			}

			@Override
			public GUIElement createNeutral() {
				return null;
			}
		}, ControllerElement.FilterRowStyle.RIGHT);
		activeSortColumnIndex = 2;
	}

	@Override
	protected Collection<EventData> getElementList() {
		return ClientCacheManager.eventData;
	}

	@Override
	public void updateListEntries(GUIElementList guiElementList, Set<EventData> set) {
		guiElementList.deleteObservers();
		guiElementList.addObserver(this);
		for(EventData eventData : set) {
			GUIClippedRow nameRow = createRow(eventData.getName());
			GUIClippedRow typeRow = createRow(eventData.getEventType().name());
			GUIClippedRow difficultyRow = createRow(String.valueOf(eventData.getDifficulty()));
			GUIClippedRow statusRow = createRow(eventData.getStatusDisplay());
			GUIClippedRow participantsRow = createRow(eventData.getCurrentPlayers() + "/" + eventData.getMaxPlayers());
			EventsScrollableListRow listRow = new EventsScrollableListRow(getState(), eventData, nameRow, typeRow, difficultyRow, statusRow, participantsRow);
			GUIAncor anchor = new GUIAncor(getState(), panel.getWidth() - 28.0f, 28.0F);
			anchor.attach(createButtonPane(eventData, anchor));
			listRow.expanded = new GUIElementList(getState());
			listRow.expanded.add(new GUIListElement(anchor, getState()));
			listRow.expanded.attach(anchor);
			listRow.onInit();
			guiElementList.addWithoutUpdate(listRow);
		}
		guiElementList.updateDim();
	}

	private GUIClippedRow createRow(String label) {
		GUITextOverlayTable element = new GUITextOverlayTable(10, 10, getState());
		element.setTextSimple(label);
		GUIClippedRow row = new GUIClippedRow(getState());
		row.attach(element);
		return row;
	}

	private GUIElement createButtonPane(final EventData eventData, GUIAncor anchor) {
		GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 2, 1, anchor);
		buttonPane.onInit();
		GUIHorizontalButton joinLeaveButton = new GUIHorizontalButton(getState(), GUIHorizontalArea.HButtonColor.valueOf(new Object() {
			@Override
			public String toString() {
				if(EventUtils.isPlayerInEvent(GameClient.getClientPlayerState(), eventData)) return GUIHorizontalArea.HButtonColor.ORANGE.name();
				else return GUIHorizontalArea.HButtonColor.BLUE.name();
			}
		}.toString()), new Object() {
			@Override
			public String toString() {
				if(EventUtils.isPlayerInEvent(GameClient.getClientPlayerState(), eventData)) return "LEAVE";
				else return "JOIN";
			}
		}.toString(), new GUICallback() {
			@Override
			public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
				if(mouseEvent.pressedLeftMouse()) {
					if(EventUtils.isPlayerInEvent(GameClient.getClientPlayerState(), eventData)) EventUtils.addPlayerToEvent(GameClient.getClientPlayerState(), eventData);
					else EventUtils.removePlayerFromEvent(GameClient.getClientPlayerState(), eventData);
				}
			}

			@Override
			public boolean isOccluded() {
				return !isActive() || EventUtils.isPlayerInEventOther(GameClient.getClientPlayerState(), eventData);
			}
		}, buttonPane.activeInterface, new GUIActivationCallback() {
			@Override
			public boolean isVisible(InputState inputState) {
				return true;
			}

			@Override
			public boolean isActive(InputState inputState) {
				return true;
			}
		}) {
			private long lastCheck;

			@Override
			public void draw() {
				if(lastCheck == 0 || lastCheck + 1000L < System.currentTimeMillis()) {
					lastCheck = System.currentTimeMillis();
					if(EventUtils.isPlayerInEvent(GameClient.getClientPlayerState(), eventData)) {
						setColor(HButtonColor.ORANGE);
						textOverlay.setTextSimple("LEAVE");
					} else {
						setColor(HButtonColor.BLUE);
						textOverlay.setTextSimple("JOIN");
					}
				}
				super.draw();
			}
		};
		buttonPane.addButton(joinLeaveButton, 0, 0);
		GUIHorizontalButton readyButton = new GUIHorizontalButton(getState(), GUIHorizontalArea.HButtonColor.valueOf(new Object() {
			@Override
			public String toString() {
				if(eventData.getSquadMember(GameClient.getClientPlayerState()).isReady()) return GUIHorizontalArea.HButtonColor.GREEN.name();
				else return GUIHorizontalArea.HButtonColor.RED.name();
			}
		}.toString()), new Object() {
			@Override
			public String toString() {
				if(eventData.getSquadMember(GameClient.getClientPlayerState()).isReady()) return "READY [true]";
				else return "READY [false]";
			}
		}.toString(), new GUICallback() {
			@Override
			public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
				if(mouseEvent.pressedLeftMouse()) {
					eventData.getSquadMember(GameClient.getClientPlayerState()).setReady(!eventData.getSquadMember(GameClient.getClientPlayerState()).isReady());
					eventData.updateClients();
				}
			}

			@Override
			public boolean isOccluded() {
				return !isActive() || EventUtils.getCurrentEvent(GameClient.getClientPlayerState()) == null;
			}
		}, buttonPane.activeInterface, new GUIActivationCallback() {
			@Override
			public boolean isVisible(InputState inputState) {
				return true;
			}

			@Override
			public boolean isActive(InputState inputState) {
				return true;
			}
		}) {
			private long lastCheck;

			@Override
			public void draw() {
				if(lastCheck == 0 || lastCheck + 1000L < System.currentTimeMillis()) {
					lastCheck = System.currentTimeMillis();
					if(EventUtils.getCurrentEvent(GameClient.getClientPlayerState()) == null) {
						setColor(HButtonColor.GREEN);
						textOverlay.setTextSimple("READY [true]");
					} else {
						setColor(HButtonColor.RED);
						textOverlay.setTextSimple("READY [false]");
					}
				}
				super.draw();
			}
		};
		buttonPane.addButton(readyButton, 1, 0);
		return buttonPane;
	}

	public class EventsScrollableListRow extends ScrollableTableList<EventData>.Row {
		public EventsScrollableListRow(InputState state, EventData eventData, GUIElement... elements) {
			super(state, eventData, elements);
			highlightSelect = true;
			highlightSelectSimple = true;
			setAllwaysOneSelected(true);
		}
	}
}

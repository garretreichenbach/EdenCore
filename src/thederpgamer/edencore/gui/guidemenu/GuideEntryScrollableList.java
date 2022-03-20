package thederpgamer.edencore.gui.guidemenu;

import org.schema.schine.graphicsengine.core.GLFrame;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.guide.GuideEntryCategory;
import thederpgamer.edencore.data.guide.GuideEntryData;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/19/2022]
 */
public class GuideEntryScrollableList extends ScrollableTableList<GuideEntryData> {

	private final GuideMenuPanel panel;
	private final GUIElement p;

	public GuideEntryScrollableList(InputState state, GUIElement p, GuideMenuPanel panel) {
		super(state, (float) (GLFrame.getWidth() / 1.5), (float) (GLFrame.getHeight() / 1.5), p);
		this.p = p;
		this.panel = panel;
		p.attach(this);
	}

	@Override
	protected Collection<GuideEntryData> getElementList() {
		return GuideMenuControlManager.guideEntries;
	}

	@Override
	public void initColumns() {
		addColumn("Name", 15.0f, new Comparator<GuideEntryData>() {
			@Override
			public int compare(GuideEntryData o1, GuideEntryData o2) {
				return o1.name.compareTo(o2.name);
			}
		});

		addTextFilter(new GUIListFilterText<GuideEntryData>() {
			@Override
			public boolean isOk(String s, GuideEntryData guideEntryData) {
				return guideEntryData.name.toLowerCase().contains(s.toLowerCase());
			}
		}, "SEARCH", ControllerElement.FilterRowStyle.FULL);
	}

	@Override
	public void updateListEntries(GUIElementList guiElementList, Set<GuideEntryData> set) {
		guiElementList.deleteObservers();
		guiElementList.addObserver(this);
		int sortIndex = 0;
		for(GuideEntryCategory category : GuideEntryCategory.values()) {
			final ScrollableTableList<GuideEntryData>.Seperator separator = getSeperator(category.display, sortIndex);
			sortIndex ++;
			for(GuideEntryData guideEntry : set) {
				if(guideEntry.category.equals(category)) {
					GUITextOverlayTable entryTextElement;
					(entryTextElement = new GUITextOverlayTable(10, 10, this.getState())).setTextSimple(guideEntry.name);
					GUIClippedRow entryRowElement;
					(entryRowElement = new GUIClippedRow(this.getState())).attach(entryTextElement);

					GuideEntryScrollableListRow listRow = new GuideEntryScrollableListRow(getState(), guideEntry, entryRowElement);
					GUIAncor anchor = new GUIAncor(getState(), p.getWidth() - 28.0f, 28.0f);
					anchor.attach(redrawButtonPane(guideEntry, anchor));
					listRow.expanded = new GUIElementList(getState());
					listRow.expanded.add(new GUIListElement(anchor, getState()));
					listRow.expanded.attach(anchor);
					listRow.seperator = separator;
					listRow.onInit();
					guiElementList.addWithoutUpdate(listRow);
				}
			}
		}
		guiElementList.updateDim();
	}

	private GUIElement redrawButtonPane(final GuideEntryData guideEntry, GUIAncor anchor) {
		GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 1, 1, anchor);
		buttonPane.onInit();
		buttonPane.addButton(0, 0, "VIEW", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
			@Override
			public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
				if(mouseEvent.pressedLeftMouse()) {
					GuideEntryDialog dialog = new GuideEntryDialog();
					dialog.getInputPanel().createPanel(guideEntry);
					dialog.activate();
					EdenCore.getInstance().guideMenuControlManager.setActive(false);
				}
			}

			@Override
			public boolean isOccluded() {
				return false;
			}
		}, new GUIActivationCallback() {
			@Override
			public boolean isVisible(InputState inputState) {
				return true;
			}

			@Override
			public boolean isActive(InputState inputState) {
				return true;
			}
		});
		return buttonPane;
	}

	public class GuideEntryScrollableListRow extends ScrollableTableList<GuideEntryData>.Row {

		public GuideEntryScrollableListRow(InputState state, GuideEntryData guideEntryData, GUIElement... elements) {
			super(state, guideEntryData, elements);
			this.highlightSelect = true;
			this.highlightSelectSimple = true;
			this.setAllwaysOneSelected(true);
		}
	}
}

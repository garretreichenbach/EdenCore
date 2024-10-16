package thederpgamer.edencore.gui.controls;

import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.GUIActivationCallback;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUIElementList;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.settingsnew.GUISettingsElementPanelNew;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.misc.ControlBindingData;

import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class ControlBindingsScrollableList extends ScrollableTableList<ControlBindingData> implements GUIActiveInterface {

	private final ControlBindingData.ControlType controlType;

	public ControlBindingsScrollableList(InputState state, GUIElement element, ControlBindingData.ControlType controlType) {
		super(state, 100, 100, element);
		this.controlType = controlType;
	}

	@Override
	protected Collection<ControlBindingData> getElementList() {
		return ControlBindingData.getBindingsCategory(controlType);
	}

	@Override
	public void initColumns() {
		addColumn(Lng.str("Name"), 4, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
		addColumn(Lng.str("Setting"), 1, Comparator.comparingInt(ControlBindingData::getBinding));
		addTextFilter(new GUIListFilterText<ControlBindingData>() {
			@Override
			public boolean isOk(String input, ControlBindingData listElement) {
				return listElement.getName().toLowerCase(Locale.ENGLISH).contains(input.trim().toLowerCase(Locale.ENGLISH));
			}
		}, Lng.str("SEARCH BY NAME"), ControllerElement.FilterRowStyle.FULL);
	}

	@Override
	public void updateListEntries(GUIElementList guiElementList, Set<ControlBindingData> set) {
		guiElementList.deleteObservers();
		guiElementList.addObserver(this);
		for(ControlBindingData binding : set) {
			GUITextOverlayTable nameText = new GUITextOverlayTable(10, 10, getState());
			GUIHorizontalButton settingBtn = new GUIHorizontalButton(getState(), GUIHorizontalArea.HButtonType.BUTTON_RED_MEDIUM, new Object() {
				@Override
				public String toString() {
					return binding.getName();
				}
			}, new GUICallback() {
				@Override
				public void callback(GUIElement callingGuiElement, MouseEvent event) {
					if(event.pressedLeftMouse()) {
						System.err.println("PRESSED MOUSE TO ACTIVATE");
						(new ControlBindingDialog(getState(), binding)).activate();
					}
				}

				@Override
				public boolean isOccluded() {
					return !isActive();
				}
			}, this, new GUIActivationCallback() {
				@Override
				public boolean isVisible(InputState state) {
					return true;
				}

				@Override
				public boolean isActive(InputState state) {
					return ControlBindingsScrollableList.this.isActive();
				}
			}) {

				@Override
				public void draw() {
					setWidth(columns.get(1).bg.getWidth() - 25);
					HButtonType.getType(HButtonType.TEXT_FILED_LIGHT, isInside(), isActive(), false);
					setMouseUpdateEnabled(isActive());
					super.draw();
				}
			};

			settingBtn.onInit();
			settingBtn.setFont(FontLibrary.getBlenderProMedium19());
			GUISettingsElementPanelNew panelNew = new GUISettingsElementPanelNew(getState(), settingBtn, false, false);
			nameText.setTextSimple(binding.getDescription());
			nameText.getPos().y = 3;
			ControlBindingSettingsRow row = new ControlBindingSettingsRow(getState(), binding, nameText, panelNew);
			row.onInit();
			guiElementList.addWithoutUpdate(row);
		}
		guiElementList.updateDim();
	}

	public class ControlBindingSettingsRow extends ScrollableTableList<ControlBindingData>.Row {

		public ControlBindingSettingsRow(InputState state, ControlBindingData binding, GUIElement... elements) {
			super(state, binding, elements);
			highlightSelect = true;
		}
	}
}

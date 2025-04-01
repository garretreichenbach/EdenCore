package thederpgamer.edencore.gui.exchangemenu;

import api.common.GameClient;
import org.schema.game.client.controller.PlayerOkCancelInput;
import org.schema.game.client.data.GameClientState;
import org.schema.game.client.view.gui.GUIInputPanel;
import org.schema.game.common.data.player.catalog.CatalogPermission;
import org.schema.schine.common.TextAreaInput;
import org.schema.schine.common.TextCallback;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.GLFrame;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.DataManager;
import thederpgamer.edencore.data.exchangedata.ExchangeData;
import thederpgamer.edencore.data.exchangedata.ExchangeDataManager;
import thederpgamer.edencore.gui.elements.GUILabeledTextInput;

import java.util.ArrayList;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class ExchangeDataDialog extends PlayerOkCancelInput {

	public static final int ADD = 0;
	public static final int EDIT = 1;

	private static final double width = GLFrame.getWidth() / 3.0;
	private static final double height = GLFrame.getHeight() / 2.5;

	private final ExchangeDataPanel panel;

	public ExchangeDataDialog(ExchangeData data, int mode, String title) {
		super("ExchangeDataDialog", GameClient.getClientState(), (int) width, (int) height, title, "");
		(panel = new ExchangeDataPanel(getState(), this, data, mode)).onInit();
	}

	@Override
	public void onDeactivate() {

	}

	@Override
	public void handleMouseEvent(MouseEvent mouseEvent) {

	}

	@Override
	public void pressedOK() {
		ExchangeData data = panel.getData();
		switch(panel.mode) {
			case ADD:
				ExchangeDataManager.getInstance(false).sendPacket(data, DataManager.ADD_DATA, true);
				break;
			case EDIT:
				ExchangeDataManager.getInstance(false).sendPacket(data, DataManager.UPDATE_DATA, true);
				break;
		}
		deactivate();
	}

	@Override
	public ExchangeDataPanel getInputPanel() {
		return panel;
	}

	public static class ExchangeDataPanel extends GUIInputPanel {

		private final ExchangeData data;
		private final int mode;
		private CatalogPermission selectedPermission;
		private GUILabeledTextInput nameInput;
		private GUILabeledTextInput descriptionInput;
		private GUILabeledTextInput priceInput;
		private GUIDropDownList dropDownList;

		public ExchangeDataPanel(InputState state, GUICallback guiCallback, ExchangeData data, int mode) {
			super("ExchangeDataPanel", state, guiCallback, "Add To Exchange", "");
			this.data = data;
			this.mode = mode;
		}

		@Override
		public void onInit() {
			super.onInit();
			GUIContentPane contentPane = ((GUIDialogWindow) background).getMainContentPane();
			contentPane.setTextBoxHeightLast((int) (getHeight() - 50));
			nameInput = new GUILabeledTextInput(10, 10, getState(), Lng.str("Name"), GUILabeledTextInput.LEFT);
			nameInput.setTextInput(new TextAreaInput(64, 1, new TextCallback() {
				@Override
				public String[] getCommandPrefixes() {
					return new String[0];
				}

				@Override
				public String handleAutoComplete(String s, TextCallback textCallback, String s1) {
					return "";
				}

				@Override
				public void onFailedTextCheck(String s) {

				}

				@Override
				public void onTextEnter(String s, boolean b, boolean b1) {

				}

				@Override
				public void newLine() {

				}
			}));
			nameInput.onInit();
			nameInput.setText(data.getName());
			contentPane.getContent(0).attach(nameInput);

			descriptionInput = new GUILabeledTextInput(10, 10, getState(), Lng.str("Description"), GUILabeledTextInput.LEFT);
			descriptionInput.setTextInput(new TextAreaInput(512, 5, new TextCallback() {
				@Override
				public String[] getCommandPrefixes() {
					return new String[0];
				}

				@Override
				public String handleAutoComplete(String s, TextCallback textCallback, String s1) {
					return "";
				}

				@Override
				public void onFailedTextCheck(String s) {

				}

				@Override
				public void onTextEnter(String s, boolean b, boolean b1) {

				}

				@Override
				public void newLine() {

				}
			}));
			descriptionInput.onInit();
			descriptionInput.setText(data.getDescription());
			contentPane.getContent(0).attach(descriptionInput);
			descriptionInput.getPos().y += nameInput.getHeight() + 4;

			priceInput = new GUILabeledTextInput(10, 10, getState(), Lng.str("Price"), GUILabeledTextInput.LEFT);
			priceInput.setTextInput(new TextAreaInput(2, 1, new TextCallback() {
				@Override
				public String[] getCommandPrefixes() {
					return new String[0];
				}

				@Override
				public String handleAutoComplete(String s, TextCallback textCallback, String s1) {
					return "";
				}

				@Override
				public void onFailedTextCheck(String s) {

				}

				@Override
				public void onTextEnter(String s, boolean b, boolean b1) {
					if(s.trim().matches("[0-9]+")) {
						try {
							int price = Math.max(1, Integer.parseInt(s.trim()));
							priceInput.setText(String.valueOf(price));
							data.setPrice(price);
						} catch(NumberFormatException ignored) {
							priceInput.setText("1");
							data.setPrice(1);
						}
					} else {
						priceInput.setText("1");
						data.setPrice(1);
					}
				}

				@Override
				public void newLine() {

				}
			}));
			priceInput.onInit();
			priceInput.setText(String.valueOf(data.getPrice()));
			contentPane.getContent(0).attach(priceInput);
			priceInput.getPos().y += nameInput.getHeight() + descriptionInput.getHeight() + 8;

			ArrayList<CatalogPermission> catalogPermissions = new ArrayList<>();
			for(CatalogPermission permission : ((GameClientState) getState()).getCatalog().getAvailableCatalog()) {
				if(!ExchangeDataManager.getInstance(false).existsName(permission.getUid())) catalogPermissions.add(permission);
			}
			GUIAncor[] elements = new GUIAncor[catalogPermissions.size()];
			for(int i = 0; i < elements.length; i++) {
				elements[i] = new GUIAncor(getState(), 300, 24);
				elements[i].setUserPointer(catalogPermissions.get(i));
				GUITextOverlay overlay = new GUITextOverlay(300, 24, FontLibrary.FontSize.MEDIUM.getFont(), getState());
				overlay.setTextSimple(catalogPermissions.get(i).getUid());
				overlay.setPos(4, 4, 0);
				elements[i].attach(overlay);
			}
			dropDownList = new GUIDropDownList(getState(), 420, 180, 200, new DropDownCallback() {
				@Override
				public void onSelectionChanged(GUIListElement guiListElement) {
					if(guiListElement != null && guiListElement.getUserPointer() instanceof CatalogPermission) {
						selectedPermission = (CatalogPermission) guiListElement.getUserPointer();
						setData(selectedPermission);
					} else selectedPermission = null; // Reset if no valid selection
				}
			});
			dropDownList.onInit();
			contentPane.getContent(0).attach(dropDownList);
			dropDownList.getPos().y += 30;
		}

		private void setData(CatalogPermission selectedPermission) {
			data.setName(selectedPermission.getUid());
			nameInput.setText(selectedPermission.getUid());
			data.setCatalogName(selectedPermission.getUid());
			data.setDescription(selectedPermission.description);
			descriptionInput.setText(selectedPermission.description);
			data.setMass(selectedPermission.mass);
			data.setClassification(selectedPermission.getClassification());
		}

		public ExchangeData getData() {
			return data;
		}
	}
}

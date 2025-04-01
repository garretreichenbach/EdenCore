package thederpgamer.edencore.gui.exchangemenu;

import api.common.GameClient;
import org.schema.game.client.controller.PlayerInput;
import org.schema.game.client.data.GameClientState;
import org.schema.game.client.view.gui.GUIInputPanel;
import org.schema.game.common.data.player.PlayerState;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIActivationCallback;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.exchangedata.ExchangeData;
import thederpgamer.edencore.data.exchangedata.ExchangeDataManager;

import java.util.Set;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class ExchangeDialog extends PlayerInput {

	public static final int SHIPS = 0;
	public static final int STATIONS = 1;
	public static final int ITEMS = 2; // Not used in this dialog but left here for future use

	private final ExchangePanel panel;

	public ExchangeDialog() {
		super(GameClient.getClientState());
		(panel = new ExchangePanel(getState(), this)).onInit();
	}

	@Override
	public void onDeactivate() {

	}

	@Override
	public void handleMouseEvent(MouseEvent mouseEvent) {

	}

	@Override
	public ExchangePanel getInputPanel() {
		return panel;
	}

	public static Set<ExchangeData> getShipList() {
		return ExchangeDataManager.getCategory(ExchangeData.ExchangeDataCategory.SHIP);
	}

	public static Set<ExchangeData> getStationList() {
		return ExchangeDataManager.getCategory(ExchangeData.ExchangeDataCategory.STATION);
	}

	public static class ExchangePanel extends GUIInputPanel {

		private GUITabbedContent tabbedContent;

		public ExchangePanel(InputState state, GUICallback guiCallback) {
			super("ExchangePanel", state, 800, 500, guiCallback, "", "");
		}

		@Override
		public void onInit() {
			super.onInit();
			GUIContentPane contentPane = ((GUIDialogWindow) background).getMainContentPane();
			contentPane.setTextBoxHeightLast(300);
			int lastTab = 0;
			if(tabbedContent != null) {
				lastTab = tabbedContent.getSelectedTab();
				tabbedContent.clearTabs();
			}
			tabbedContent = new GUITabbedContent(getState(), contentPane.getContent(0));
			tabbedContent.onInit();
			final PlayerState playerState = ((GameClientState) getState()).getPlayer();

			GUIContentPane shipsTab = tabbedContent.addTab(Lng.str("SHIPS"));
			shipsTab.setTextBoxHeightLast(28);
			GUIHorizontalButtonTablePane shipsAddButton = new GUIHorizontalButtonTablePane(getState(), 1, 1, shipsTab.getContent(0));
			shipsAddButton.onInit();
			shipsAddButton.addButton(0, 0, Lng.str("ADD"), GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						(new AddExchangeBlueprintDialog(GameClient.getClientState(), SHIPS)).activate();
					}
				}

				@Override
				public boolean isOccluded() {
					return playerState.getFactionId() == 0;
				}
			}, new GUIActivationCallback() {
				@Override
				public boolean isVisible(InputState inputState) {
					return true;
				}

				@Override
				public boolean isActive(InputState inputState) {
					return playerState.getFactionId() != 0;
				}
			});
			shipsTab.getContent(0).attach(shipsAddButton);

			shipsTab.addNewTextBox(300);
			ExchangeItemScrollableList shipsList = new ExchangeItemScrollableList(getState(), shipsTab.getContent(1), SHIPS);
			shipsList.onInit();
			shipsTab.getContent(1).attach(shipsList);

			GUIContentPane stationsTab = tabbedContent.addTab(Lng.str("STATIONS"));
			stationsTab.setTextBoxHeightLast(28);
			GUIHorizontalButtonTablePane stationsAddButton = new GUIHorizontalButtonTablePane(getState(), 1, 1, stationsTab.getContent(0));
			stationsAddButton.onInit();
			stationsAddButton.addButton(0, 0, Lng.str("ADD"), GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						(new AddExchangeBlueprintDialog(GameClient.getClientState(), STATIONS)).activate();
					}
				}

				@Override
				public boolean isOccluded() {
					return playerState.getFactionId() == 0;
				}
			}, new GUIActivationCallback() {
				@Override
				public boolean isVisible(InputState inputState) {
					return true;
				}

				@Override
				public boolean isActive(InputState inputState) {
					return playerState.getFactionId() != 0;
				}
			});
			stationsTab.getContent(0).attach(stationsAddButton);

			stationsTab.addNewTextBox(300);
			ExchangeItemScrollableList stationsList = new ExchangeItemScrollableList(getState(), stationsTab.getContent(1), STATIONS);
			stationsList.onInit();
			stationsTab.getContent(1).attach(stationsList);

			tabbedContent.setSelectedTab(lastTab);
			contentPane.getContent(0).attach(tabbedContent);
		}
	}
}

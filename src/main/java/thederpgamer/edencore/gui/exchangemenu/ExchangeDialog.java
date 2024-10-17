package thederpgamer.edencore.gui.exchangemenu;

import api.common.GameClient;
import org.schema.game.client.controller.PlayerInput;
import org.schema.game.client.data.GameClientState;
import org.schema.game.client.view.gui.GUIInputPanel;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.server.data.blueprintnw.BlueprintClassification;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.GLFrame;
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
			super("ExchangePanel", state, guiCallback, GLFrame.getWidth() / 2, GLFrame.getHeight() / 1.5);
		}

		@Override
		public void onInit() {
			super.onInit();
			GUIContentPane contentPane = ((GUIDialogWindow) background).getMainContentPane();
			contentPane.setTextBoxHeightLast((int) (getHeight() - 50));
			int lastTab = 0;
			if(tabbedContent != null) {
				lastTab = tabbedContent.getSelectedTab();
				tabbedContent.clearTabs();
			}
			tabbedContent = new GUITabbedContent(getState(), contentPane.getContent(0));
			tabbedContent.onInit();
			PlayerState playerState = ((GameClientState) getState()).getPlayer();

			GUIContentPane shipsTab = tabbedContent.addTab(Lng.str("SHIPS"));
			ExchangeItemScrollableList shipsList = new ExchangeItemScrollableList(getState(), shipsTab.getContent(0), SHIPS);
			shipsList.onInit();
			shipsTab.getContent(0).attach(shipsList);
			shipsTab.addNewTextBox(28);
			GUIHorizontalButtonTablePane shipsAddButton = new GUIHorizontalButtonTablePane(getState(), 1, 1, shipsTab.getContent(1));
			shipsAddButton.onInit();
			shipsAddButton.addButton(0, 0, Lng.str("ADD"), GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						(new ExchangeDataDialog(new ExchangeData("New Ship", "N/A", "No description provided.", playerState.getFactionName(), 1, ExchangeData.ExchangeDataCategory.SHIP, BlueprintClassification.NONE, 0.0f), ExchangeDataDialog.ADD, Lng.str("Add Ship"))).activate();
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
			shipsTab.getContent(1).attach(shipsAddButton);

			GUIContentPane stationsTab = tabbedContent.addTab(Lng.str("STATIONS"));
			ExchangeItemScrollableList stationsList = new ExchangeItemScrollableList(getState(), stationsTab.getContent(0), STATIONS);
			stationsList.onInit();
			stationsTab.getContent(0).attach(stationsList);
			stationsTab.addNewTextBox(28);
			GUIHorizontalButtonTablePane stationsAddButton = new GUIHorizontalButtonTablePane(getState(), 1, 1, stationsTab.getContent(1));
			stationsAddButton.onInit();
			stationsAddButton.addButton(0, 0, Lng.str("ADD"), GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						(new ExchangeDataDialog(new ExchangeData("New Station", "N/A", "No description provided.", playerState.getFactionName(), 1, ExchangeData.ExchangeDataCategory.STATION, BlueprintClassification.NONE_STATION, 0.0f), ExchangeDataDialog.ADD, Lng.str("Add Station"))).activate();
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
			stationsTab.getContent(1).attach(stationsAddButton);
			
			tabbedContent.setSelectedTab(lastTab);
			contentPane.getContent(0).attach(tabbedContent);
		}
	}
}

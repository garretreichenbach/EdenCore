package thederpgamer.edencore.gui.exchangemenu;

import api.common.GameClient;
import org.schema.game.client.controller.PlayerInput;
import org.schema.game.client.view.gui.GUIInputPanel;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.GLFrame;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUITabbedContent;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.exchangedata.ExchangeData;
import thederpgamer.edencore.data.exchangedata.ExchangeDataManager;
import thederpgamer.edencore.element.ElementManager;

import java.util.Collections;
import java.util.List;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class ExchangeDialog extends PlayerInput {

	public static final int SHIPS = 0;
	public static final int STATIONS = 1;

	public static final short BRONZE = ElementManager.getItem("Bronze Bar").getId();
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

	public static List<ExchangeData> getShipList() {
		return ExchangeDataManager.getInstance().getCategory(ExchangeDataManager.Category.SHIP);
	}

	public static List<ExchangeData> getStationList() {
		return Collections.emptyList();
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
			int lastTab = 0;
			if(tabbedContent != null) {
				lastTab = tabbedContent.getSelectedTab();
				tabbedContent.clearTabs();
			}
			tabbedContent = new GUITabbedContent(getState(), contentPane.getContent(0));
			tabbedContent.onInit();

			GUIContentPane shipsTab = tabbedContent.addTab(Lng.str("SHIPS"));
			ExchangeItemScrollableList shipsList = new ExchangeItemScrollableList(getState(), shipsTab.getContent(0), SHIPS);
			shipsList.onInit();
			shipsTab.getContent(0).attach(shipsList);

			GUIContentPane stationsTab = tabbedContent.addTab(Lng.str("STATIONS"));
			ExchangeItemScrollableList stationsList = new ExchangeItemScrollableList(getState(), stationsTab.getContent(0), STATIONS);
			stationsList.onInit();
			stationsTab.getContent(0).attach(stationsList);
			
			tabbedContent.setSelectedTab(lastTab);
//			contentPane.setTextBoxHeight(0, (int) tabbedContent.getHeight());
			contentPane.getContent(0).attach(tabbedContent);
		}
	}

	/*
	public static class ExchangeMenuPanel extends GUIMenuPanel {

		protected static short BRONZE;
		protected static short SILVER;
//		protected static short GOLD;

		private int lastBPTab;
		private BlueprintExchangeItem lastClickedBP;
		private ResourceExchangeItem lastClickedResource;
		private ItemExchangeItem lastClickedItem;

		private GUITabbedContent bpTabbedContent;
		private final int horizontalOffset = 107;
		private final int verticalOffset = 139;

		public ExchangeMenuPanel(InputState inputState) {
			super(inputState, "EXCHANGE_MENU", 850, 650);
			BRONZE = ElementManager.getItem("Bronze Bar").getId();
			SILVER = ElementManager.getItem("Silver Bar").getId();
//			GOLD = ElementManager.getItem("Gold Bar").getId();
		}

		@Override
		public void recreateTabs() {
			PacketUtil.sendPacketToServer(new RequestClientCacheUpdatePacket());
			if(guiWindow == null) return;
			int lastTab = guiWindow.getSelectedTab();
			if(!guiWindow.getTabs().isEmpty()) guiWindow.clearTabs();

			GUIContentPane blueprintsTab = guiWindow.addTab("BLUEPRINTS");
			blueprintsTab.setTextBoxHeightLast((int) (guiWindow.getHeight() - verticalOffset));
			createBlueprintsTab(blueprintsTab);

			guiWindow.setSelectedTab(lastTab);
		}

		private void createBlueprintsTab(GUIContentPane tab) {
			GUIAncor tabAnchor = new GUIAncor(getState(), guiWindow.getWidth() - horizontalOffset, guiWindow.getHeight() - verticalOffset) {
				@Override
				public void draw() {
					setWidth(guiWindow.getWidth() - horizontalOffset);
					setHeight(guiWindow.getHeight() - verticalOffset);
					super.draw();
				}
			};
			if(bpTabbedContent == null) (bpTabbedContent = new GUITabbedContent(getState(), tabAnchor)).onInit();
			else {
				lastBPTab = bpTabbedContent.getSelectedTab();
				bpTabbedContent.clearTabs();
			}
			GUIContentPane shipsTab = bpTabbedContent.addTab("SHIPS");
			shipsTab.setTextBoxHeightLast((int) (guiWindow.getHeight() - verticalOffset));
			shipsTab.orientateInsideFrame();
			GUIScrollablePanel scrollPanel = new GUIScrollablePanel(1, 1, shipsTab.getContent(0), getState());
			scrollPanel.setScrollable(GUIScrollablePanel.SCROLLABLE_VERTICAL);
			scrollPanel.setLeftRightClipOnly = true;
			GUITilePane<BlueprintExchangeItem> shipsTilePane = new GUITilePane<>(getState(), scrollPanel, 230, 350);
			shipsTilePane.onInit();
			for(final BlueprintExchangeItem item : getBlueprints("SHIP")) {
				GUITile tile = shipsTilePane.addButtonTile("EXCHANGE", item.createDescription(), getTileColor(item), new GUICallback() {
					@Override
					public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
						if(mouseEvent.pressedLeftMouse()) {
							if(mouseEvent.pressedLeftMouse()) {
								if(canAffordItem(item)) {
									(new PlayerOkCancelInput("ConfirmExchangePanel", getState(), "CONFIRM EXCHANGE", "Are you sure you wish to exchange " + item.price + " " + getBarTypeName(item) + "s for this item?") {
										@Override
										public void onDeactivate() {
										}

										@Override
										public void pressedOK() {
											GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - highlight 1");
											item.community = false;
											givePlayerItem(item);
											InventoryUtils.consumeItems(GameClient.getClientPlayerState().getInventory(), item.barType, item.price);
											lastClickedBP = null;
											deactivate();
										}
									}).activate();
								} else GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - error 1");
							}
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
				setIcon(item, tile);
			}

			tab.addNewTextBox(0, 18);
			GUIHorizontalButtonTablePane adminPane = new GUIHorizontalButtonTablePane(getState(), 2, 1, tab.getContent(1));
			adminPane.onInit();
			adminPane.addButton(0, 0, "ADD", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						AddBlueprintExchangeDialog dialog = new AddBlueprintExchangeDialog();
						dialog.community = true;
						dialog.activate();
						recreateTabs();
					}
				}

				@Override
				public boolean isOccluded() {
					return !getState().getController().getPlayerInputs().isEmpty();
				}
			}, new GUIActivationCallback() {
				@Override
				public boolean isVisible(InputState inputState) {
					return true;
				}

				@Override
				public boolean isActive(InputState inputState) {
					return getState().getController().getPlayerInputs().isEmpty();
				}
			});
			adminPane.addButton(1, 0, "REMOVE", GUIHorizontalArea.HButtonColor.ORANGE, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse() && lastClickedBP != null && lastClickedBP.seller != null && lastClickedBP.seller.equals(GameClient.getClientPlayerState().getName())) {
						(new PlayerOkCancelInput("ConfirmExchangeRemovalPanel", getState(), "CONFIRM REMOVAL", "Are you sure you wish to remove this item?") {
							@Override
							public void onDeactivate() {
							}

							@Override
							public void pressedOK() {
								sendExchangeItemRemoval(BLUEPRINT, lastClickedBP);
								recreateTabs();
								deactivate();
							}
						}).activate();
					}
				}

				@Override
				public boolean isOccluded() {
					return !getState().getController().getPlayerInputs().isEmpty();
				}
			}, new GUIActivationCallback() {
				@Override
				public boolean isVisible(InputState inputState) {
					return true;
				}

				@Override
				public boolean isActive(InputState inputState) {
					return getState().getController().getPlayerInputs().isEmpty();
				}
			});
			tab.getContent(1).attach(adminPane);
			bpTabbedContent.setSelectedTab(lastBPTab);
		}

		private List<BlueprintExchangeItem> getBlueprints(String entityType) {
			ArrayList<BlueprintExchangeItem> allBlueprints = ClientCacheManager.blueprintExchangeItems;
			ArrayList<BlueprintExchangeItem> blueprints = new ArrayList<>();
			for(BlueprintExchangeItem item : allBlueprints) {
				if(item.entityType.equals(entityType)) blueprints.add(item);
			}
			return blueprints;
		}

		private void setIcon(ExchangeItem item, GUITile tile) {
			GUIOverlay spriteOverlay = item.getIcon();
			spriteOverlay.onInit();
			if(spriteOverlay.getUserPointer() == null) spriteOverlay.setUserPointer("default-icon");
			spriteOverlay.setPos(100.0f, 230.0f, 0.0f);
			tile.getContent().attach(spriteOverlay);
		}

		private boolean canAffordItem(ExchangeItem item) {
			short barType = item.barType;
			int count = InventoryUtils.getItemAmount(GameClient.getClientPlayerState().getInventory(), barType);
			return (count >= item.price && GameClient.getClientPlayerState().getInventory().hasFreeSlot()) || (GameClient.getClientPlayerState().isCreativeModeEnabled() && isAdmin() && !DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState()));
		}

		public void givePlayerItem(ExchangeItem item) {
			Inventory inventory = GameClient.getClientPlayerState().getInventory();
			if(item instanceof BlueprintExchangeItem) {
				BlueprintPlayerHandleRequest req = new BlueprintPlayerHandleRequest();
				req.catalogName = item.name;
				if(((BlueprintExchangeItem) item).community) req.entitySpawnName = "EDENCORE_TEMP_COMMUNITY";
				else req.entitySpawnName = "EDENCORE_TEMP";
				req.save = false;
				req.toSaveShip = -1;
				req.directBuy = false;
				req.setOwnFaction = true;
				GameClient.getClientPlayerState().getNetworkObject().catalogPlayerHandleBuffer.add(new RemoteBlueprintPlayerRequest(req, false));
				if(((BlueprintExchangeItem) item).community) PacketUtil.sendPacketToServer(new PlayerBuyBPPacket((BlueprintExchangeItem) item));
			} else if(item instanceof ResourceExchangeItem) InventoryUtils.addItem(inventory, ((ResourceExchangeItem) item).itemId, ((ResourceExchangeItem) item).itemCount);
			else if(item instanceof ItemExchangeItem) {
				ItemExchangeItem itemExchangeItem = (ItemExchangeItem) item;
				PacketUtil.sendPacketToServer(new RequestMetaObjectPacket(itemExchangeItem.itemId, itemExchangeItem.metaId, itemExchangeItem.subType));
			}
		}

		private GUIHorizontalArea.HButtonColor getTileColor(ExchangeItem item) {
			if(canAffordItem(item)) {
				if(item instanceof BlueprintExchangeItem) return GUIHorizontalArea.HButtonColor.BLUE;
				else if(item instanceof ResourceExchangeItem) return GUIHorizontalArea.HButtonColor.YELLOW;
			}
			return GUIHorizontalArea.HButtonColor.RED;
		}

		private boolean isAdmin() {
			return GameClient.getClientPlayerState().isAdmin();
		}

		private void sendExchangeItemRemoval(int type, ExchangeItem item) {
			PacketUtil.sendPacketToServer(new ExchangeItemRemovePacket(type, item));
		}

		private String getBarTypeName(ExchangeItem item) {
			return ElementManager.getItem(item.barType).getItemInfo().getName();
		}
	}

	 */
}

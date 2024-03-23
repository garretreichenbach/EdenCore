package thederpgamer.edencore.gui.exchangemenu;

import api.common.GameClient;
import api.network.packets.PacketUtil;
import api.utils.game.inventory.InventoryUtils;
import api.utils.gui.GUIControlManager;
import api.utils.gui.GUIMenuPanel;
import org.schema.game.client.controller.PlayerOkCancelInput;
import org.schema.game.common.data.player.BlueprintPlayerHandleRequest;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.game.network.objects.remote.RemoteBlueprintPlayerRequest;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.exchange.BlueprintExchangeItem;
import thederpgamer.edencore.data.exchange.ExchangeItem;
import thederpgamer.edencore.data.exchange.ItemExchangeItem;
import thederpgamer.edencore.data.exchange.ResourceExchangeItem;
import thederpgamer.edencore.element.ElementManager;
import thederpgamer.edencore.manager.ClientCacheManager;
import thederpgamer.edencore.network.client.exchange.ExchangeItemRemovePacket;
import thederpgamer.edencore.network.client.exchange.PlayerBuyBPPacket;
import thederpgamer.edencore.network.client.misc.RequestClientCacheUpdatePacket;
import thederpgamer.edencore.network.client.misc.RequestMetaObjectPacket;
import thederpgamer.edencore.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class ExchangeMenu extends GUIControlManager {

	public static final int BLUEPRINT = 0;
	public static final int RESOURCE = 1;
	public static final int ITEM = 2;

	public ExchangeMenu() {
		super(GameClient.getClientState());
	}

	@Override
	public ExchangeMenuPanel createMenuPanel() {
		return new ExchangeMenuPanel(getState());
	}

	@Override
	public ExchangeMenuPanel getMenuPanel() {
		return (ExchangeMenuPanel) super.getMenuPanel();
	}

	public static class ExchangeMenuPanel extends GUIMenuPanel {

///		protected static short BRONZE;
//		protected static short SILVER;
		protected static short GOLD;

		private BlueprintExchangeItem lastClickedBP;
		private ResourceExchangeItem lastClickedResource;
		private ItemExchangeItem lastClickedItem;

		private GUITabbedContent bpTabbedContent;
		private int lastBPTab;

		public ExchangeMenuPanel(InputState inputState) {
			super(inputState, "EXCHANGE_MENU", 850, 650);
//			BRONZE = ElementManager.getItem("Bronze Bar").getId();
//			SILVER = ElementManager.getItem("Silver Bar").getId();
			GOLD = ElementManager.getItem("Gold Bar").getId();
		}

		@Override
		public void recreateTabs() {
			PacketUtil.sendPacketToServer(new RequestClientCacheUpdatePacket());
			if(guiWindow == null) return;
			int lastTab = guiWindow.getSelectedTab();
			if(!guiWindow.getTabs().isEmpty()) guiWindow.clearTabs();

			GUIContentPane blueprintsTab = guiWindow.addTab("BLUEPRINTS");
			blueprintsTab.setTextBoxHeightLast(600);
			createBlueprintsTab(blueprintsTab);

			guiWindow.setSelectedTab(lastTab);
		}

		private void createBlueprintsTab(GUIContentPane tab) {
			GUIAncor tabAnchor = new GUIAncor(getState(), guiWindow.getInnerWidth() - 20, guiWindow.getInnerHeigth() - 13);
			if(bpTabbedContent == null) (bpTabbedContent = new GUITabbedContent(getState(), tabAnchor)).onInit();
			else {
				lastBPTab = bpTabbedContent.getSelectedTab();
				bpTabbedContent.clearTabs();
			}
			GUIContentPane shipsTab = bpTabbedContent.addTab("SHIPS");
			shipsTab.setTextBoxHeightLast(600);
			shipsTab.orientateInsideFrame();
			GUIScrollablePanel scrollPanel = new GUIScrollablePanel(1, 1, shipsTab.getContent(0), getState());
			scrollPanel.setScrollable(GUIScrollablePanel.SCROLLABLE_VERTICAL);
			scrollPanel.setLeftRightClipOnly = true;
			GUITilePane<BlueprintExchangeItem> shipsTilePane = new GUITilePane<>(getState(), scrollPanel, 230, 350);
			shipsTilePane.onInit();
			for(final BlueprintExchangeItem item : getBlueprints("SHIP")) {
				if(item.community) continue;
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

//			if(isAdmin()) {
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
//			}
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
}

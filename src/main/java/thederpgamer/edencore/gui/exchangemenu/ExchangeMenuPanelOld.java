package thederpgamer.edencore.gui.exchangemenu;

import api.common.GameClient;
import api.network.packets.PacketUtil;
import api.utils.game.inventory.InventoryUtils;
import api.utils.gui.GUIMenuPanel;
import org.schema.game.client.controller.PlayerOkCancelInput;
import org.schema.game.common.data.player.BlueprintPlayerHandleRequest;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.game.network.objects.remote.RemoteBlueprintPlayerRequest;
import org.schema.schine.graphicsengine.core.GLFrame;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
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
import thederpgamer.edencore.utils.APIUtils;
import thederpgamer.edencore.utils.DataUtils;

import javax.vecmath.Vector3f;
import java.util.ArrayList;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/17/2021]
 */
public class ExchangeMenuPanelOld extends GUIMenuPanel {
	public static short BRONZE;
	public static short SILVER;
	public static short GOLD;
	private BlueprintExchangeItem lastClickedBP;
	private ResourceExchangeItem lastClickedResource;
	private ItemExchangeItem lastClickedItem;
	private GUITabbedContent bpTabbedContent;
	private GUITabbedContent resourcesTabbedContent;
	private int lastBPTab;
	private int lastResourceTab;

	public ExchangeMenuPanelOld(InputState inputState) {
		super(inputState, "ServerExchange", (int) (GLFrame.getWidth() / 1.5), (int) (GLFrame.getHeight() / 1.5));
		BRONZE = ElementManager.getItem("Bronze Bar").getId();
		SILVER = ElementManager.getItem("Silver Bar").getId();
		GOLD = ElementManager.getItem("Gold Bar").getId();
	}

	@Override
	public void recreateTabs() {
		PacketUtil.sendPacketToServer(new RequestClientCacheUpdatePacket());
		if(guiWindow == null) return;
		int lastTab = guiWindow.getSelectedTab();
		if(guiWindow.getTabs().size() > 0) guiWindow.clearTabs();
		GUIContentPane blueprintsTab = guiWindow.addTab("ENTITIES");
		blueprintsTab.setTextBoxHeightLast((int) (GLFrame.getHeight() / 1.5));
		createBlueprintsTab(blueprintsTab);
		GUIContentPane resourcesTab;
		if(APIUtils.isRRSInstalled()) resourcesTab = guiWindow.addTab("COMPONENTS");
		else resourcesTab = guiWindow.addTab("RESOURCES");
		resourcesTab.setTextBoxHeightLast((int) (GLFrame.getHeight() / 1.5));
		createResourcesTab(resourcesTab);
		GUIContentPane itemsTab = guiWindow.addTab("ITEMS");
		itemsTab.setTextBoxHeightLast((int) (GLFrame.getHeight() / 1.5));
		createItemsTab(itemsTab);
		GUIContentPane exchangeTab = guiWindow.addTab("EXCHANGE");
		exchangeTab.setTextBoxHeightLast((int) (GLFrame.getHeight() / 1.5));
		createExchangeTab(exchangeTab);
		guiWindow.setSelectedTab(lastTab);
		lastClickedBP = null;
		lastClickedResource = null;
		lastClickedItem = null;
	}

	private void createBlueprintsTab(GUIContentPane contentPane) {
		GUIAncor tabAnchor = new GUIAncor(getState(), guiWindow.getInnerWidth() - 20, guiWindow.getInnerHeigth() - 13);
		if(bpTabbedContent == null) (bpTabbedContent = new GUITabbedContent(getState(), tabAnchor)).onInit();
		else {
			lastBPTab = bpTabbedContent.getSelectedTab();
			bpTabbedContent.clearTabs();
		}
		{ //Server Tab
			GUIContentPane subTab = bpTabbedContent.addTab("SERVER");
			subTab.setTextBoxHeightLast((int) (GLFrame.getHeight() / 1.5));
			subTab.orientateInsideFrame();
			GUIScrollablePanel scrollPanel = new GUIScrollablePanel(1, 1, subTab.getContent(0), getState());
			scrollPanel.setScrollable(GUIScrollablePanel.SCROLLABLE_VERTICAL);
			scrollPanel.setLeftRightClipOnly = true;
			GUITilePane<BlueprintExchangeItem> blueprintsTilePane;
			(blueprintsTilePane = new GUITilePane<>(getState(), scrollPanel, 200, 300)).onInit();
			for(final BlueprintExchangeItem item : getBlueprints()) {
				if(item.community) continue;
				GUITile tile = blueprintsTilePane.addButtonTile("EXCHANGE", item.createDescription(), getTileColor(item), new GUICallback() {
					@Override
					public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
						if(mouseEvent.pressedLeftMouse()) {
							if(mouseEvent.pressedLeftMouse()) {
								lastClickedBP = item;
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
			scrollPanel.setContent(blueprintsTilePane);
			scrollPanel.onInit();
			subTab.getContent(0).attach(scrollPanel);
			if(isAdmin()) {
				subTab.setTextBoxHeightLast(0, guiWindow.getInnerHeigth() - 90);
				subTab.addNewTextBox(0, 18);
				GUIHorizontalButtonTablePane adminPane = new GUIHorizontalButtonTablePane(getState(), 2, 1, subTab.getContent(1));
				adminPane.onInit();
				adminPane.addButton(0, 0, "ADD", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
					@Override
					public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
						if(mouseEvent.pressedLeftMouse()) {
							AddBlueprintExchangeDialog dialog = new AddBlueprintExchangeDialog();
							dialog.community = false;
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
						if(mouseEvent.pressedLeftMouse() && lastClickedBP != null) {
							(new PlayerOkCancelInput("ConfirmExchangeRemovalPanel", getState(), "CONFIRM REMOVAL", "Are you sure you wish to remove this item?") {
								@Override
								public void onDeactivate() {
								}

								@Override
								public void pressedOK() {
									sendExchangeItemRemoval(0, lastClickedBP);
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
				subTab.getContent(1).attach(adminPane);
			}
		}
		{ //Community Tab
			GUIContentPane subTab = bpTabbedContent.addTab("COMMUNITY");
			subTab.setTextBoxHeightLast((int) (GLFrame.getHeight() / 1.5));
			subTab.orientateInsideFrame();
			GUIScrollablePanel scrollPanel = new GUIScrollablePanel(1, 1, subTab.getContent(0), getState());
			scrollPanel.setScrollable(GUIScrollablePanel.SCROLLABLE_VERTICAL);
			scrollPanel.setLeftRightClipOnly = true;
			GUITilePane<BlueprintExchangeItem> blueprintsTilePane;
			(blueprintsTilePane = new GUITilePane<>(getState(), scrollPanel, 200, 300)).onInit();
			for(final BlueprintExchangeItem item : getBlueprints()) {
				if(!item.community) continue;
				GUITile tile = blueprintsTilePane.addButtonTile("EXCHANGE", item.createDescription(), getTileColor(item), new GUICallback() {
					@Override
					public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
						if(mouseEvent.pressedLeftMouse()) {
							if(mouseEvent.pressedLeftMouse()) {
								lastClickedBP = item;
								if(canAffordItem(item)) {
									(new PlayerOkCancelInput("ConfirmExchangePanel", getState(), "CONFIRM EXCHANGE", "Are you sure you wish to exchange " + item.price + " " + getBarTypeName(item) + "s for this item?") {
										@Override
										public void onDeactivate() {
										}

										@Override
										public void pressedOK() {
											GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - highlight 1");
											item.community = true;
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
			scrollPanel.setContent(blueprintsTilePane);
			scrollPanel.onInit();
			subTab.getContent(0).attach(scrollPanel);
			subTab.setTextBoxHeightLast(0, guiWindow.getInnerHeigth() - 90);
			subTab.addNewTextBox(0, 18);
			GUIHorizontalButtonTablePane adminPane = new GUIHorizontalButtonTablePane(getState(), 2, 1, subTab.getContent(1));
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
					if(mouseEvent.pressedLeftMouse() && lastClickedBP != null) {
						(new PlayerOkCancelInput("ConfirmExchangeRemovalPanel", getState(), "CONFIRM REMOVAL", "Are you sure you wish to remove this item?") {
							@Override
							public void onDeactivate() {
							}

							@Override
							public void pressedOK() {
								sendExchangeItemRemoval(0, lastClickedBP);
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
			subTab.getContent(1).attach(adminPane);
		}
		tabAnchor.attach(bpTabbedContent);
		contentPane.getContent(0).attach(tabAnchor);
		bpTabbedContent.setSelectedTab(lastBPTab);
	}

	private void createResourcesTab(GUIContentPane contentPane) {
		GUIAncor tabAnchor = new GUIAncor(getState(), guiWindow.getInnerWidth() - 20, guiWindow.getInnerHeigth() - 13);
		if(resourcesTabbedContent == null) (resourcesTabbedContent = new GUITabbedContent(getState(), tabAnchor)).onInit();
		else {
			lastResourceTab = resourcesTabbedContent.getSelectedTab();
			resourcesTabbedContent.clearTabs();
		}
		{ // Bronze Tab
			GUIContentPane subTab = resourcesTabbedContent.addTab("BRONZE");
			subTab.setTextBoxHeightLast((int) (GLFrame.getHeight() / 1.5));
			subTab.orientateInsideFrame();
			GUIScrollablePanel scrollPanel = new GUIScrollablePanel(1, 1, subTab.getContent(0), getState());
			scrollPanel.setScrollable(GUIScrollablePanel.SCROLLABLE_VERTICAL);
			scrollPanel.setLeftRightClipOnly = true;
			GUITilePane<ResourceExchangeItem> resourcesTilePane;
			(resourcesTilePane = new GUITilePane<>(getState(), scrollPanel, 200, 300)).onInit();
			for(final ResourceExchangeItem item : getResources()) {
				if(item.barType == BRONZE) {
					GUITile tile = resourcesTilePane.addButtonTile("EXCHANGE", item.createDescription(), getTileColor(item), new GUICallback() {
						@Override
						public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
							if(mouseEvent.pressedLeftMouse()) {
								lastClickedResource = item;
								if(canAffordItem(item)) {
									(new PlayerOkCancelInput("ConfirmExchangePanel", getState(), "CONFIRM EXCHANGE", "Are you sure you wish to exchange " + item.price + " " + getBarTypeName(item) + "s for this item?") {
										@Override
										public void onDeactivate() {
										}

										@Override
										public void pressedOK() {
											GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - highlight 1");
											givePlayerItem(item);
											InventoryUtils.consumeItems(GameClient.getClientPlayerState().getInventory(), item.barType, item.price);
											lastClickedResource = null;
											deactivate();
										}
									}).activate();
								} else GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - error 1");
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
							return true;
						}
					});
					setIcon(item, tile);
				}
			}
			scrollPanel.setContent(resourcesTilePane);
			scrollPanel.onInit();
			subTab.getContent(0).attach(scrollPanel);
			if(isAdmin()) {
				subTab.setTextBoxHeightLast(0, guiWindow.getInnerHeigth() - 90);
				subTab.addNewTextBox(18);
				GUIHorizontalButtonTablePane adminPane = new GUIHorizontalButtonTablePane(getState(), 2, 1, subTab.getContent(1));
				adminPane.onInit();
				adminPane.addButton(0, 0, "ADD", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
					@Override
					public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
						if(mouseEvent.pressedLeftMouse()) {
							(new AddResourceExchangeDialog()).activate();
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
						if(mouseEvent.pressedLeftMouse() && lastClickedResource != null) {
							(new PlayerOkCancelInput("ConfirmExchangeRemovalPanel", getState(), "CONFIRM REMOVAL", "Are you sure you wish to remove this item?") {
								@Override
								public void onDeactivate() {
								}

								@Override
								public void pressedOK() {
									sendExchangeItemRemoval(1, lastClickedResource);
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
				subTab.getContent(1).attach(adminPane);
			}
		}
		{ // Silver Tab
			GUIContentPane subTab = resourcesTabbedContent.addTab("SILVER");
			subTab.setTextBoxHeightLast((int) (GLFrame.getHeight() / 1.5));
			subTab.orientateInsideFrame();
			GUIScrollablePanel scrollPanel = new GUIScrollablePanel(1, 1, subTab.getContent(0), getState());
			scrollPanel.setScrollable(GUIScrollablePanel.SCROLLABLE_VERTICAL);
			scrollPanel.setLeftRightClipOnly = true;
			GUITilePane<ResourceExchangeItem> resourcesTilePane;
			(resourcesTilePane = new GUITilePane<>(getState(), scrollPanel, 200, 300)).onInit();
			for(final ResourceExchangeItem item : getResources()) {
				if(item.barType == SILVER) {
					GUITile tile = resourcesTilePane.addButtonTile("EXCHANGE", item.createDescription(), getTileColor(item), new GUICallback() {
						@Override
						public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
							if(mouseEvent.pressedLeftMouse()) {
								lastClickedResource = item;
								if(canAffordItem(item)) {
									(new PlayerOkCancelInput("ConfirmExchangePanel", getState(), "CONFIRM EXCHANGE", "Are you sure you wish to exchange " + item.price + " " + getBarTypeName(item) + "s for this item?") {
										@Override
										public void onDeactivate() {
										}

										@Override
										public void pressedOK() {
											GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - highlight 1");
											givePlayerItem(item);
											InventoryUtils.consumeItems(GameClient.getClientPlayerState().getInventory(), item.barType, item.price);
											lastClickedResource = null;
											deactivate();
										}
									}).activate();
								} else GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - error 1");
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
							return true;
						}
					});
					setIcon(item, tile);
				}
			}
			scrollPanel.setContent(resourcesTilePane);
			scrollPanel.onInit();
			subTab.getContent(0).attach(scrollPanel);
			if(isAdmin()) {
				subTab.setTextBoxHeightLast(0, guiWindow.getInnerHeigth() - 90);
				subTab.addNewTextBox(18);
				GUIHorizontalButtonTablePane adminPane = new GUIHorizontalButtonTablePane(getState(), 2, 1, subTab.getContent(1));
				adminPane.onInit();
				adminPane.addButton(0, 0, "ADD", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
					@Override
					public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
						if(mouseEvent.pressedLeftMouse()) {
							(new AddResourceExchangeDialog()).activate();
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
						if(mouseEvent.pressedLeftMouse() && lastClickedResource != null) {
							(new PlayerOkCancelInput("ConfirmExchangeRemovalPanel", getState(), "CONFIRM REMOVAL", "Are you sure you wish to remove this item?") {
								@Override
								public void onDeactivate() {
								}

								@Override
								public void pressedOK() {
									sendExchangeItemRemoval(1, lastClickedResource);
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
				subTab.getContent(1).attach(adminPane);
			}
		}
		{ // Gold Tab
			GUIContentPane subTab = resourcesTabbedContent.addTab("GOLD");
			subTab.setTextBoxHeightLast((int) (GLFrame.getHeight() / 1.5));
			subTab.orientateInsideFrame();
			GUIScrollablePanel scrollPanel = new GUIScrollablePanel(1, 1, subTab.getContent(0), getState());
			scrollPanel.setScrollable(GUIScrollablePanel.SCROLLABLE_VERTICAL);
			scrollPanel.setLeftRightClipOnly = true;
			GUITilePane<ResourceExchangeItem> resourcesTilePane;
			(resourcesTilePane = new GUITilePane<>(getState(), scrollPanel, 200, 300)).onInit();
			for(final ResourceExchangeItem item : getResources()) {
				if(item.barType == GOLD) {
					GUITile tile = resourcesTilePane.addButtonTile("EXCHANGE", item.createDescription(), getTileColor(item), new GUICallback() {
						@Override
						public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
							if(mouseEvent.pressedLeftMouse()) {
								lastClickedResource = item;
								if(canAffordItem(item)) {
									(new PlayerOkCancelInput("ConfirmExchangePanel", getState(), "CONFIRM EXCHANGE", "Are you sure you wish to exchange " + item.price + " " + getBarTypeName(item) + "s for this item?") {
										@Override
										public void onDeactivate() {
										}

										@Override
										public void pressedOK() {
											GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - highlight 1");
											givePlayerItem(item);
											InventoryUtils.consumeItems(GameClient.getClientPlayerState().getInventory(), item.barType, item.price);
											lastClickedResource = null;
											deactivate();
										}
									}).activate();
								} else GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - error 1");
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
							return true;
						}
					});
					setIcon(item, tile);
				}
			}
			scrollPanel.setContent(resourcesTilePane);
			scrollPanel.onInit();
			subTab.getContent(0).attach(scrollPanel);
			if(isAdmin()) {
				subTab.setTextBoxHeightLast(0, guiWindow.getInnerHeigth() - 90);
				subTab.addNewTextBox(18);
				GUIHorizontalButtonTablePane adminPane = new GUIHorizontalButtonTablePane(getState(), 2, 1, subTab.getContent(1));
				adminPane.onInit();
				adminPane.addButton(0, 0, "ADD", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
					@Override
					public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
						if(mouseEvent.pressedLeftMouse()) {
							(new AddResourceExchangeDialog()).activate();
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
						if(mouseEvent.pressedLeftMouse() && lastClickedResource != null) {
							(new PlayerOkCancelInput("ConfirmExchangeRemovalPanel", getState(), "CONFIRM REMOVAL", "Are you sure you wish to remove this item?") {
								@Override
								public void onDeactivate() {
								}

								@Override
								public void pressedOK() {
									sendExchangeItemRemoval(1, lastClickedResource);
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
				subTab.getContent(1).attach(adminPane);
			}
		}
		tabAnchor.attach(resourcesTabbedContent);
		contentPane.getContent(0).attach(tabAnchor);
		resourcesTabbedContent.setSelectedTab(lastResourceTab);
	}

	private void createItemsTab(GUIContentPane contentPane) {
		GUIScrollablePanel scrollPanel = new GUIScrollablePanel(1, 1, contentPane.getContent(0), getState());
		scrollPanel.setScrollable(GUIScrollablePanel.SCROLLABLE_VERTICAL);
		scrollPanel.setLeftRightClipOnly = true;
		GUITilePane<ItemExchangeItem> itemsTilePane;
		(itemsTilePane = new GUITilePane<>(getState(), scrollPanel, 200, 300)).onInit();
		for(final ItemExchangeItem item : getItems()) {
			if(item.barType == BRONZE) {
				GUITile tile = itemsTilePane.addButtonTile("EXCHANGE", item.createDescription(), getTileColor(item), new GUICallback() {
					@Override
					public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
						if(mouseEvent.pressedLeftMouse()) {
							lastClickedItem = item;
							if(canAffordItem(item)) {
								(new PlayerOkCancelInput("ConfirmExchangePanel", getState(), "CONFIRM EXCHANGE", "Are you sure you wish to exchange " + item.price + " " + getBarTypeName(item) + "s for this item?") {
									@Override
									public void onDeactivate() {
									}

									@Override
									public void pressedOK() {
										GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - highlight 1");
										givePlayerItem(item);
										InventoryUtils.consumeItems(GameClient.getClientPlayerState().getInventory(), item.barType, item.price);
										lastClickedItem = null;
										deactivate();
									}
								}).activate();
							} else GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - error 1");
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
						return true;
					}
				});
				item.setTempOverlay(tile.getContent());
				setIcon(item, tile);
			}
		}
		scrollPanel.setContent(itemsTilePane);
		scrollPanel.onInit();
		contentPane.getContent(0).attach(scrollPanel);
		if(isAdmin()) {
			contentPane.setTextBoxHeightLast(0, guiWindow.getInnerHeigth() - 90);
			contentPane.addNewTextBox(18);
			GUIHorizontalButtonTablePane adminPane = new GUIHorizontalButtonTablePane(getState(), 2, 1, contentPane.getContent(0, 1));
			adminPane.onInit();
			adminPane.addButton(0, 0, "ADD", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						(new AddItemExchangeDialog()).activate();
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
					if(mouseEvent.pressedLeftMouse() && lastClickedItem != null) {
						(new PlayerOkCancelInput("ConfirmItemRemovalDialog", getState(), "CONFIRM REMOVAL", "Are you sure you wish to remove this item?") {
							@Override
							public void onDeactivate() {
							}

							@Override
							public void pressedOK() {
								sendExchangeItemRemoval(2, lastClickedItem);
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
			contentPane.getContent(0, 1).attach(adminPane);
		}
	}

	private void createExchangeTab(GUIContentPane contentPane) {
		GUIAncor bronzeSection;
		GUIAncor silverSection;
		GUIAncor goldSection;
		// I see no reason why this shouldn't work, but should work and does work are two different
		// things in StarMade
		bronzeSection = contentPane.getContent(0, 0);
		contentPane.addDivider(guiWindow.getInnerWidth() / 3); // 3 Sections
		silverSection = contentPane.getContent(1, 0);
		contentPane.addDivider(guiWindow.getInnerWidth() / 3); // 3 Sections
		goldSection = contentPane.getContent(2, 0);
		Vector3f iconScale = new Vector3f(5.0f, 5.0f, 5.0f);
		GUIHorizontalButtonTablePane bronzePane = new GUIHorizontalButtonTablePane(getState(), 1, 2, bronzeSection);
		bronzePane.onInit();
		bronzePane.addButton(0, 0, "CONVERT TO SILVER", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
			@Override
			public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
				if(mouseEvent.pressedLeftMouse()) {
					if(canConvert(BRONZE, SILVER)) {
						convertBars(BRONZE, SILVER);
						getState().getController().queueUIAudio("0022_menu_ui - highlight 1");
						recreateTabs();
					} else getState().getController().queueUIAudio("0022_menu_ui - error 1");
				}
			}

			@Override
			public boolean isOccluded() {
				return !getState().getController().getPlayerInputs().isEmpty() || !canConvert(BRONZE, SILVER);
			}
		}, new GUIActivationCallback() {
			@Override
			public boolean isVisible(InputState inputState) {
				return true;
			}

			@Override
			public boolean isActive(InputState inputState) {
				return inputState.getController().getPlayerInputs().isEmpty() && canConvert(BRONZE, SILVER);
			}
		});
		bronzePane.addButton(0, 1, "CONVERT TO GOLD", GUIHorizontalArea.HButtonColor.YELLOW, new GUICallback() {
			@Override
			public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
				if(mouseEvent.pressedLeftMouse()) {
					if(canConvert(BRONZE, GOLD)) {
						convertBars(BRONZE, GOLD);
						getState().getController().queueUIAudio("0022_menu_ui - highlight 1");
						recreateTabs();
					} else getState().getController().queueUIAudio("0022_menu_ui - error 2");
				}
			}

			@Override
			public boolean isOccluded() {
				return !getState().getController().getPlayerInputs().isEmpty() || !canConvert(BRONZE, GOLD);
			}
		}, new GUIActivationCallback() {
			@Override
			public boolean isVisible(InputState inputState) {
				return true;
			}

			@Override
			public boolean isActive(InputState inputState) {
				return inputState.getController().getPlayerInputs().isEmpty() && canConvert(BRONZE, GOLD);
			}
		});
		bronzeSection.attach(bronzePane);
		GUIOverlay bronzeOverlay = IconDatabase.getBuildIconsInstance(getState(), ElementManager.getItem(BRONZE).getItemInfo().getBuildIconNum());
		bronzeOverlay.setScale(iconScale);
		bronzeSection.attach(bronzeOverlay);
		bronzeOverlay.getPos().y += 250;
		GUITextOverlay bronzeText = new GUITextOverlay(30, 30, getState());
		bronzeText.onInit();
		bronzeText.setFont(FontLibrary.FontSize.BIG.getFont());
		bronzeText.setTextSimple("x" + getCount(BRONZE));
		bronzeSection.attach(bronzeText);
		bronzeText.setScale(new Vector3f(3.0f, 3.0f, 1.0f));
		bronzeText.setPos(new Vector3f(230, 430, 0));
		GUIHorizontalButtonTablePane silverPane = new GUIHorizontalButtonTablePane(getState(), 1, 2, silverSection);
		silverPane.onInit();
		silverPane.addButton(0, 0, "CONVERT TO GOLD", GUIHorizontalArea.HButtonColor.YELLOW, new GUICallback() {
			@Override
			public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
				if(mouseEvent.pressedLeftMouse()) {
					if(canConvert(SILVER, GOLD)) {
						convertBars(SILVER, GOLD);
						getState().getController().queueUIAudio("0022_menu_ui - highlight 2");
						recreateTabs();
					} else getState().getController().queueUIAudio("0022_menu_ui - error 1");
				}
			}

			@Override
			public boolean isOccluded() {
				return !getState().getController().getPlayerInputs().isEmpty() || !canConvert(SILVER, GOLD);
			}
		}, new GUIActivationCallback() {
			@Override
			public boolean isVisible(InputState inputState) {
				return true;
			}

			@Override
			public boolean isActive(InputState inputState) {
				return inputState.getController().getPlayerInputs().isEmpty() && canConvert(SILVER, GOLD);
			}
		});
		silverPane.addButton(0, 1, "CONVERT TO BRONZE", GUIHorizontalArea.HButtonColor.ORANGE, new GUICallback() {
			@Override
			public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
				if(mouseEvent.pressedLeftMouse()) {
					if(canConvert(SILVER, BRONZE)) {
						convertBars(SILVER, BRONZE);
						getState().getController().queueUIAudio("0022_menu_ui - highlight 2");
						recreateTabs();
					} else getState().getController().queueUIAudio("0022_menu_ui - error 2");
				}
			}

			@Override
			public boolean isOccluded() {
				return !getState().getController().getPlayerInputs().isEmpty() || !canConvert(SILVER, BRONZE);
			}
		}, new GUIActivationCallback() {
			@Override
			public boolean isVisible(InputState inputState) {
				return true;
			}

			@Override
			public boolean isActive(InputState inputState) {
				return inputState.getController().getPlayerInputs().isEmpty() && canConvert(SILVER, BRONZE);
			}
		});
		silverSection.attach(silverPane);
		GUIOverlay silverOverlay = IconDatabase.getBuildIconsInstance(getState(), ElementManager.getItem(SILVER).getItemInfo().getBuildIconNum());
		silverOverlay.setScale(iconScale);
		silverSection.attach(silverOverlay);
		silverOverlay.getPos().y += 250;
		GUITextOverlay silverText = new GUITextOverlay(30, 30, getState());
		silverText.onInit();
		silverText.setFont(FontLibrary.FontSize.BIG.getFont());
		silverText.setTextSimple("x" + getCount(SILVER));
		silverSection.attach(silverText);
		silverText.setScale(new Vector3f(3.0f, 3.0f, 1.0f));
		silverText.setPos(new Vector3f(230, 430, 0));
		GUIHorizontalButtonTablePane goldPane = new GUIHorizontalButtonTablePane(getState(), 1, 2, goldSection);
		goldPane.onInit();
		goldPane.addButton(0, 0, "CONVERT TO SILVER", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
			@Override
			public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
				if(mouseEvent.pressedLeftMouse()) {
					if(canConvert(GOLD, SILVER)) {
						convertBars(GOLD, SILVER);
						getState().getController().queueUIAudio("0022_menu_ui - highlight 3");
						recreateTabs();
					} else getState().getController().queueUIAudio("0022_menu_ui - error 1");
				}
			}

			@Override
			public boolean isOccluded() {
				return !getState().getController().getPlayerInputs().isEmpty() || !canConvert(GOLD, SILVER);
			}
		}, new GUIActivationCallback() {
			@Override
			public boolean isVisible(InputState inputState) {
				return true;
			}

			@Override
			public boolean isActive(InputState inputState) {
				return inputState.getController().getPlayerInputs().isEmpty() && canConvert(GOLD, SILVER);
			}
		});
		goldPane.addButton(0, 1, "CONVERT TO BRONZE", GUIHorizontalArea.HButtonColor.ORANGE, new GUICallback() {
			@Override
			public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
				if(mouseEvent.pressedLeftMouse()) {
					if(canConvert(GOLD, BRONZE)) {
						convertBars(GOLD, BRONZE);
						getState().getController().queueUIAudio("0022_menu_ui - highlight 3");
						recreateTabs();
					} else getState().getController().queueUIAudio("0022_menu_ui - error 2");
				}
			}

			@Override
			public boolean isOccluded() {
				return !getState().getController().getPlayerInputs().isEmpty() || !canConvert(GOLD, BRONZE);
			}
		}, new GUIActivationCallback() {
			@Override
			public boolean isVisible(InputState inputState) {
				return true;
			}

			@Override
			public boolean isActive(InputState inputState) {
				return inputState.getController().getPlayerInputs().isEmpty() && canConvert(GOLD, BRONZE);
			}
		});
		goldSection.attach(goldPane);
		GUIOverlay goldOverlay = IconDatabase.getBuildIconsInstance(getState(), ElementManager.getItem(GOLD).getItemInfo().getBuildIconNum());
		goldOverlay.setScale(iconScale);
		goldSection.attach(goldOverlay);
		goldOverlay.getPos().y += 250;
		GUITextOverlay goldText = new GUITextOverlay(30, 30, getState());
		goldText.onInit();
		goldText.setFont(FontLibrary.FontSize.BIG.getFont());
		goldText.setTextSimple("x" + getCount(GOLD));
		goldSection.attach(goldText);
		goldText.setScale(new Vector3f(3.0f, 3.0f, 1.0f));
		goldText.setPos(new Vector3f(230, 430, 0));
	}

	private boolean canConvert(short from, short to) {
		if(from == BRONZE) {
			if(to == SILVER) return getCount(from) >= 5;
			else if(to == GOLD) return getCount(from) >= 25;
		} else if(from == SILVER) {
			if(to == BRONZE) return getCount(from) >= 1;
			else if(to == GOLD) return getCount(from) >= 5;
		} else if(from == GOLD) {
			if(to == BRONZE) return getCount(from) >= 1;
			else if(to == SILVER) return getCount(from) >= 1;
		}
		return false;
	}

	private void convertBars(short from, short to) {
		Inventory inventory = GameClient.getClientPlayerState().getInventory();
		if(from == BRONZE) {
			if(to == SILVER) {
				InventoryUtils.consumeItems(inventory, from, 5);
				InventoryUtils.addItem(inventory, to, 1);
			} else if(to == GOLD) {
				InventoryUtils.consumeItems(inventory, from, 25);
				InventoryUtils.addItem(inventory, to, 1);
			}
		} else if(from == SILVER) {
			if(to == BRONZE) {
				InventoryUtils.consumeItems(inventory, from, 1);
				InventoryUtils.addItem(inventory, to, 5);
			} else if(to == GOLD) {
				InventoryUtils.consumeItems(inventory, from, 5);
				InventoryUtils.addItem(inventory, to, 1);
			}
		} else if(from == GOLD) {
			if(to == BRONZE) {
				InventoryUtils.consumeItems(inventory, from, 1);
				InventoryUtils.addItem(inventory, to, 25);
			} else if(to == SILVER) {
				InventoryUtils.consumeItems(inventory, from, 1);
				InventoryUtils.addItem(inventory, to, 5);
			}
		}
	}

	private int getCount(short type) {
		return InventoryUtils.getItemAmount(GameClient.getClientPlayerState().getInventory(), type);
	}

	private boolean isAdmin() {
		return GameClient.getClientPlayerState().isAdmin();
	}

	private ArrayList<BlueprintExchangeItem> getBlueprints() {
		return ClientCacheManager.blueprintExchangeItems;
	}

	private ArrayList<ResourceExchangeItem> getResources() {
		return ClientCacheManager.resourceExchangeItems;
	}

	private ArrayList<ItemExchangeItem> getItems() {
		return ClientCacheManager.itemExchangeItems;
	}

	private GUIHorizontalArea.HButtonColor getTileColor(ExchangeItem item) {
		if(canAffordItem(item)) {
			if(item instanceof BlueprintExchangeItem) return GUIHorizontalArea.HButtonColor.BLUE;
			else if(item instanceof ResourceExchangeItem) return GUIHorizontalArea.HButtonColor.YELLOW;
		}
		return GUIHorizontalArea.HButtonColor.RED;
	}

	private String getBarTypeName(ExchangeItem item) {
		return ElementManager.getItem(item.barType).getItemInfo().getName();
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

	private void sendExchangeItemRemoval(int type, ExchangeItem item) {
		PacketUtil.sendPacketToServer(new ExchangeItemRemovePacket(type, item));
	}

	private void setIcon(ExchangeItem item, GUITile tile) {
		GUIOverlay spriteOverlay = item.getIcon();
		spriteOverlay.onInit();
		if(spriteOverlay.getUserPointer() == null) spriteOverlay.setUserPointer("default-icon");
		spriteOverlay.setPos(100.0f, 230.0f, 0.0f);
		tile.getContent().attach(spriteOverlay);
	}
}

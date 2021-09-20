package thederpgamer.edencore.gui.exchangemenu;

import api.common.GameClient;
import api.network.packets.PacketUtil;
import api.utils.game.inventory.InventoryUtils;
import api.utils.gui.GUIMenuPanel;
import org.schema.game.client.controller.PlayerOkCancelInput;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIActivationCallback;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUIOverlay;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.data.exchange.BlueprintExchangeItem;
import thederpgamer.edencore.data.exchange.ExchangeItem;
import thederpgamer.edencore.data.exchange.ResourceExchangeItem;
import thederpgamer.edencore.element.ElementManager;
import thederpgamer.edencore.manager.ClientCacheManager;
import thederpgamer.edencore.network.client.ExchangeItemRemovePacket;
import thederpgamer.edencore.network.client.RequestSpawnEntryPacket;
import thederpgamer.edencore.utils.DataUtils;
import thederpgamer.edencore.utils.InventoryUtil;

import java.util.ArrayList;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/17/2021]
 */
public class ExchangeMenuPanel extends GUIMenuPanel {

    private BlueprintExchangeItem lastClickedBP;
    private ResourceExchangeItem lastClickedResource;

    public ExchangeMenuPanel(InputState inputState) {
        super(inputState, "ServerExchange", 750, 500);
    }

    @Override
    public void recreateTabs() {
        int lastTab = guiWindow.getSelectedTab();
        guiWindow.clearTabs();

        GUIContentPane blueprintsTab = guiWindow.addTab("SHIPS AND STATIONS");
        blueprintsTab.setTextBoxHeightLast(500);
        createBlueprintsTab(blueprintsTab);

        GUIContentPane resourcesTab = guiWindow.addTab("RESOURCES");
        resourcesTab.setTextBoxHeightLast(500);
        createResourcesTab(resourcesTab);

        GUIContentPane servicesTab = guiWindow.addTab("SERVICES");
        servicesTab.setTextBoxHeightLast(500);
        createServicesTab(servicesTab);

        GUIContentPane exchangeTab = guiWindow.addTab("EXCHANGE");
        exchangeTab.setTextBoxHeightLast(500);
        createExchangeTab(exchangeTab);

        guiWindow.setSelectedTab(lastTab);
        lastClickedBP = null;
        lastClickedResource = null;
    }

    private void createBlueprintsTab(GUIContentPane contentPane) {
        GUITilePane<BlueprintExchangeItem> blueprintsTilePane;
        (blueprintsTilePane = new GUITilePane<>(getState(), guiWindow, 200, 300)).onInit();
        for(final BlueprintExchangeItem item : getBlueprints()) {
            GUITile tile = blueprintsTilePane.addButtonTile("EXCHANGE", item.createDescription(), getTileColor(item), new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
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
                                    givePlayerItem(item);
                                    InventoryUtil.consumeItems(GameClient.getClientPlayerState().getInventory(), item.barType, item.price);
                                    lastClickedBP = null;
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
            GUIOverlay spriteOverlay = item.getIcon();
            spriteOverlay.onInit();
            tile.attach(spriteOverlay);
            if(spriteOverlay.getUserPointer().equals("default-icon")) {
                spriteOverlay.getPos().x += 80;
                spriteOverlay.getPos().y += 180;
            } else {
                spriteOverlay.getPos().x += 100;
                spriteOverlay.getPos().y += 200;
            }
        }
        contentPane.getContent(0).attach(blueprintsTilePane);

        if(isAdmin()) {
            contentPane.addNewTextBox(12);
            GUIHorizontalButtonTablePane adminPane = new GUIHorizontalButtonTablePane(getState(), 2, 1, contentPane.getContent(1));
            adminPane.onInit();

            adminPane.addButton(0, 0, "ADD", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        (new AddBlueprintExchangeDialog()).activate();
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
            contentPane.getContent(1).attach(adminPane);
        }
    }

    private void createResourcesTab(GUIContentPane contentPane) {
        GUITilePane<ResourceExchangeItem> resourcesTilePane;
        (resourcesTilePane = new GUITilePane<>(getState(), guiWindow, 200, 256)).onInit();
        for(final ResourceExchangeItem item : getResources()) {
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
                                    InventoryUtil.consumeItems(GameClient.getClientPlayerState().getInventory(), item.barType, item.price);
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
                    return getState().getController().getPlayerInputs().isEmpty();
                }
            });
            GUIOverlay spriteOverlay = item.getIcon();
            spriteOverlay.onInit();
            spriteOverlay.getSprite().setWidth(200);
            spriteOverlay.getSprite().setHeight(200);
            tile.attach(spriteOverlay);
            spriteOverlay.getPos().x += 65;
            spriteOverlay.getPos().y += 180;
        }
        contentPane.getContent(0).attach(resourcesTilePane);

        if(isAdmin()) {
            contentPane.addNewTextBox(12);
            GUIHorizontalButtonTablePane adminPane = new GUIHorizontalButtonTablePane(getState(), 2, 1, contentPane.getContent(1));
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
            contentPane.getContent(1).attach(adminPane);
        }
    }

    private void createServicesTab(GUIContentPane contentPane) {

    }

    private void createExchangeTab(GUIContentPane contentPane) {

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
        if(item instanceof BlueprintExchangeItem && ((BlueprintExchangeItem) item).blueprint != null) {
            PacketUtil.sendPacketToServer(new RequestSpawnEntryPacket(((BlueprintExchangeItem) item).blueprint.getName()));
            /* This doesn't work because the game won't see the item as valid and won't spawn it
            BlueprintMetaItem metaItem = (BlueprintMetaItem) MetaObjectManager.instantiate(MetaObjectManager.MetaObjectType.BLUEPRINT, (short) -1, false);
            metaItem.blueprintName = item.name;
            metaItem.goal = new ElementCountMap(((BlueprintExchangeItem) item).blueprint.getElementCountMapWithChilds());
            metaItem.progress = new ElementCountMap(((BlueprintExchangeItem) item).blueprint.getElementCountMapWithChilds());
            try {
                int slot = inventory.getFreeSlot();
                inventory.put(slot, metaItem);
                if(GameCommon.isOnSinglePlayer() || GameCommon.isClientConnectedToServer()) inventory.sendInventoryModification(slot);
            } catch(Exception exception) {
                exception.printStackTrace();
            }
             */
        } else if(item instanceof ResourceExchangeItem) InventoryUtils.addItem(inventory, ((ResourceExchangeItem) item).itemId, ((ResourceExchangeItem) item).itemCount);
    }

    private void sendExchangeItemRemoval(int type, ExchangeItem item) {
        PacketUtil.sendPacketToServer(new ExchangeItemRemovePacket(type, item));
    }
}
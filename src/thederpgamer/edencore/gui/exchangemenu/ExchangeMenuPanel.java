package thederpgamer.edencore.gui.exchangemenu;

import api.common.GameClient;
import api.mod.config.PersistentObjectUtil;
import api.utils.game.inventory.InventoryUtils;
import api.utils.gui.GUIMenuPanel;
import org.schema.game.common.controller.ElementCountMap;
import org.schema.game.common.data.element.meta.BlueprintMetaItem;
import org.schema.game.common.data.element.meta.MetaObjectManager;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.game.common.data.player.inventory.NoSlotFreeException;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIActivationCallback;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUIOverlay;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.exchange.BlueprintExchangeItem;
import thederpgamer.edencore.data.exchange.ExchangeItem;
import thederpgamer.edencore.data.exchange.ResourceExchangeItem;
import thederpgamer.edencore.utils.DataUtils;

import java.util.ArrayList;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/17/2021]
 */
public class ExchangeMenuPanel extends GUIMenuPanel {

    private GUITilePane<BlueprintExchangeItem> blueprintsTilePane;
    private GUITilePane<ResourceExchangeItem> resourcesTilePane;

    public ExchangeMenuPanel(InputState inputState) {
        super(inputState, "ServerExchange", 750, 500);
    }

    @Override
    public void recreateTabs() {
        guiWindow.clearTabs();

        GUIContentPane blueprintsTab = guiWindow.addTab("BLUEPRINTS");
        blueprintsTab.setTextBoxHeightLast(500);
        createBlueprintsTab(blueprintsTab);

        GUIContentPane resourcesTab = guiWindow.addTab("RESOURCES");
        resourcesTab.setTextBoxHeightLast(500);
        createResourcesTab(resourcesTab);

        GUIContentPane exchangeTab = guiWindow.addTab("EXCHANGE");
        exchangeTab.setTextBoxHeightLast(500);
        createExchangeTab(exchangeTab);
    }

    private void createBlueprintsTab(GUIContentPane contentPane) {
        (blueprintsTilePane = new GUITilePane<>(getState(), guiWindow, 130, 180)).onInit();
        for(final BlueprintExchangeItem item : getBlueprints()) {
            GUITile tile = blueprintsTilePane.addButtonTile("EXCHANGE", item.description, getTileColor(item), new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        if(canAffordItem(item)) {
                            GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - highlight 1");
                            givePlayerItem(item);
                            InventoryUtils.consumeItems(GameClient.getClientPlayerState().getInventory(), item.barType, item.price);
                        } else GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - error 1");
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
            GUIOverlay spriteOverlay = item.getIcon();
            spriteOverlay.onInit();
            spriteOverlay.getSprite().setWidth(130);
            spriteOverlay.getSprite().setHeight(115);
            tile.attach(spriteOverlay);
            spriteOverlay.getPos().x += 5;
            spriteOverlay.getPos().y += 70;
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
                        (new AddBlueprintExchangeDialog(ExchangeMenuPanel.this)).activate();
                        recreateTabs();
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

            adminPane.addButton(1, 0, "REMOVE", GUIHorizontalArea.HButtonColor.ORANGE, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        //Todo
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
            contentPane.getContent(1).attach(adminPane);
        }
    }

    private void createResourcesTab(GUIContentPane contentPane) {
        (resourcesTilePane = new GUITilePane<>(getState(), guiWindow, 130, 180)).onInit();
        for(final ResourceExchangeItem item : getResources()) {
            GUITile tile = resourcesTilePane.addButtonTile("EXCHANGE", item.description, getTileColor(item), new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        if(canAffordItem(item)) {
                            GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - highlight 1");
                            givePlayerItem(item);
                            InventoryUtils.consumeItems(GameClient.getClientPlayerState().getInventory(), item.barType, item.price);
                        } else GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - error 1");
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
            GUIOverlay spriteOverlay = item.getIcon();
            spriteOverlay.onInit();
            spriteOverlay.getSprite().setWidth(130);
            spriteOverlay.getSprite().setHeight(115);
            tile.attach(spriteOverlay);
            spriteOverlay.getPos().x += 5;
            spriteOverlay.getPos().y += 70;
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
                        //Todo
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

            adminPane.addButton(1, 0, "REMOVE", GUIHorizontalArea.HButtonColor.ORANGE, new GUICallback() {
                @Override
                public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                    if(mouseEvent.pressedLeftMouse()) {
                        //Todo
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
            contentPane.getContent(1).attach(adminPane);
        }
    }

    private void createExchangeTab(GUIContentPane contentPane) {

    }

    private boolean isAdmin() {
        return GameClient.getClientPlayerState().isAdmin();
    }

    private ArrayList<BlueprintExchangeItem> getBlueprints() {
        ArrayList<BlueprintExchangeItem> blueprintList = new ArrayList<>();
        for(Object obj : PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), BlueprintExchangeItem.class)) {
            blueprintList.add((BlueprintExchangeItem) obj);
        }
        return blueprintList;
    }

    private ArrayList<ResourceExchangeItem> getResources() {
        ArrayList<ResourceExchangeItem> resourceList = new ArrayList<>();
        for(Object obj : PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), ResourceExchangeItem.class)) {
            resourceList.add((ResourceExchangeItem) obj);
        }
        return resourceList;
    }

    private GUIHorizontalArea.HButtonColor getTileColor(ExchangeItem item) {
        if(canAffordItem(item)) {
            if(item instanceof BlueprintExchangeItem) return GUIHorizontalArea.HButtonColor.BLUE;
            else if(item instanceof ResourceExchangeItem) return GUIHorizontalArea.HButtonColor.YELLOW;
        }
        return GUIHorizontalArea.HButtonColor.RED;
    }

    private boolean canAffordItem(ExchangeItem item) {
        short barType = item.barType;
        int count = InventoryUtils.getItemAmount(GameClient.getClientPlayerState().getInventory(), barType);
        return (count >= item.price && GameClient.getClientPlayerState().getInventory().hasFreeSlot()) || (GameClient.getClientPlayerState().isCreativeModeEnabled() && isAdmin() && !DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState()));
    }

    public void givePlayerItem(ExchangeItem item) {
        Inventory inventory = GameClient.getClientPlayerState().getInventory();
        if(item instanceof BlueprintExchangeItem) {
            BlueprintMetaItem metaItem = (BlueprintMetaItem) MetaObjectManager.instantiate(MetaObjectManager.MetaObjectType.BLUEPRINT, (short) -1, true);
            metaItem.blueprintName = item.name;
            metaItem.goal = new ElementCountMap(((BlueprintExchangeItem) item).blueprint.getElementCountMapWithChilds());
            metaItem.progress = new ElementCountMap(metaItem.goal);
            try {
                int slot = inventory.getFreeSlot();
                inventory.put(slot, metaItem);
                inventory.sendInventoryModification(slot);
            } catch(NoSlotFreeException exception) {
                exception.printStackTrace();
            }
        } else if(item instanceof ResourceExchangeItem) InventoryUtils.addItem(inventory, ((ResourceExchangeItem) item).itemId, ((ResourceExchangeItem) item).itemCount);
    }
}
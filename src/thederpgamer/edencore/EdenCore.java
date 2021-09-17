package thederpgamer.edencore;

import api.common.GameClient;
import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.block.*;
import api.listener.events.draw.RegisterWorldDrawersEvent;
import api.listener.events.gui.GUITopBarCreateEvent;
import api.listener.events.player.PlayerDeathEvent;
import api.listener.events.player.PlayerPickupFreeItemEvent;
import api.listener.events.player.PlayerSpawnEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.StarRunnable;
import api.utils.game.PlayerUtils;
import org.apache.commons.io.IOUtils;
import org.schema.game.client.view.gui.newgui.GUITopBar;
import org.schema.game.common.data.player.PlayerState;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIActivationHighlightCallback;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.input.InputState;
import org.schema.schine.resource.ResourceLoader;
import thederpgamer.edencore.commands.BuildSectorCommand;
import thederpgamer.edencore.commands.ListEntityCommand;
import thederpgamer.edencore.commands.LoadEntityCommand;
import thederpgamer.edencore.commands.SaveEntityCommand;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.drawer.BuildSectorHudDrawer;
import thederpgamer.edencore.element.ElementManager;
import thederpgamer.edencore.element.items.PrizeBars;
import thederpgamer.edencore.manager.*;
import thederpgamer.edencore.utils.DataUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Main class for EdenCore mod.
 *
 * @author TheDerpGamer
 * @version 1.0 - [06/27/2021]
 */
public class EdenCore extends StarMod {

    //Instance
    private static EdenCore instance;
    public static EdenCore getInstance() {
        return instance;
    }
    public EdenCore() {

    }
    public static void main(String[] args) {

    }

    //Other
    private final String[] overwriteClasses = new String[] {
            "PlayerState",
            "CatalogExtendedPanel",
            "BlueprintEntry"
    };

    @Override
    public void onEnable() {
        instance = this;
        ConfigManager.initialize(this);
        LogManager.initialize();
        TransferManager.initialize();
        GUIManager.initialize();
        registerListeners();
        registerCommands();
    }

    @Override
    public byte[] onClassTransform(String className, byte[] byteCode) {
        for(String name : overwriteClasses) if(className.endsWith(name)) return overwriteClass(className, byteCode);
        return super.onClassTransform(className, byteCode);
    }

    @Override
    public void onResourceLoad(ResourceLoader resourceLoader) {
        ResourceManager.loadResources(resourceLoader);
    }

    @Override
    public void onBlockConfigLoad(BlockConfig blockConfig) {
        //Items
        ElementManager.addItemGroup(new PrizeBars());

        ElementManager.initialize();
    }


    private void registerListeners() {
        StarLoader.registerListener(GUITopBarCreateEvent.class, new Listener<GUITopBarCreateEvent>() {
            @Override
            public void onEvent(GUITopBarCreateEvent event) {
                GUITopBar.ExpandedButton dropDownButton = event.getDropdownButtons().get(event.getDropdownButtons().size() - 1);
                dropDownButton.addExpandedButton("EXCHANGE", new GUICallback() {
                    @Override
                    public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                        if(mouseEvent.pressedLeftMouse()) {
                            GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
                            GUIManager.exchangeMenuManager.setActive(true);
                        }
                    }

                    @Override
                    public boolean isOccluded() {
                        return false;
                    }
                }, new GUIActivationHighlightCallback() {
                    @Override
                    public boolean isHighlighted(InputState inputState) {
                        return false;
                    }

                    @Override
                    public boolean isVisible(InputState inputState) {
                        return true;
                    }

                    @Override
                    public boolean isActive(InputState inputState) {
                        return true;
                    }
                });
            }
        }, this);

        StarLoader.registerListener(RegisterWorldDrawersEvent.class, new Listener<RegisterWorldDrawersEvent>() {
            @Override
            public void onEvent(RegisterWorldDrawersEvent event) {
                event.getModDrawables().add(new BuildSectorHudDrawer());
            }
        }, this);

        StarLoader.registerListener(SegmentPieceAddEvent.class, new Listener<SegmentPieceAddEvent>() {
            @Override
            public void onEvent(SegmentPieceAddEvent event) {
                try {
                    if(DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
                        BuildSectorData sectorData = DataUtils.getPlayerCurrentBuildSector(GameClient.getClientPlayerState());
                        if(!sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "EDIT")) event.setCanceled(true);
                    }
                } catch(Exception ignored) { }
            }
        }, this);

        StarLoader.registerListener(SegmentPieceRemoveEvent.class, new Listener<SegmentPieceRemoveEvent>() {
            @Override
            public void onEvent(SegmentPieceRemoveEvent event) {
                try {
                    if(DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
                        BuildSectorData sectorData = DataUtils.getPlayerCurrentBuildSector(GameClient.getClientPlayerState());
                        if(!sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "EDIT")) event.setCanceled(true);
                    }
                } catch(Exception ignored) { }
            }
        }, this);

        StarLoader.registerListener(SegmentPieceActivateByPlayer.class, new Listener<SegmentPieceActivateByPlayer>() {
            @Override
            public void onEvent(SegmentPieceActivateByPlayer event) {
                try {
                    if(DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
                        BuildSectorData sectorData = DataUtils.getPlayerCurrentBuildSector(GameClient.getClientPlayerState());
                        if(!sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "EDIT")) event.setCanceled(true);
                    }
                } catch(Exception ignored) { }
            }
        }, this);

        StarLoader.registerListener(SegmentPieceModifyOnClientEvent.class, new Listener<SegmentPieceModifyOnClientEvent>() {
            @Override
            public void onEvent(SegmentPieceModifyOnClientEvent event) {
                try {
                    if(DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
                        BuildSectorData sectorData = DataUtils.getPlayerCurrentBuildSector(GameClient.getClientPlayerState());
                        if(!sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "EDIT")) event.setCanceled(true);
                    }
                } catch(Exception ignored) { }
            }
        }, this);

        StarLoader.registerListener(ClientSelectSegmentPieceEvent.class, new Listener<ClientSelectSegmentPieceEvent>() {
            @Override
            public void onEvent(ClientSelectSegmentPieceEvent event) {
                try {
                    if(DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
                        BuildSectorData sectorData = DataUtils.getPlayerCurrentBuildSector(GameClient.getClientPlayerState());
                        if(!sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "EDIT")) event.setCanceled(true);
                    }
                } catch(Exception ignored) { }
            }
        }, this);

        StarLoader.registerListener(ClientSegmentPieceConnectionChangeEvent.class, new Listener<ClientSegmentPieceConnectionChangeEvent>() {
            @Override
            public void onEvent(ClientSegmentPieceConnectionChangeEvent event) {
                try {
                    if(DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
                        BuildSectorData sectorData = DataUtils.getPlayerCurrentBuildSector(GameClient.getClientPlayerState());
                        if(!sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "EDIT")) event.setCanceled(true);
                    }
                } catch(Exception ignored) { }
            }
        }, this);

        StarLoader.registerListener(PlayerPickupFreeItemEvent.class, new Listener<PlayerPickupFreeItemEvent>() {
            @Override
            public void onEvent(PlayerPickupFreeItemEvent event) {
                try {
                    if(DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
                        BuildSectorData sectorData = DataUtils.getPlayerCurrentBuildSector(GameClient.getClientPlayerState());
                        if(!sectorData.hasPermission(GameClient.getClientPlayerState().getName(), "PICKUP")) event.setCanceled(true);
                    }
                } catch(Exception ignored) { }
            }
        }, this);

        StarLoader.registerListener(PlayerDeathEvent.class, new Listener<PlayerDeathEvent>() {
            @Override
            public void onEvent(PlayerDeathEvent event) {
                if(DataUtils.isPlayerInAnyBuildSector(event.getPlayer())) queueSpawnSwitch(event.getPlayer());
            }
        }, this);

        StarLoader.registerListener(PlayerSpawnEvent.class, new Listener<PlayerSpawnEvent>() {
            @Override
            public void onEvent(PlayerSpawnEvent event) {
                if(DataUtils.isPlayerInAnyBuildSector(event.getPlayer().getOwnerState())) queueSpawnSwitch(event.getPlayer().getOwnerState());
            }
        }, this);
    }

    private void registerCommands() {
        StarLoader.registerCommand(new SaveEntityCommand());
        StarLoader.registerCommand(new LoadEntityCommand());
        StarLoader.registerCommand(new ListEntityCommand());
        StarLoader.registerCommand(new BuildSectorCommand());
    }

    private void queueSpawnSwitch(final PlayerState playerState) {
        new StarRunnable() {
            @Override
            public void run() {
                if(!DataUtils.isPlayerInAnyBuildSector(playerState)) cancel();
                if(!playerState.hasSpawnWait) { //Wait until player has spawned, then warp them
                    try {
                        DataUtils.movePlayerFromBuildSector(playerState);
                    } catch(IOException | SQLException exception) {
                        LogManager.logException("Encountered a severe exception while trying to move player \"" + playerState.getName() + "\" out of a build sector! Report this ASAP!", exception);
                        playerState.setUseCreativeMode(false);
                        if(!playerState.isAdmin()) playerState.setHasCreativeMode(false);
                        PlayerUtils.sendMessage(playerState, "The server encountered a severe exception while trying to load you in and your player state may be corrupted as a result. Report this to an admin ASAP!");
                    }
                    cancel();
                }
            }
        }.runTimer(this, 15);
    }

    private byte[] overwriteClass(String className, byte[] byteCode) {
        byte[] bytes = null;
        try {
            ZipInputStream file = new ZipInputStream(new FileInputStream(this.getSkeleton().getJarFile()));
            while(true) {
                ZipEntry nextEntry = file.getNextEntry();
                if(nextEntry == null) break;
                if(nextEntry.getName().endsWith(className + ".class")) bytes = IOUtils.toByteArray(file);
            }
            file.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        if(bytes != null) return bytes;
        else return byteCode;
    }
}

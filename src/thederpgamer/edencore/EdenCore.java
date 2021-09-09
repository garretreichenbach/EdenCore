package thederpgamer.edencore;

import api.common.GameClient;
import api.listener.Listener;
import api.listener.events.block.*;
import api.listener.events.draw.RegisterWorldDrawersEvent;
import api.listener.events.input.KeyPressEvent;
import api.listener.events.player.PlayerDeathEvent;
import api.listener.events.player.PlayerPickupFreeItemEvent;
import api.listener.events.player.PlayerSpawnEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.StarRunnable;
import api.utils.game.PlayerUtils;
import api.utils.gui.ModGUIHandler;
import org.apache.commons.io.IOUtils;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.commands.BuildSectorCommand;
import thederpgamer.edencore.commands.ListEntityCommand;
import thederpgamer.edencore.commands.LoadEntityCommand;
import thederpgamer.edencore.commands.SaveEntityCommand;
import thederpgamer.edencore.data.BuildSectorData;
import thederpgamer.edencore.drawer.BuildSectorHudDrawer;
import thederpgamer.edencore.gui.admintools.AdminToolsControlManager;
import thederpgamer.edencore.gui.buildtools.BuildToolsControlManager;
import thederpgamer.edencore.manager.ConfigManager;
import thederpgamer.edencore.manager.LogManager;
import thederpgamer.edencore.manager.TransferManager;
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
 * @since 06/27/2021
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

    //Data
    private final String[] overwriteClasses = new String[] {
            "PlayerState",
            "GUITopBar",
            "BlueprintEntry"
    };
    public BuildSectorHudDrawer hudDrawer;
    public AdminToolsControlManager adminToolsControlManager;
    public BuildToolsControlManager buildToolsControlManager;

    @Override
    public void onEnable() {
        instance = this;
        ConfigManager.initialize(this);
        LogManager.initialize();
        TransferManager.initialize();
        registerListeners();
        registerCommands();
        startRunners();
    }

    @Override
    public byte[] onClassTransform(String className, byte[] byteCode) {
        for(String name : overwriteClasses) if(className.endsWith(name)) return overwriteClass(className, byteCode);
        return super.onClassTransform(className, byteCode);
    }

    private void registerListeners() {
        StarLoader.registerListener(RegisterWorldDrawersEvent.class, new Listener<RegisterWorldDrawersEvent>() {
            @Override
            public void onEvent(RegisterWorldDrawersEvent event) {
                (hudDrawer = new BuildSectorHudDrawer()).onInit();
                event.getModDrawables().add(hudDrawer);
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

        StarLoader.registerListener(KeyPressEvent.class, new Listener<KeyPressEvent>() {
            @Override
            public void onEvent(KeyPressEvent event) {
                if(GameClient.getClientState().getPlayerInputs().isEmpty()) {
                    if(!ConfigManager.getMainConfig().getBoolean("debug-mode")) return; //Todo: Finish build tools and admin tools menus
                    if(event.getChar() == ConfigManager.getMainConfig().getString("build-tools-menu-key").charAt(0)) {
                        activateBuildToolsMenu();
                    } else if(event.getChar() == ConfigManager.getMainConfig().getString("admin-tools-menu-key").charAt(0) && GameClient.getClientPlayerState().isAdmin()) {
                        activateAdminToolsMenu();
                    }
                }
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

    public void activateAdminToolsMenu() {
        ModGUIHandler.deactivateAll();
        if(adminToolsControlManager == null) {
            adminToolsControlManager = new AdminToolsControlManager(GameClient.getClientState());
            ModGUIHandler.registerNewControlManager(getSkeleton(), adminToolsControlManager);
        }
        if(!ConfigManager.getMainConfig().getBoolean("debug-mode")) {
            PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "This is a WIP feature. Check back later!");
        } else adminToolsControlManager.setActive(true);
    }

    public void activateBuildToolsMenu() {
        if(ConfigManager.getMainConfig().getBoolean("debug-mode")) { //Todo: Finish / fix build tools menu
            ModGUIHandler.deactivateAll();
            if(buildToolsControlManager == null) {
                buildToolsControlManager = new BuildToolsControlManager(GameClient.getClientState());
                ModGUIHandler.registerNewControlManager(getSkeleton(), buildToolsControlManager);
            }
            if(!ConfigManager.getMainConfig().getBoolean("debug-mode")) {
                PlayerUtils.sendMessage(GameClient.getClientPlayerState(), "This is a WIP feature. Check back later!");
            } else buildToolsControlManager.setActive(true);
        }
    }

    private void startRunners() {
        new StarRunnable() {
            @Override
            public void run() {
                DataUtils.saveData();
            }
        }.runTimer(this, ConfigManager.getMainConfig().getLong("auto-save-interval"));
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

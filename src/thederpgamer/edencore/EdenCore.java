package thederpgamer.edencore;

import api.common.GameClient;
import api.listener.Listener;
import api.listener.events.input.KeyPressEvent;
import api.listener.events.player.PlayerSpawnEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.StarRunnable;
import api.utils.gui.ModGUIHandler;
import org.apache.commons.io.IOUtils;
import thederpgamer.edencore.gui.admintools.AdminToolsControlManager;
import thederpgamer.edencore.gui.buildtools.BuildToolsControlManager;
import thederpgamer.edencore.manager.ConfigManager;
import thederpgamer.edencore.manager.LogManager;
import thederpgamer.edencore.utils.DataUtils;
import java.io.FileInputStream;
import java.io.IOException;
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
            "GUITopBar"
    };
    public AdminToolsControlManager adminToolsControlManager;
    public BuildToolsControlManager buildToolsControlManager;

    @Override
    public void onEnable() {
        instance = this;
        ConfigManager.initialize(this);
        LogManager.initialize();
        registerListeners();
        startRunners();
    }

    @Override
    public byte[] onClassTransform(String className, byte[] byteCode) {
        for(String name : overwriteClasses) if(className.endsWith(name)) return overwriteClass(className, byteCode);
        return super.onClassTransform(className, byteCode);
    }

    private void registerListeners() {
        StarLoader.registerListener(PlayerSpawnEvent.class, new Listener<PlayerSpawnEvent>() {
            @Override
            public void onEvent(PlayerSpawnEvent event) {
                if(DataUtils.isPlayerInAnyBuildSector(event.getPlayer().getOwnerState())) {
                    try {
                        DataUtils.movePlayerFromBuildSector(event.getPlayer().getOwnerState());
                    } catch(IOException ignored) { }
                }
            }
        }, this);

        StarLoader.registerListener(KeyPressEvent.class, new Listener<KeyPressEvent>() {
            @Override
            public void onEvent(KeyPressEvent event) {
                if(GameClient.getClientState().getPlayerInputs().isEmpty()) {
                    if(event.getChar() == ConfigManager.getMainConfig().getString("build-tools-menu-key").charAt(0)) {
                        activateBuildToolsMenu();
                    } else if(event.getChar() == ConfigManager.getMainConfig().getString("admin-tools-menu-key").charAt(0) && GameClient.getClientPlayerState().isAdmin()) {
                        activateAdminToolsMenu();
                    }
                }
            }
        }, this);
    }

    public void activateAdminToolsMenu() {
        ModGUIHandler.deactivateAll();
        if(adminToolsControlManager == null) {
            adminToolsControlManager = new AdminToolsControlManager(GameClient.getClientState());
            ModGUIHandler.registerNewControlManager(getSkeleton(), adminToolsControlManager);
        }
        adminToolsControlManager.setActive(true);
    }

    public void activateBuildToolsMenu() {
        ModGUIHandler.deactivateAll();
        if(buildToolsControlManager == null) {
            buildToolsControlManager = new BuildToolsControlManager(GameClient.getClientState());
            ModGUIHandler.registerNewControlManager(getSkeleton(), buildToolsControlManager);
        }
        buildToolsControlManager.setActive(true);
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

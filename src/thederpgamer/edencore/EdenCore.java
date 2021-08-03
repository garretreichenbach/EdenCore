package thederpgamer.edencore;

import api.common.GameClient;
import api.listener.Listener;
import api.listener.events.gui.GUITopBarCreateEvent;
import api.listener.events.player.PlayerSpawnEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.StarRunnable;
import api.utils.gui.ModGUIHandler;
import org.apache.commons.io.IOUtils;
import org.schema.game.client.view.gui.newgui.GUITopBar;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIActivationHighlightCallback;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.input.InputState;
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
            "PlayerState"
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
                if(DataUtils.isPlayerInAnyBuildSector(event.getPlayer().getOwnerState())) DataUtils.movePlayerFromBuildSector(event.getPlayer().getOwnerState());
            }
        }, this);

        StarLoader.registerListener(GUITopBarCreateEvent.class, new Listener<GUITopBarCreateEvent>() {
            @Override
            public void onEvent(final GUITopBarCreateEvent event) {
                GUITopBar.ExpandedButton buildToolsButton = event.getDropdownButtons().get(event.getDropdownButtons().size() - 1);
                buildToolsButton.addExpandedButton("BUILD TOOLS", new GUICallback() {
                    @Override
                    public void callback(final GUIElement guiElement, MouseEvent mouseEvent) {
                        if(mouseEvent.pressedLeftMouse()) {
                            GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
                            if(buildToolsControlManager == null) {
                                buildToolsControlManager = new BuildToolsControlManager(GameClient.getClientState());
                                ModGUIHandler.registerNewControlManager(getSkeleton(), buildToolsControlManager);
                            }
                            buildToolsControlManager.setActive(true);
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

                if(GameClient.getClientPlayerState().isAdmin()) {
                    GUITopBar.ExpandedButton adminToolsButton = event.getDropdownButtons().get(event.getDropdownButtons().size() - 1);
                    adminToolsButton.addExpandedButton("ADMIN TOOLS", new GUICallback() {
                        @Override
                        public void callback(final GUIElement guiElement, MouseEvent mouseEvent) {
                            if(mouseEvent.pressedLeftMouse()) {
                                GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
                                if(adminToolsControlManager == null) {
                                    adminToolsControlManager = new AdminToolsControlManager(GameClient.getClientState());
                                    ModGUIHandler.registerNewControlManager(getSkeleton(), adminToolsControlManager);
                                }
                                adminToolsControlManager.setActive(true);
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
            }
        }, this);
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

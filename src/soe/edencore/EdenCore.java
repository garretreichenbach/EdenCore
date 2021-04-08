package soe.edencore;

import api.DebugFile;
import api.common.GameClient;
import api.common.GameCommon;
import api.listener.Listener;
import api.listener.events.faction.FactionCreateEvent;
import api.listener.events.faction.FactionRelationChangeEvent;
import api.listener.events.gui.GUITopBarCreateEvent;
import api.listener.events.player.*;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.mod.config.FileConfiguration;
import api.mod.config.PersistentObjectUtil;
import api.utils.StarRunnable;
import api.utils.gui.ControlManagerHandler;
import org.apache.commons.io.IOUtils;
import org.schema.game.client.view.gui.newgui.GUITopBar;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.faction.FactionRelation;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIActivationHighlightCallback;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.input.InputState;
import soe.edencore.data.player.PlayerData;
import soe.edencore.gui.admintools.AdminToolsGUIControlManager;
import soe.edencore.gui.admintools.AdminToolsMenuPanel;
import soe.edencore.gui.admintools.player.PlayerDataEditorControlManager;
import soe.edencore.gui.admintools.player.group.GroupEditorControlManager;
import soe.edencore.server.ServerDatabase;
import soe.edencore.server.bot.EdenBot;
import soe.edencore.server.chat.ChatLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.ProtectionDomain;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * EdenCore.java
 * <Description>
 *
 * @since 03/10/2021
 * @author TheDerpGamer
 */
public class EdenCore extends StarMod {

    //Instance
    public EdenCore() {
        instance = this;
    }
    public static EdenCore instance;
    public static void main(String[] args) { }

    //Config
    private final String[] defaultConfig = {
            "debug-mode: false",
            "auto-save-frequency: 10000",
            "max-log-age: 30"
    };
    public boolean debugMode = false;
    public long autoSaveFrequency = 10000;
    public int maxLogAge = 30;

    //Permissions
    public final String[] defaultPermissions = {
            "player.chat.general.speak",
            "player.chat.general.read",
            "player.chat.faction.speak",
            "player.chat.faction.read"
    };

    //Data
    public EdenBot edenBot;
    private final String[] overwriteClasses = {
            "GUITextOverlay"
    };

    //GUI
    public AdminToolsGUIControlManager adminToolsGUIControlManager;
    public PlayerDataEditorControlManager playerDataEditorControlManager;
    public GroupEditorControlManager groupEditorControlManager;

    @Override
    public void onEnable() {
        instance = this;
        initConfig();
        initialize();
        registerListeners();
        startRunners();
    }

    @Override
    public void onDisable() {
        edenBot.chatWebhook.setUsername("EdenBot");
        edenBot.chatWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");
        edenBot.chatWebhook.setContent(":octagonal_sign: Server Stopped");
        try {
            edenBot.chatWebhook.execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public byte[] onClassTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] byteCode) {
        for(String name : overwriteClasses) {
            if(className.endsWith(name)) return overwriteClass(className, byteCode);
        }

        return super.onClassTransform(loader, className, classBeingRedefined, protectionDomain, byteCode);
    }

    private void initConfig() {
        FileConfiguration config = getConfig("config");
        config.saveDefault(defaultConfig);

        debugMode = config.getConfigurableBoolean("debug-mode", false);
        autoSaveFrequency = config.getConfigurableLong("auto-save-frequency", 10000);
        maxLogAge = config.getConfigurableInt("max-log-age", 30);
    }

    private void initialize() {
        edenBot = new EdenBot();
        edenBot.chatWebhook.setUsername("EdenBot");
        edenBot.chatWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");
        edenBot.chatWebhook.setContent(":white_check_mark: Server Started");
        try {
            edenBot.chatWebhook.execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void registerListeners() {
        StarLoader.registerListener(GUITopBarCreateEvent.class, new Listener<GUITopBarCreateEvent>() {
            @Override
            public void onEvent(final GUITopBarCreateEvent event) {
                PlayerData playerData = ServerDatabase.getPlayerData(GameClient.getClientPlayerState().getName());
                if(playerData != null && playerData.hasPermission(AdminToolsMenuPanel.usagePerms)) {
                    GUITopBar.ExpandedButton dropDownButton = event.getDropdownButtons().get(event.getDropdownButtons().size() - 1);
                    dropDownButton.addExpandedButton("ADMIN TOOLS", new GUICallback() {
                        @Override
                        public void callback(final GUIElement guiElement, MouseEvent mouseEvent) {
                            if(mouseEvent.pressedLeftMouse()) {
                                GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
                                if(adminToolsGUIControlManager == null) {
                                    adminToolsGUIControlManager = new AdminToolsGUIControlManager(event.getGuiTopBar().getState());
                                    ControlManagerHandler.registerNewControlManager(getSkeleton(), adminToolsGUIControlManager);
                                }
                                adminToolsGUIControlManager.setActive(true);
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

        StarLoader.registerListener(PlayerChatEvent.class, new Listener<PlayerChatEvent>() {
            @Override
            public void onEvent(PlayerChatEvent event) {
                if(event.getText().startsWith("/")) {
                    ChatLogger.handleCommand(event.getMessage().sender, event.getText());
                } else {
                    ChatLogger.handleMessage(event);
                }
            }
        }, this);

        StarLoader.registerListener(PlayerCustomCommandEvent.class, new Listener<PlayerCustomCommandEvent>() {
            @Override
            public void onEvent(PlayerCustomCommandEvent event) {
                ChatLogger.handleCommand(event.getSender().getName(), convertToString(event.getCommand().getCommand(), event.getArgs()));
            }
        }, this);

        StarLoader.registerListener(PlayerJoinWorldEvent.class, new Listener<PlayerJoinWorldEvent>() {
            @Override
            public void onEvent(PlayerJoinWorldEvent event) {
                ChatLogger.handlePlayerJoinEvent(event);
            }
        }, this);

        StarLoader.registerListener(PlayerLeaveWorldEvent.class, new Listener<PlayerLeaveWorldEvent>() {
            @Override
            public void onEvent(PlayerLeaveWorldEvent event) {
                ChatLogger.handlePlayerLeaveEvent(event);
            }
        }, this);

        StarLoader.registerListener(FactionCreateEvent.class, new Listener<FactionCreateEvent>() {
            @Override
            public void onEvent(FactionCreateEvent event) {
                edenBot.chatWebhook.setUsername("EdenBot");
                edenBot.chatWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");
                edenBot.chatWebhook.setContent(":new: " + event.getPlayer().getName() + " has created a new faction called " + event.getFaction().getName());
                try {
                    edenBot.chatWebhook.execute();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }, this);

        StarLoader.registerListener(PlayerJoinFactionEvent.class, new Listener<PlayerJoinFactionEvent>() {
            @Override
            public void onEvent(PlayerJoinFactionEvent event) {
                edenBot.chatWebhook.setUsername("EdenBot");
                edenBot.chatWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");
                edenBot.chatWebhook.setContent(":heavy_plus_sign: " + event.getPlayer().getName() + " has joined the faction " + event.getFaction().getName());
                try {
                    edenBot.chatWebhook.execute();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }, this);

        StarLoader.registerListener(PlayerLeaveFactionEvent.class, new Listener<PlayerLeaveFactionEvent>() {
            @Override
            public void onEvent(PlayerLeaveFactionEvent event) {
                edenBot.chatWebhook.setUsername("EdenBot");
                edenBot.chatWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");
                edenBot.chatWebhook.setContent(":heavy_minus_sign: " + event.getPlayer().getName() + " has left the faction " + event.getFaction().getName());
                try {
                    edenBot.chatWebhook.execute();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }, this);

        StarLoader.registerListener(PlayerDeathEvent.class, new Listener<PlayerDeathEvent>() {
            @Override
            public void onEvent(PlayerDeathEvent event) {
                edenBot.chatWebhook.setUsername("EdenBot");
                edenBot.chatWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");
                if(event.getDamager().isSegmentController()) {
                    SegmentController segmentController = (SegmentController) event.getDamager();
                    edenBot.chatWebhook.setContent(":skull_crossbones: " + event.getPlayer().getName() + " was killed by entity " + segmentController.getName() + " [" + GameCommon.getGameState().getFactionManager().getFaction(segmentController.getFactionId()).getName() + "]");
                } else if(event.getDamager() instanceof PlayerState) {
                    PlayerState playerState = (PlayerState) event.getDamager();
                    if(playerState.getFactionId() != 0) {
                        edenBot.chatWebhook.setContent(":skull_crossbones: " + event.getPlayer().getName() + " was killed by player " + playerState.getName() + " [" + GameCommon.getGameState().getFactionManager().getFaction(playerState.getFactionId()).getName() + "]");
                    } else {
                        edenBot.chatWebhook.setContent(":skull_crossbones: " + event.getPlayer().getName() + " was killed by player " + playerState.getName());
                    }
                } else {
                    edenBot.chatWebhook.setContent(":skull_crossbones: " + event.getPlayer().getName() + " has died");
                }
                try {
                    edenBot.chatWebhook.execute();
                } catch(IOException exception) {
                    exception.printStackTrace();
                }
            }
        }, this);

        StarLoader.registerListener(FactionRelationChangeEvent.class, new Listener<FactionRelationChangeEvent>() {
            @Override
            public void onEvent(FactionRelationChangeEvent event) {
                edenBot.chatWebhook.setUsername("EdenBot");
                edenBot.chatWebhook.setAvatarUrl("https://i.imgur.com/2Prc2ke.jpg");

                if(event.getNewRelation().equals(FactionRelation.RType.FRIEND)) {
                    edenBot.chatWebhook.setContent(":shield: " + event.getTo().getName() + " is now allied with " + event.getFrom().getName());
                } else if(event.getNewRelation().equals(FactionRelation.RType.ENEMY)) {
                    edenBot.chatWebhook.setContent(":crossed_swords: " + event.getTo().getName() + " is now at war with " + event.getFrom().getName());
                } else {
                    return;
                }

                try {
                    edenBot.chatWebhook.execute();
                } catch(IOException exception) {
                    exception.printStackTrace();
                }
            }
        }, this);
    }

    private void startRunners() {
        //Auto Saver
        new StarRunnable() {
            @Override
            public void run() {
                PersistentObjectUtil.save(getSkeleton());
            }
        }.runTimer(this, autoSaveFrequency);
    }

    private String convertToString(String command, String... args) {
        StringBuilder builder = new StringBuilder();
        builder.append(command);
        for(String arg : args) builder.append(" ").append(arg);
        return builder.toString();
    }

    private byte[] overwriteClass(String className, byte[] byteCode) {
        byte[] bytes = null;
        try {
            ZipInputStream file = new ZipInputStream(new FileInputStream(this.getSkeleton().getJarFile()));
            while (true) {
                ZipEntry nextEntry = file.getNextEntry();
                if (nextEntry == null) break;
                if (nextEntry.getName().endsWith(className + ".class")) {
                    bytes = IOUtils.toByteArray(file);
                }
            }
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bytes != null) {
            DebugFile.log("[ImmersivePlanets]: Overwrote Class " + className, this);
            return bytes;
        } else {
            return byteCode;
        }
    }
}

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
import soe.edencore.data.player.PlayerRank;
import soe.edencore.gui.admintools.AdminToolsGUIControlManager;
import soe.edencore.gui.admintools.AdminToolsMenuPanel;
import soe.edencore.gui.admintools.player.PlayerDataEditorControlManager;
import soe.edencore.gui.admintools.player.group.GroupEditorControlManager;
import soe.edencore.server.ServerDatabase;
import soe.edencore.server.bot.BotThread;
import soe.edencore.server.bot.commands.ListCommand;
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
            "max-log-age: 30",
            "bot-token: BOT TOKEN",
            "channel-id: CHANNEL ID"
    };
    public boolean debugMode = false;
    public long autoSaveFrequency = 10000;
    public int maxLogAge = 30;
    public String botToken;
    public long channelId;

    //Permissions
    public final String[] defaultPermissions = {
            "chat.general.speak",
            "chat.general.read",
            "chat.faction.speak",
            "chat.faction.read",
            "chat.command.list"
    };

    //Data
    public BotThread botThread;
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
        initializeBot();
        registerCommands();
        registerListeners();
        startRunners();
    }

    @Override
    public void onDisable() {
        botThread.getBot().sendMessage("EdenBot", ":octagonal_sign: Server Stopped");
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
        botToken = config.getString("bot-token");
        channelId = config.getLong("channel-id");
    }

    private void initializeBot() {
        (botThread = new BotThread(botToken, channelId)).start();
    }

    private void registerCommands() {
        StarLoader.registerCommand(new ListCommand());
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
                if(!event.getMessage().sender.equals("EdenBot")) {
                    PlayerData playerData = ServerDatabase.getPlayerData(event.getMessage().sender);
                    if(playerData != null) {
                        PlayerRank playerRank = playerData.getRank();
                        //Todo: Color and Emote support
                        botThread.getBot().sendMessage("[" + playerRank.rankName + "] " + playerData.getPlayerName(), event.getText());
                    }
                }
            }
        }, this);


        StarLoader.registerListener(PlayerJoinWorldEvent.class, new Listener<PlayerJoinWorldEvent>() {
            @Override
            public void onEvent(PlayerJoinWorldEvent event) {
                if(ServerDatabase.getPlayerData(event.getPlayerName()) == null) {
                    botThread.getBot().sendMessage("EdenBot", ":confetti_ball: Everyone welcome " + event.getPlayerName() + " to Skies of Eden!");
                    ServerDatabase.addNewPlayerData(event.getPlayerName());
                } else {
                    botThread.getBot().sendMessage("EdenBot", ":arrow_right: " + event.getPlayerName() + " has joined the server");
                }
            }
        }, this);

        StarLoader.registerListener(PlayerLeaveWorldEvent.class, new Listener<PlayerLeaveWorldEvent>() {
            @Override
            public void onEvent(PlayerLeaveWorldEvent event) {
                botThread.getBot().sendMessage("EdenBot", ":door: " + event.getPlayerName() + " has left the server");
            }
        }, this);

        StarLoader.registerListener(FactionCreateEvent.class, new Listener<FactionCreateEvent>() {
            @Override
            public void onEvent(FactionCreateEvent event) {
                botThread.getBot().sendMessage("EdenBot", ":new: " + event.getPlayer().getName() + " has created a new faction called " + event.getFaction().getName());
            }
        }, this);

        StarLoader.registerListener(PlayerJoinFactionEvent.class, new Listener<PlayerJoinFactionEvent>() {
            @Override
            public void onEvent(PlayerJoinFactionEvent event) {
                botThread.getBot().sendMessage("EdenBot", ":heavy_plus_sign: " + event.getPlayer().getName() + " has joined the faction " + event.getFaction().getName());
            }
        }, this);

        StarLoader.registerListener(PlayerLeaveFactionEvent.class, new Listener<PlayerLeaveFactionEvent>() {
            @Override
            public void onEvent(PlayerLeaveFactionEvent event) {
                botThread.getBot().sendMessage("EdenBot", ":heavy_minus_sign: " + event.getPlayer().getName() + " has left the faction " + event.getFaction().getName());
            }
        }, this);

        StarLoader.registerListener(PlayerDeathEvent.class, new Listener<PlayerDeathEvent>() {
            @Override
            public void onEvent(PlayerDeathEvent event) {
                if(event.getDamager().isSegmentController()) {
                    SegmentController segmentController = (SegmentController) event.getDamager();
                    botThread.getBot().sendMessage("EdenBot", ":skull_crossbones: " + event.getPlayer().getName() + " was killed by entity " + segmentController.getName() + " [" + GameCommon.getGameState().getFactionManager().getFaction(segmentController.getFactionId()).getName() + "]");
                } else if(event.getDamager() instanceof PlayerState) {
                    PlayerState playerState = (PlayerState) event.getDamager();
                    if(playerState.getFactionId() != 0) {
                        botThread.getBot().sendMessage("EdenBot", ":skull_crossbones: " + event.getPlayer().getName() + " was killed by player " + playerState.getName() + " [" + GameCommon.getGameState().getFactionManager().getFaction(playerState.getFactionId()).getName() + "]");
                    } else {
                        botThread.getBot().sendMessage("EdenBot", ":skull_crossbones: " + event.getPlayer().getName() + " was killed by player " + playerState.getName());
                    }
                } else {
                    botThread.getBot().sendMessage("EdenBot", ":skull_crossbones: " + event.getPlayer().getName() + " has died");
                }
            }
        }, this);

        StarLoader.registerListener(FactionRelationChangeEvent.class, new Listener<FactionRelationChangeEvent>() {
            @Override
            public void onEvent(FactionRelationChangeEvent event) {
                if(event.getNewRelation().equals(FactionRelation.RType.FRIEND)) {
                    botThread.getBot().sendMessage("EdenBot", ":shield: " + event.getTo().getName() + " is now allied with " + event.getFrom().getName());
                } else if(event.getNewRelation().equals(FactionRelation.RType.ENEMY)) {
                    botThread.getBot().sendMessage("EdenBot", ":crossed_swords: " + event.getTo().getName() + " is now at war with " + event.getFrom().getName());
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

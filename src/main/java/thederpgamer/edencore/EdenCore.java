package thederpgamer.edencore;

import api.common.GameClient;
import api.common.GameCommon;
import api.common.GameServer;
import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceActivateEvent;
import api.listener.events.block.SegmentPieceAddEvent;
import api.listener.events.block.SegmentPieceRemoveEvent;
import api.listener.events.controller.ClientInitializeEvent;
import api.listener.events.controller.ServerInitializeEvent;
import api.listener.events.draw.RegisterWorldDrawersEvent;
import api.listener.events.entity.SegmentControllerInstantiateEvent;
import api.listener.events.gui.GUITopBarCreateEvent;
import api.listener.events.gui.MainWindowTabAddEvent;
import api.listener.events.input.KeyPressEvent;
import api.listener.events.player.PlayerDeathEvent;
import api.listener.events.player.PlayerJoinWorldEvent;
import api.listener.events.player.PlayerPickupFreeItemEvent;
import api.listener.events.player.PlayerSpawnEvent;
import api.listener.events.world.SimulationJobExecuteEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.mod.config.PersistentObjectUtil;
import api.network.packets.PacketUtil;
import api.utils.StarRunnable;
import api.utils.game.PlayerUtils;
import api.utils.game.SegmentControllerUtils;
import api.utils.game.inventory.InventoryUtils;
import api.utils.gui.ModGUIHandler;
import glossar.GlossarCategory;
import glossar.GlossarEntry;
import glossar.GlossarInit;
import org.apache.commons.io.IOUtils;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.view.gui.newgui.GUITopBar;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.faction.FactionManager;
import org.schema.game.server.data.simulation.jobs.SpawnPiratePatrolPartyJob;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIActivationHighlightCallback;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.input.InputState;
import org.schema.schine.resource.ResourceLoader;
import thederpgamer.edencore.commands.*;
import thederpgamer.edencore.commands.events.EventEditorCommand;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.data.player.PlayerData;
import thederpgamer.edencore.element.ElementManager;
import thederpgamer.edencore.element.items.PrizeBars;
import thederpgamer.edencore.gui.buildsectormenu.BuildSectorMenuControlManager;
import thederpgamer.edencore.gui.eventsmenu.EventsMenuControlManager;
import thederpgamer.edencore.gui.exchangemenu.ExchangeMenu;
import thederpgamer.edencore.manager.ConfigManager;
import thederpgamer.edencore.manager.ResourceManager;
import thederpgamer.edencore.manager.TransferManager;
import thederpgamer.edencore.network.client.buildsector.*;
import thederpgamer.edencore.network.client.event.ClientModifyEventPacket;
import thederpgamer.edencore.network.client.exchange.ExchangeItemCreatePacket;
import thederpgamer.edencore.network.client.exchange.ExchangeItemRemovePacket;
import thederpgamer.edencore.network.client.exchange.PlayerBuyBPPacket;
import thederpgamer.edencore.network.client.exchange.RequestSpawnEntryPacket;
import thederpgamer.edencore.network.client.misc.NavigationUpdateRequestPacket;
import thederpgamer.edencore.network.client.misc.RequestClientCacheUpdatePacket;
import thederpgamer.edencore.network.client.misc.RequestEntityDeletePacket;
import thederpgamer.edencore.network.client.misc.RequestMetaObjectPacket;
import thederpgamer.edencore.network.server.*;
import thederpgamer.edencore.network.server.event.ServerSendEventDataPacket;
import thederpgamer.edencore.utils.BuildSectorUtils;
import thederpgamer.edencore.utils.ClassUtils;
import thederpgamer.edencore.utils.DataUtils;
import thederpgamer.edencore.utils.DateUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Main class for EdenCore mod.
 *
 * @author TheDerpGamer
 * @version 1.0 - [06/27/2021]
 */
public class EdenCore extends StarMod {
	// Instance
	private static EdenCore instance;
	// Overwrites
	private final String[] overwriteClasses = {"PlayerState", "BlueprintEntry", "TorchBeam", "MarkerBeam", "WarpgateUnit"};
	// Disabled Blocks
	public static final short[] disabledBlocks = {
			347, // Shop Module
			291, // Faction Module
			667, // Shipyard Computer
			683, // Race Gate Controller
			542 // Warp Gate Computer
	};
	// Disabled Tabs
	private final String[] disabledTabs = {"FLEETS", "SHOP", "REPAIRS", "TRADE", "SET PRICES"};
	// GUI
	public ExchangeMenu exchangeMenuControlManager;
	public BuildSectorMenuControlManager buildSectorMenuControlManager;
	public EventsMenuControlManager eventsMenuControlManager;

	public EdenCore() {
	}

	public static void main(String[] args) {
	}

	public static EdenCore getInstance() {
		return instance;
	}

	@Override
	public byte[] onClassTransform(String className, byte[] byteCode) {
		for(String name : overwriteClasses) {
			if(className.endsWith(name)) return overwriteClass(className, byteCode);
		}
		return super.onClassTransform(className, byteCode);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		instance = this;
		ConfigManager.initialize(this);
		TransferManager.initialize();
		registerPackets();
		registerListeners();
		registerCommands();
		startRunners();
	}

	@Override
	public void onServerCreated(ServerInitializeEvent serverInitializeEvent) {
		super.onServerCreated(serverInitializeEvent);
//		EventManager.doRunners();
	}

	@Override
	public void onClientCreated(ClientInitializeEvent clientInitializeEvent) {
		super.onClientCreated(clientInitializeEvent);
		initGlossary();
	}

	@Override
	public void onBlockConfigLoad(BlockConfig blockConfig) {
		// Items
		ElementManager.addItemGroup(new PrizeBars());
		ElementManager.initialize();
		logInfo("Initialized Blocks");
	}

	@Override
	public void onResourceLoad(ResourceLoader resourceLoader) {
		ResourceManager.loadResources(resourceLoader);
	}

	private void initGlossary() {
		GlossarInit.initGlossar(this);
		GlossarCategory rules = new GlossarCategory("Server Info and Rules");
		rules.addEntry(new GlossarEntry("Server Info", "Skies of Eden is a modded survival StarMade server run by the SOE staff team and hosted on CBS hardware.\n" + "We work hard to bring new features and content to the server, and we hope you enjoy your time here.\n" + "Note that not all features are complete, and some may be buggy. If you find any bugs, please report them to a staff member.\n" + "Please read the rules section before playing on the server, and be sure to join our discord at https://discord.gg/qxzvBxT."));
		rules.addEntry(new GlossarEntry("Rules", "1) Be polite and respectful in chat.\n" + "2) Do not spam chat or advertise links to other servers.\n" + "3) Do not use any cheats, glitches, exploits, etc. that give you an unfair advantage over other players. If you find a bug, please report it to a staff member.\n" + "4) Keep politics at an absolute minimum. This is a starmade server, not a political forum.\n" + "5) Hate speech and hate symbols are not tolerated. This includes racism, sexism, homophobia, etc.\n" + "6) Do not intentionally create server lag. If your entity is lagging the server, it may be deleted by staff without compensation.\n" + "7) Do not create home-bases on planets.\n" + "8) Do not attempt to attack or capture public infrastructure such as warpgates.\n" + "9) Use common sense. If you are unsure about something, ask a staff member.\n" + "10) Repeated or serious violations of any of the server rules can result in bans of the offenders, deletion of ships/stations, and penalties to anyone involved or associated."));
		GlossarInit.addCategory(rules);
		GlossarCategory edenCore = new GlossarCategory("Eden Core");
		edenCore.addEntry(new GlossarEntry("Build Sectors", "Build Sectors are special sectors unique to each player where you can build freely in creative mode. They are protected from other players and hostiles.\n" + "You can invite other players to your build sector, set permissions, spawn entities, and more using the build sector menu.\nTo access the build sector menu, use the - key on your keypad or look in the top right menu bar under PLAYER.\n" + "If you prefer to use commands, you can use /help build_sector to view usable commands."));
		edenCore.addEntry(new GlossarEntry("Banking", "Banking is a feature that allows you to send money to other players.\n" + "To send money, use /bank_send <player_name> <amount> [optional_message].\n" + "To view the last 10 transactions, use /bank_list."));
		edenCore.addEntry(new GlossarEntry("Server Exchange", "Every day you log in, you will receive 2 Bronze Bars. You can only receive this reward once per day.\n" + "You can use these bars in the Server Exchange menu, which can be opened with the * key on your keypad, or by looking in the top right menu bar under PLAYER.\n" + "You can use the Server Exchange to buy items from the server shop, such as resources, items, and blueprints. Please note that some of these features are work in progress and may not always be available.\n" + "Some items in the Server Exchange require silver or gold bars instead of bronze. To upgrade your bronze bars into silver or gold, see the EXCHANGE tab in the Server Exchange menu."));
		edenCore.addEntry(new GlossarEntry("Listing Blueprints in the Exchange", "Players are able to sell blueprints in the COMMUNITY tab of the blueprint exchange."));
		edenCore.addEntry(new GlossarEntry("Donator Perks", "Donators can use chat colors and get free bars each day.\n" + "Chat colors:\n" + " - &0 = Transparent\n" + " - &1 = White\n" + " - &2 = Light Grey\n" + " - &3 = Grey\n" + " - &4 = Dark Grey\n" + " - &5 = Black\n" + " - &y = Yellow\n" + " - &o = Orange\n" + " - &r = Red\n" + " - &m = Magenta\n" + " - &p = Pink\n" + " - &b = Blue\n" + " - &c = Cyan\n" + " - &g = Green\n"));
		GlossarInit.addCategory(edenCore);
		logInfo("Initialized Glossary");
	}

	private void registerPackets() {
		PacketUtil.registerPacket(RequestClientCacheUpdatePacket.class);
		PacketUtil.registerPacket(RequestMetaObjectPacket.class);
		PacketUtil.registerPacket(RequestMoveToBuildSectorPacket.class);
		PacketUtil.registerPacket(RequestMoveFromBuildSectorPacket.class);
		PacketUtil.registerPacket(RequestBuildSectorProtectPacket.class);
		PacketUtil.registerPacket(RequestBuildSectorInvitePacket.class);
		PacketUtil.registerPacket(RequestBuildSectorKickPacket.class);
		PacketUtil.registerPacket(RequestBuildSectorBanPacket.class);
		PacketUtil.registerPacket(RequestSpawnEntryPacket.class);
		PacketUtil.registerPacket(RequestEntityDeletePacket.class);
		PacketUtil.registerPacket(UpdateBuildSectorPermissionsPacket.class);
		PacketUtil.registerPacket(ExchangeItemCreatePacket.class);
		PacketUtil.registerPacket(ExchangeItemRemovePacket.class);
		PacketUtil.registerPacket(SendCacheUpdatePacket.class);
		PacketUtil.registerPacket(PlayerWarpIntoEntityPacket.class);
		PacketUtil.registerPacket(NavigationUpdateRequestPacket.class);
		PacketUtil.registerPacket(NavigationUpdatePacket.class);
		PacketUtil.registerPacket(PlayerBuyBPPacket.class);
		PacketUtil.registerPacket(SendGuideMenuPacket.class);
		PacketUtil.registerPacket(ClientModifyEventPacket.class);
		PacketUtil.registerPacket(ServerSendEventDataPacket.class);
		logInfo("Registered Packets");
	}

	private void registerListeners() {
		StarLoader.registerListener(KeyPressEvent.class, new Listener<KeyPressEvent>() {
			@Override
			public void onEvent(KeyPressEvent event) {
				char buildSectorKey = ConfigManager.getKeyBinding("build-sector-key");
				if(buildSectorKey != '\0' && event.getChar() == buildSectorKey) {
					if(buildSectorMenuControlManager == null) {
						buildSectorMenuControlManager = new BuildSectorMenuControlManager();
						ModGUIHandler.registerNewControlManager(getSkeleton(), buildSectorMenuControlManager);
					}
					if(!GameClient.getClientState().getController().isChatActive() && GameClient.getClientState().getController().getPlayerInputs().isEmpty()) {
						GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
						GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().deactivateAll();
						buildSectorMenuControlManager.setActive(true);
					}
				}
				char exchangeKey = ConfigManager.getKeyBinding("exchange-menu-key");
				if(exchangeKey != '\0' && event.getChar() == exchangeKey) {
					if(exchangeMenuControlManager == null) {
						exchangeMenuControlManager = new ExchangeMenu();
						ModGUIHandler.registerNewControlManager(getSkeleton(), exchangeMenuControlManager);
					}
					if(!GameClient.getClientState().getController().isChatActive() && GameClient.getClientState().getController().getPlayerInputs().isEmpty()) {
						GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
						GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().deactivateAll();
						exchangeMenuControlManager.setActive(true);
						exchangeMenuControlManager.getMenuPanel().recreateTabs();
					}
				}

				/*
				char eventsKey = ConfigManager.getKeyBinding("events-menu-key");
				if(eventsKey != '\0' && event.getChar() == eventsKey && event.getKey() == Keyboard.KEY_DIVIDE) {
					if(eventsMenuControlManager == null) {
						eventsMenuControlManager = new EventsMenuControlManager();
						ModGUIHandler.registerNewControlManager(getSkeleton(), eventsMenuControlManager);
					}

					if(!GameClient.getClientState().getController().isChatActive() && GameClient.getClientState().getController().getPlayerInputs().isEmpty()) {
						GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
						GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().deactivateAll();
						eventsMenuControlManager.setActive(true);
					}
				}
				 */
				char guideKey = ConfigManager.getKeyBinding("guide-menu-key");
				if(guideKey != '\0' && event.getChar() == guideKey) activateGuideMenu();
			}
		}, this);

		StarLoader.registerListener(SimulationJobExecuteEvent.class, new Listener<SimulationJobExecuteEvent>() {
			@Override
			public void onEvent(SimulationJobExecuteEvent event) {
				if(event.getSimulationJob() instanceof SpawnPiratePatrolPartyJob) {
					SpawnPiratePatrolPartyJob job = (SpawnPiratePatrolPartyJob) event.getSimulationJob();
					Vector3i from = (Vector3i) ClassUtils.getField(job, "from");
					Vector3i to = (Vector3i) ClassUtils.getField(job, "to");
					if(DataUtils.isBuildSector(from) || DataUtils.isBuildSector(to)) event.setCanceled(true);
				} else if(DataUtils.isBuildSector(event.getStartLocation())) event.setCanceled(true);
			}
		}, this);
		StarLoader.registerListener(GUITopBarCreateEvent.class, new Listener<GUITopBarCreateEvent>() {
			@Override
			public void onEvent(GUITopBarCreateEvent event) {
				GUITopBar.ExpandedButton dropDownButton = event.getDropdownButtons().get(event.getDropdownButtons().size() - 1);
				if(buildSectorMenuControlManager == null) {
					buildSectorMenuControlManager = new BuildSectorMenuControlManager();
					ModGUIHandler.registerNewControlManager(getSkeleton(), buildSectorMenuControlManager);
				}
				if(exchangeMenuControlManager == null) {
					exchangeMenuControlManager = new ExchangeMenu();
					ModGUIHandler.registerNewControlManager(getSkeleton(), exchangeMenuControlManager);
				}

						/*
						if (eventsMenuControlManager == null) {
							eventsMenuControlManager = new EventsMenuControlManager();
							ModGUIHandler.registerNewControlManager(getSkeleton(), eventsMenuControlManager);
						}
						 */
				dropDownButton.addExpandedButton("BUILD SECTOR", new GUICallback() {
					@Override
					public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
						if(mouseEvent.pressedLeftMouse()) {
							GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
							GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().deactivateAll();
							buildSectorMenuControlManager.setActive(true);
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
				dropDownButton.addExpandedButton("EXCHANGE", new GUICallback() {
					@Override
					public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
						if(mouseEvent.pressedLeftMouse()) {
							GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
							GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().deactivateAll();
							exchangeMenuControlManager.setActive(true);
							try {
								exchangeMenuControlManager.getMenuPanel().recreateTabs();
							} catch(Exception exception) {
								exception.printStackTrace();
							}
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

						/*
						dropDownButton.addExpandedButton("EVENTS", new GUICallback() {
							@Override
							public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
								if (mouseEvent.pressedLeftMouse()) {
									GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
									GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().deactivateAll();
									eventsMenuControlManager.setActive(true);
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
						 */
				dropDownButton.addExpandedButton("GUIDE", new GUICallback() {
					@Override
					public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
						if(mouseEvent.pressedLeftMouse()) {
							GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
							GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().deactivateAll();
							activateGuideMenu();
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
		StarLoader.registerListener(MainWindowTabAddEvent.class, new Listener<MainWindowTabAddEvent>() {
			@Override
			public void onEvent(MainWindowTabAddEvent event) {
				if(DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
					for(String s : disabledTabs) {
						if(event.getTitleAsString().equals(Lng.str(s))) {
							event.setCanceled(true);
							event.getWindow().cleanUp();
						}
					}
				}
			}
		}, this);
		StarLoader.registerListener(RegisterWorldDrawersEvent.class, new Listener<RegisterWorldDrawersEvent>() {
			@Override
			public void onEvent(RegisterWorldDrawersEvent event) {
				// event.getModDrawables().add(new BuildSectorHudDrawer());
			}
		}, this);
		StarLoader.registerListener(SegmentControllerInstantiateEvent.class, new Listener<SegmentControllerInstantiateEvent>() {
			@Override
			public void onEvent(final SegmentControllerInstantiateEvent event) {
				new StarRunnable() {
					@Override
					public void run() {
						try {
							if(event.getController() == null || !event.getController().isFullyLoadedWithDock()) return;
							if(event.getController().getSector(new Vector3i()).x > 100000000 || event.getController().getSector(new Vector3i()).y > 100000000 || event.getController().getSector(new Vector3i()).z > 100000000) updateClientCacheData();
							if(DataUtils.isBuildSector(event.getController().getSector(new Vector3i()))) {
								BuildSectorData sectorData = DataUtils.getSectorData(event.getController().getSector(new Vector3i()));
								if(sectorData != null) {
									if(event.getController().getFactionId() == FactionManager.PIRATES_ID && !BuildSectorUtils.getPlayersWithEnemySpawnPerms(sectorData).contains(event.getController().getSpawner())) DataUtils.deleteEnemies(sectorData, 0);
									updateClientCacheData();
								}
							}
						} catch(Exception exception) {
							getInstance().logException("Encountered an exception while trying to check if a new SegmentController \"" + event.getController().getName() + "\" was in a build sector", exception);
						}
					}
				}.runLater(getInstance(), 3);
			}
		}, this);
		StarLoader.registerListener(SegmentPieceAddEvent.class, new Listener<SegmentPieceAddEvent>() {
			@Override
			public void onEvent(SegmentPieceAddEvent event) {
				try {
					PlayerState playerState = SegmentControllerUtils.getAttachedPlayers(event.getSegmentController()).get(0);
					if(DataUtils.isPlayerInAnyBuildSector(playerState)) {
						BuildSectorData sectorData = DataUtils.getPlayerCurrentBuildSector(playerState);
						if(!sectorData.hasPermission(playerState.getName(), "EDIT")) {
							PlayerUtils.sendMessage(playerState, "You don't have permission to do this!");
							event.setCanceled(true);
						}
					}
				} catch(Exception ignored) {
				}
			}
		}, this);
		StarLoader.registerListener(SegmentPieceRemoveEvent.class, new Listener<SegmentPieceRemoveEvent>() {
			@Override
			public void onEvent(SegmentPieceRemoveEvent event) {
				try {
					PlayerState playerState = SegmentControllerUtils.getAttachedPlayers(event.getSegment().getSegmentController()).get(0);
					if(DataUtils.isPlayerInAnyBuildSector(playerState)) {
						BuildSectorData sectorData = DataUtils.getPlayerCurrentBuildSector(playerState);
						if(!sectorData.hasPermission(playerState.getName(), "EDIT")) {
							PlayerUtils.sendMessage(playerState, "You don't have permission to do this!");
							event.setCanceled(true);
						}
					}
				} catch(Exception ignored) {
				}
			}
		}, this);
		StarLoader.registerListener(SegmentPieceActivateEvent.class, new Listener<SegmentPieceActivateEvent>() {
			@Override
			public void onEvent(SegmentPieceActivateEvent event) {
				try {
					if(DataUtils.isBuildSector(event.getSegmentPiece().getSegmentController().getSector(new Vector3i()))) {
						for(short id : disabledBlocks) {
							if(event.getSegmentPiece().getType() == id) {
								event.setCanceled(true);
								return;
							}
						}
					}
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			}
		}, this);
		StarLoader.registerListener(SegmentPieceActivateByPlayer.class, new Listener<SegmentPieceActivateByPlayer>() {
			@Override
			public void onEvent(SegmentPieceActivateByPlayer event) {
				try {
					if(DataUtils.isPlayerInAnyBuildSector(event.getPlayer())) {
						for(short id : disabledBlocks) {
							if(event.getSegmentPiece().getType() == id && !event.getPlayer().isAdmin()) {
								event.setCanceled(true);
							}
						}
						BuildSectorData sectorData = DataUtils.getPlayerCurrentBuildSector(event.getPlayer());
						if(!sectorData.hasPermission(event.getPlayer().getName(), "EDIT")) {
							PlayerUtils.sendMessage(event.getPlayer(), "You don't have permission to do this!");
							event.setCanceled(true);
						}
					}
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			}
		}, this);
		StarLoader.registerListener(PlayerPickupFreeItemEvent.class, new Listener<PlayerPickupFreeItemEvent>() {
			@Override
			public void onEvent(PlayerPickupFreeItemEvent event) {
				try {
					PlayerState playerState = event.getPlayer();
					if(DataUtils.isPlayerInAnyBuildSector(playerState)) {
						BuildSectorData sectorData = DataUtils.getPlayerCurrentBuildSector(playerState);
						if(!sectorData.hasPermission(playerState.getName(), "PICKUP")) {
							PlayerUtils.sendMessage(event.getPlayer(), "You don't have permission to do this!");
							event.setCanceled(true);
						}
					}
				} catch(Exception exception) {
					exception.printStackTrace();
				}
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
			public void onEvent(final PlayerSpawnEvent event) {
				new StarRunnable() {
					@Override
					public void run() {
						try {
							if(!event.isServer() && !DataUtils.isPlayerInAnyBuildSector(event.getPlayer().getOwnerState())) PacketUtil.sendPacketToServer(new RequestClientCacheUpdatePacket());
						} catch(Exception exception) {
							exception.printStackTrace();
						}
					}
				}.runLater(getInstance(), 5);
				if(DataUtils.isPlayerInAnyBuildSector(event.getPlayer().getOwnerState())) queueSpawnSwitch(event.getPlayer().getOwnerState());
			}
		}, this);
		StarLoader.registerListener(PlayerJoinWorldEvent.class, new Listener<PlayerJoinWorldEvent>() {
			@Override
			public void onEvent(final PlayerJoinWorldEvent event) {
				if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) {
					if(DataUtils.getBuildSector(event.getPlayerName()) == null) DataUtils.createNewBuildSector(event.getPlayerName());
							/*
							new StarRunnable() {
								@Override
								public void run() {
									try {
										PacketUtil.sendPacket(GameServer.getServerState().getPlayerFromName(event.getPlayerName()), new SendCacheUpdatePacket(GameServer.getServerState().getPlayerFromName(event.getPlayerName())));
									} catch(PlayerNotFountException exception) {
										exception.printStackTrace();
									}
								}
							}.runLater(EdenCore.this, 100);
							 */
					new StarRunnable() {
						@Override
						public void run() {
							PlayerState playerState = GameCommon.getPlayerFromName(event.getPlayerName());
							if(playerState != null && playerState.isOnServer()) {
								PlayerData playerData = DataUtils.getPlayerData(playerState);
								Date date = new Date(playerData.lastDailyPrizeClaim);
								if(DateUtils.getAgeDays(date) >= 1.0f) {
									InventoryUtils.addItem(playerState.getPersonalInventory(), ElementManager.getItem("Bronze Bar").getId(), 2);
									playerData.lastDailyPrizeClaim = System.currentTimeMillis();
									PersistentObjectUtil.save(getSkeleton());
									PlayerUtils.sendMessage(playerState, "You have been given 2 Bronze Bars for logging in. Thanks for playing!");
								}
							}
						}
					}.runLater(EdenCore.this, 10000);
				}
			}
		}, this);
		logInfo("Registered Listeners");
	}

	private void registerCommands() {
		StarLoader.registerCommand(new EventEditorCommand());
		StarLoader.registerCommand(new SaveEntityCommand());
		StarLoader.registerCommand(new LoadEntityCommand());
		StarLoader.registerCommand(new ListEntityCommand());
		StarLoader.registerCommand(new BuildSectorCommand());
		StarLoader.registerCommand(new AwardBarsCommand());
		StarLoader.registerCommand(new BankingSendMoneyCommand());
		StarLoader.registerCommand(new BankingListCommand());
		StarLoader.registerCommand(new BankingAdminListCommand());
		StarLoader.registerCommand(new CountdownCommand());
		StarLoader.registerCommand(new GuideCommand());
		StarLoader.registerCommand(new ResetEventsCommand());
		StarLoader.registerCommand(new EditEventCommand());
		logInfo("Registered Commands");
	}

	private void startRunners() {
		if(GameCommon.isOnSinglePlayer() || GameCommon.isDedicatedServer()) {
			new StarRunnable() {
				@Override
				public void run() {
					updateClientCacheData();
				}
			}.runTimer(this, 1000);
			new StarRunnable() {
				long last = System.currentTimeMillis() - 1800000L + 10000L;

				public void run() {
					if(last + 1800000L < System.currentTimeMillis()) {
						last = System.currentTimeMillis();
						for(PlayerState playerState : GameServer.getServerState().getPlayerStatesByName().values())
							PlayerUtils.sendMessage(playerState, "The guide can be accessed via the /guide command or via the GUIDE button under the PLAYER tab in the top right.");
					}
				}
			}.runTimer(instance, 10L);
		}
	}

	public void activateGuideMenu() {
		try {
			ModGUIHandler.getGUIControlManager("glossarPanel").setActive(true);
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

	public void updateClientCacheData() {
		if(GameCommon.isOnSinglePlayer() || GameCommon.isDedicatedServer()) {
			try {
				for(PlayerState playerState : GameServer.getServerState().getPlayerStatesByName().values()) {
					PacketUtil.sendPacket(playerState, new SendCacheUpdatePacket(playerState));
				}
			} catch(Exception exception) {
				instance.logException("Encountered an exception while trying to updateClients client cache data", exception);
			}
		}
	}

	private void queueSpawnSwitch(final PlayerState playerState) {
		new StarRunnable() {
			@Override
			public void run() {
				if(!DataUtils.isPlayerInAnyBuildSector(playerState)) cancel();
				if(!playerState.hasSpawnWait) { // Wait until player has spawned, then warp them
					try {
						DataUtils.movePlayerFromBuildSector(playerState);
					} catch(Exception exception) {
						getInstance().logException("Encountered a severe exception while trying to move player \"" + playerState.getName() + "\" out of a build sector! Report this ASAP!", exception);
						playerState.setUseCreativeMode(false);
						if(!playerState.isAdmin()) playerState.setHasCreativeMode(false);
						PlayerUtils.sendMessage(playerState, "The server encountered a severe exception while trying to load you in and your" + " player state may be corrupted as a result. Report this to an admin ASAP!");
					}
					cancel();
				}
			}
		}.runTimer(this, 300);
	}

	private byte[] overwriteClass(String className, byte[] byteCode) {
		byte[] bytes = null;
		try {
			ZipInputStream file = new ZipInputStream(Files.newInputStream(getSkeleton().getJarFile().toPath()));
			while(true) {
				ZipEntry nextEntry = file.getNextEntry();
				if(nextEntry == null) break;
				if(nextEntry.getName().endsWith(className + ".class")) bytes = IOUtils.toByteArray(file);
			}
			file.close();
		} catch(IOException exception) {
			exception.printStackTrace();
		}
		if(bytes != null) return bytes;
		else return byteCode;
	}

	public void activateGuideMenuForPlayer(PlayerState playerState) {
		PacketUtil.sendPacket(playerState, new SendGuideMenuPacket());
	}
}

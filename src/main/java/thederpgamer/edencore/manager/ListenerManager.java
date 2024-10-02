package thederpgamer.edencore.manager;

import api.common.GameClient;
import api.common.GameCommon;
import api.listener.Listener;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceActivateEvent;
import api.listener.events.block.SegmentPieceAddEvent;
import api.listener.events.block.SegmentPieceRemoveEvent;
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
import api.mod.config.PersistentObjectUtil;
import api.network.packets.PacketUtil;
import api.utils.StarRunnable;
import api.utils.game.PlayerUtils;
import api.utils.game.SegmentControllerUtils;
import api.utils.game.inventory.InventoryUtils;
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
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.data.player.PlayerData;
import thederpgamer.edencore.element.ElementManager;
import thederpgamer.edencore.gui.exchangemenu.ExchangeDialog;
import thederpgamer.edencore.network.old.client.misc.RequestClientCacheUpdatePacket;
import thederpgamer.edencore.utils.BuildSectorUtils;
import thederpgamer.edencore.utils.ClassUtils;
import thederpgamer.edencore.utils.DataUtils;
import thederpgamer.edencore.utils.DateUtils;

import java.sql.Date;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class ListenerManager {

	public static final String[] disabledTabs = {"FLEETS", "SHOP", "REPAIRS", "TRADE", "SET PRICES"};
	public static final short[] disabledBlocks = {
			347, // Shop Module
			291, // Faction Module
			667, // Shipyard Computer
			683, // Race Gate Controller
			542 // Warp Gate Computer
	};

	public static void initialize(final EdenCore instance) {
		StarLoader.registerListener(KeyPressEvent.class, new Listener<KeyPressEvent>() {
			@Override
			public void onEvent(KeyPressEvent event) {
				char buildSectorKey = ConfigManager.getKeyBinding("build-sector-key");
				char exchangeKey = ConfigManager.getKeyBinding("exchange-menu-key");
				char guideKey = ConfigManager.getKeyBinding("guide-menu-key");
				char eventsKey = ConfigManager.getKeyBinding("events-menu-key");

				if(buildSectorKey != '\0' && event.getChar() == buildSectorKey) UIManager.openBuildSectorMenu();
				else if(exchangeKey != '\0' && event.getChar() == exchangeKey) (new ExchangeDialog()).activate();
				else if(guideKey != '\0' && event.getChar() == guideKey) UIManager.openGuideMenu();
				else if(eventsKey != '\0' && event.getChar() == eventsKey) UIManager.openEventsMenu();
			}
		}, instance);

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
		}, instance);
		StarLoader.registerListener(GUITopBarCreateEvent.class, new Listener<GUITopBarCreateEvent>() {
			@Override
			public void onEvent(GUITopBarCreateEvent event) {
				GUITopBar.ExpandedButton dropDownButton = event.getDropdownButtons().get(event.getDropdownButtons().size() - 1);
				dropDownButton.addExpandedButton("GUIDE", new GUICallback() {
					@Override
					public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
						if(mouseEvent.pressedLeftMouse()) {
							GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
							GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().deactivateAll();
							UIManager.openGuideMenu();
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
				dropDownButton.addExpandedButton("BUILD SECTOR", new GUICallback() {
					@Override
					public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
						if(mouseEvent.pressedLeftMouse()) UIManager.openBuildSectorMenu();
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
						if(mouseEvent.pressedLeftMouse()) (new ExchangeDialog()).activate();
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
				dropDownButton.addExpandedButton("EVENTS", new GUICallback() {
					@Override
					public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
						if(mouseEvent.pressedLeftMouse()) UIManager.openEventsMenu();
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
		}, instance);
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
		}, instance);
		StarLoader.registerListener(RegisterWorldDrawersEvent.class, new Listener<RegisterWorldDrawersEvent>() {
			@Override
			public void onEvent(RegisterWorldDrawersEvent event) {
				// event.getModDrawables().add(new BuildSectorHudDrawer());
			}
		}, instance);
		StarLoader.registerListener(SegmentControllerInstantiateEvent.class, new Listener<SegmentControllerInstantiateEvent>() {
			@Override
			public void onEvent(final SegmentControllerInstantiateEvent event) {
				new StarRunnable() {
					@Override
					public void run() {
						try {
							if(event.getController() == null || !event.getController().isFullyLoadedWithDock()) return;
							if(event.getController().getSector(new Vector3i()).x > 100000000 || event.getController().getSector(new Vector3i()).y > 100000000 || event.getController().getSector(new Vector3i()).z > 100000000) ClientCacheManager.updateClientCacheData();
							if(DataUtils.isBuildSector(event.getController().getSector(new Vector3i()))) {
								BuildSectorData sectorData = DataUtils.getSectorData(event.getController().getSector(new Vector3i()));
								if(sectorData != null) {
									if(event.getController().getFactionId() == FactionManager.PIRATES_ID && !BuildSectorUtils.getPlayersWithEnemySpawnPerms(sectorData).contains(event.getController().getSpawner())) DataUtils.deleteEnemies(sectorData, 0);
									ClientCacheManager.updateClientCacheData();
								}
							}
						} catch(Exception exception) {
							instance.logException("Encountered an exception while trying to check if a new SegmentController \"" + event.getController().getName() + "\" was in a build sector", exception);
						}
					}
				}.runLater(instance, 3);
			}
		}, instance);
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
				} catch(Exception exception) {
					instance.logException("Encountered an exception while trying to enforce permissions in a build sector", exception);
				}
			}
		}, instance);
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
				} catch(Exception exception) {
					instance.logException("Encountered an exception while trying to enforce permissions in a build sector", exception);
				}
			}
		}, instance);
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
					instance.logException("Encountered an exception while trying to enforce permissions in a build sector", exception);
				}
			}
		}, instance);
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
					instance.logException("Encountered an exception while trying to enforce permissions in a build sector", exception);
				}
			}
		}, instance);
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
					instance.logException("Encountered an exception while trying to enforce permissions in a build sector", exception);
				}
			}
		}, instance);
		StarLoader.registerListener(PlayerDeathEvent.class, new Listener<PlayerDeathEvent>() {
			@Override
			public void onEvent(PlayerDeathEvent event) {
				if(DataUtils.isPlayerInAnyBuildSector(event.getPlayer())) queueSpawnSwitch(event.getPlayer());
			}
		}, instance);
		StarLoader.registerListener(PlayerSpawnEvent.class, new Listener<PlayerSpawnEvent>() {
			@Override
			public void onEvent(final PlayerSpawnEvent event) {
				new StarRunnable() {
					@Override
					public void run() {
						try {
							if(!event.isServer() && !DataUtils.isPlayerInAnyBuildSector(event.getPlayer().getOwnerState())) PacketUtil.sendPacketToServer(new RequestClientCacheUpdatePacket());
						} catch(Exception exception) {
							instance.logException("Encountered an exception while trying to request a client cache update", exception);
						}
					}
				}.runLater(instance, 5);
				if(DataUtils.isPlayerInAnyBuildSector(event.getPlayer().getOwnerState())) queueSpawnSwitch(event.getPlayer().getOwnerState());
			}
		}, instance);
		StarLoader.registerListener(PlayerJoinWorldEvent.class, new Listener<PlayerJoinWorldEvent>() {
			@Override
			public void onEvent(final PlayerJoinWorldEvent event) {
				if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) {
					if(DataUtils.getBuildSector(event.getPlayerName()) == null) DataUtils.createNewBuildSector(event.getPlayerName());
					(new StarRunnable() {
						@Override
						public void run() {
							PlayerState playerState = GameCommon.getPlayerFromName(event.getPlayerName());
							if(playerState != null && playerState.isOnServer()) {
								PlayerData playerData = DataUtils.getPlayerData(playerState);
								Date date = new Date(playerData.lastDailyPrizeClaim);
								if(DateUtils.getAgeDays(date) >= 1.0f) {
									InventoryUtils.addItem(playerState.getPersonalInventory(), ElementManager.getItem("Bronze Bar").getId(), 2);
									playerData.lastDailyPrizeClaim = System.currentTimeMillis();
									PersistentObjectUtil.save(instance.getSkeleton());
									PlayerUtils.sendMessage(playerState, "You have been given 2 Bronze Bars for logging in. Thanks for playing!");
								}
							}
						}
					}).runLater(instance, 10000);
				}
			}
		}, instance);
		startRunners();
	}

	private static void startRunners() {
		if(GameCommon.isOnSinglePlayer() || GameCommon.isDedicatedServer()) {
			(new StarRunnable() {
				@Override
				public void run() {
					ClientCacheManager.updateClientCacheData();
				}
			}).runTimer(EdenCore.getInstance(), 10000);
			//Todo: Replace auto updates with ones that only fire when actually needed to cut down on packet spam
		}
	}

	private static void queueSpawnSwitch(final PlayerState playerState) {
		new StarRunnable() {
			@Override
			public void run() {
				if(!DataUtils.isPlayerInAnyBuildSector(playerState)) cancel();
				if(!playerState.hasSpawnWait) { // Wait until player has spawned, then warp them
					try {
						DataUtils.movePlayerFromBuildSector(playerState);
					} catch(Exception exception) {
						EdenCore.getInstance().logException("Encountered a severe exception while trying to move player \"" + playerState.getName() + "\" out of a build sector! Report this ASAP!", exception);
						playerState.setUseCreativeMode(false);
						if(!playerState.isAdmin()) playerState.setHasCreativeMode(false);
						PlayerUtils.sendMessage(playerState, "The server encountered a severe exception while trying to load you in and your" + " player state may be corrupted as a result. Report this to an admin ASAP!");
					}
					cancel();
				}
			}
		}.runTimer(EdenCore.getInstance(), 300);
	}
}

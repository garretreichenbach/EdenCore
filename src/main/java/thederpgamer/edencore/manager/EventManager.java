package thederpgamer.edencore.manager;

import api.common.GameClient;
import api.listener.Listener;
import api.listener.events.gui.GUITopBarCreateEvent;
import api.listener.events.gui.MainWindowTabAddEvent;
import api.mod.StarLoader;
import api.utils.gui.ModGUIHandler;
import org.schema.game.client.view.gui.newgui.GUITopBar;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIActivationHighlightCallback;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.buildsectordata.BuildSectorDataManager;
import thederpgamer.edencore.data.misc.ControlBindingData;
import thederpgamer.edencore.gui.controls.ControlBindingsScrollableList;
import thederpgamer.edencore.gui.exchangemenu.ExchangeDialog;

import java.util.Locale;
import java.util.Set;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class EventManager {

	public static final String[] disabledTabs = {"FLEETS", "SHOP", "REPAIRS", "TRADE", "SET PRICES"};
	
	public static final short[] disabledBlocks = {
			347, // Shop Module
			291, // Faction Module
			667, // Shipyard Computer
			683, // Race Gate Controller
			542 // Warp Gate Computer
	};

	public static void initialize(EdenCore instance) {
		StarLoader.registerListener(MainWindowTabAddEvent.class, new Listener<MainWindowTabAddEvent>() {
			@Override
			public void onEvent(MainWindowTabAddEvent event) {
				if(event.getTitleAsString().toUpperCase(Locale.ENGLISH).equals(Lng.str("MOUSE"))) {
					event.getPane().addNewTextBox(300);
					ControlBindingsScrollableList list = new ControlBindingsScrollableList(event.getPane().getState(), event.getPane().getContent(1), ControlBindingData.ControlType.MOUSE);
					list.onInit();
					event.getPane().getContent(1).attach(list);
				} else if(event.getTitleAsString().toUpperCase(Locale.ENGLISH).equals(Lng.str("KEYBOARD"))) {
					event.getPane().setTabName(Lng.str("KEYBOARD")); //Fix for the tab name being lowercase for some reason
					event.getPane().addNewTextBox(300);
					ControlBindingsScrollableList list = new ControlBindingsScrollableList(event.getPane().getState(), event.getPane().getContent(1), ControlBindingData.ControlType.KEYBOARD);
					list.onInit();
					event.getPane().getContent(1).attach(list);
				} else if(event.getTitleAsString().toUpperCase(Locale.ENGLISH).equals(Lng.str("JOYSTICK/PAD"))) {
					event.getPane().addNewTextBox(300);
					ControlBindingsScrollableList list = new ControlBindingsScrollableList(event.getPane().getState(), event.getPane().getContent(1), ControlBindingData.ControlType.JOYSTICK_PAD);
					list.onInit();
					event.getPane().getContent(1).attach(list);
				}
				
				if(BuildSectorDataManager.getInstance().isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
					for(String disabledTab : disabledTabs) {
						if(event.getTitleAsString().equals(Lng.str(disabledTab))) {
							event.setCanceled(true);
							event.getWindow().cleanUp();
						}
					}
				}
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
							ModGUIHandler.getGUIControlManager("glossarPanel").setActive(true);
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
						if(mouseEvent.pressedLeftMouse()) {
							//Todo
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
			}
		}, instance);
		/*
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
		
		 */
	}
}

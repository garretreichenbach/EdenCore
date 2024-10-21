package thederpgamer.edencore.manager;

import api.common.GameClient;
import api.listener.Listener;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceActivateEvent;
import api.listener.events.draw.RegisterWorldDrawersEvent;
import api.listener.events.gui.GUIElementInstansiateEvent;
import api.listener.events.gui.GUITopBarCreateEvent;
import api.listener.events.gui.MainWindowTabAddEvent;
import api.listener.events.input.KeyPressEvent;
import api.listener.events.world.SimulationJobExecuteEvent;
import api.mod.StarLoader;
import api.utils.gui.ModGUIHandler;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.view.gui.PlayerPanel;
import org.schema.game.client.view.gui.catalog.newcatalog.CatalogOptionsButtonPanel;
import org.schema.game.client.view.gui.catalog.newcatalog.CatalogPanelNew;
import org.schema.game.client.view.gui.catalog.newcatalog.CatalogScrollableListNew;
import org.schema.game.client.view.gui.newgui.GUITopBar;
import org.schema.game.server.data.simulation.jobs.SpawnPiratePatrolPartyJob;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIActivationHighlightCallback;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIResizableGrabbableWindow;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUITabbedContent;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.buildsectordata.BuildSectorDataManager;
import thederpgamer.edencore.data.misc.ControlBindingData;
import thederpgamer.edencore.drawer.BuildSectorHudDrawer;
import thederpgamer.edencore.gui.buildsectormenu.BuildSectorDialog;
import thederpgamer.edencore.gui.controls.ControlBindingsScrollableList;
import thederpgamer.edencore.gui.elements.ECCatalogScrollableListNew;
import thederpgamer.edencore.gui.exchangemenu.ExchangeDialog;
import thederpgamer.edencore.utils.ClassUtils;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class EventManager {

	public static final String[] disabledTabs = {"FLEETS", "SHOP", "REPAIRS", "TRADE", "SET PRICES"};
	public static final String[] disabledWindows = {"Fleet", "ShopPanelNew", "FactionPanelNew"};
	public static final short[] disabledBlocks = {
			347, //Shop Module
			291, //Faction Module
			667, //Shipyard Computer
			683, //Race Gate Controller
			542 //Warp Gate Computer
	};

	public static void initialize(EdenCore instance) {
		StarLoader.registerListener(MainWindowTabAddEvent.class, new Listener<MainWindowTabAddEvent>() {
			@Override
			public void onEvent(MainWindowTabAddEvent event) {
				if(event.getTitleAsString().equals(Lng.str("Keyboard"))) event.getPane().setTabName(Lng.str("KEYBOARD")); //Fix for the tab name being lowercase for some reason
				else if(event.getTitleAsString().equals(Lng.str("CONTROLS")) && event.getWindow().getTabs().size() == 2) { //Make sure we aren't adding a duplicate tab
					GUIContentPane modControlsPane = event.getWindow().addTab(Lng.str("MOD CONTROLS")); //Todo: StarLoader will support mod controls and settings in these menus next update, so we can remove this later
					GUITabbedContent tabbedContent = new GUITabbedContent(modControlsPane.getState(), modControlsPane.getContent(0));
					tabbedContent.activationInterface = event.getWindow().activeInterface;
					tabbedContent.onInit();
					tabbedContent.setPos(0, 2, 0);
					modControlsPane.getContent(0).attach(tabbedContent);

					GUIContentPane edenCorePane = tabbedContent.addTab("EDENCORE");
					ControlBindingsScrollableList list = new ControlBindingsScrollableList(edenCorePane.getState(), edenCorePane.getContent(0), ControlBindingData.ControlType.KEYBOARD);
					list.onInit();
					edenCorePane.getContent(0).attach(list);
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
				/* Todo: Finish Banking Menu
				dropDownButton.addExpandedButton("BANKING", new GUICallback() {
					@Override
					public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
						if(mouseEvent.pressedLeftMouse()) {

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
							GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
							GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().deactivateAll();
							(new BuildSectorDialog()).activate();
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
							(new ExchangeDialog()).activate();
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
		}, instance);

		StarLoader.registerListener(KeyPressEvent.class, new Listener<KeyPressEvent>() {
			@Override
			public void onEvent(KeyPressEvent event) {
				Set<ControlBindingData> bindings = ControlBindingData.getBindings();
				for(ControlBindingData binding : bindings) {
					if(event.getKey() == binding.getBinding()) {
						switch(binding.getName()) {
							case "Guide Menu":
								GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
								GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().deactivateAll();
								ModGUIHandler.getGUIControlManager("glossarPanel").setActive(true);
								return;
//							case "Banking Menu": Todo: Finish Banking Menu
//								GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
//								GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().deactivateAll();
//								(new BankingDialog()).activate();
//								return;
							case "Exchange Menu":
								GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
								GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().deactivateAll();
								(new ExchangeDialog()).activate();
								return;
							case "Build Sector Menu":
								GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
								GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().deactivateAll();
								(new BuildSectorDialog()).activate();
								return;
						}
					}
				}
			}
		}, instance);

		StarLoader.registerListener(GUIElementInstansiateEvent.class, new Listener<GUIElementInstansiateEvent>() {
			@Override
			public void onEvent(GUIElementInstansiateEvent event) {
				if(event.getGUIElement() instanceof CatalogPanelNew) {
					try {
						PlayerPanel panel = GameClient.getClientState().getWorldDrawer().getGuiDrawer().getPlayerPanel();
						Field catalogField = panel.getClass().getDeclaredField("catalogPanelNew");
						catalogField.setAccessible(true);
						CatalogPanelNew catalogPanel = (CatalogPanelNew) catalogField.get(panel);
						if(catalogPanel != null) {
							catalogPanel.cleanUp();
							catalogPanel = new CatalogPanelNew(event.getInputState()) {
								@Override
								public void createAvailableCatalogPane() {
									try {
										Field availListField = CatalogPanelNew.class.getDeclaredField("availList");
										Field availableTabField = CatalogPanelNew.class.getDeclaredField("availableTab");

										availListField.setAccessible(true);
										availableTabField.setAccessible(true);

										CatalogScrollableListNew availList = (CatalogScrollableListNew) availListField.get(this);

										Field modeField = availList.getClass().getDeclaredField("mode");
										modeField.setAccessible(true);
										int mode = modeField.getInt(availList);

										Field showPriceField = availList.getClass().getDeclaredField("showPrice");
										showPriceField.setAccessible(true);
										boolean showPrice = showPriceField.getBoolean(availList);

										Field selectSingleField = availList.getClass().getDeclaredField("selectSingle");
										selectSingleField.setAccessible(true);
										boolean selectSingle = selectSingleField.getBoolean(availList);

										if(!(availList instanceof ECCatalogScrollableListNew)) {
											GUIContentPane availableTab = (GUIContentPane) availableTabField.get(this);
											if(availList != null) availList.cleanUp();
											CatalogOptionsButtonPanel c = new CatalogOptionsButtonPanel(getState(), this);
											c.onInit();
											availableTab.setContent(0, c);
											if(!CatalogOptionsButtonPanel.areMultiplayerButtonVisible()) {
												availableTab.setTextBoxHeightLast(58);
												availableTab.addNewTextBox(10);
											} else {
												availableTab.setTextBoxHeightLast(82);
												availableTab.addNewTextBox(10);
											}

											availList = new ECCatalogScrollableListNew(getState(), availableTab.getContent(1), mode, showPrice, selectSingle);
											availList.onInit();
											availableTab.getContent(1).attach(availList);
										}
									} catch(Exception exception) {
										instance.logException("Failed to create Available Catalog Pane", exception);
									}
								}
							};
							catalogPanel.onInit();
							catalogField.set(panel, catalogPanel);
						}
					} catch(Exception exception) {
						instance.logException("Failed to replace Catalog Panel", exception);
					}
				} else if(event.getGUIElement() instanceof GUIResizableGrabbableWindow) {
					GUIResizableGrabbableWindow window = (GUIResizableGrabbableWindow) event.getGUIElement();
					if(BuildSectorDataManager.getInstance().isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
						for(String windowID : disabledWindows) {
							if(window.getWindowId().equals(windowID) || window.getWindowId().equals(Lng.str(windowID))) {
								window.cleanUp();
								event.setCanceled(true);
							}
						}
					}
				}
			}
		}, instance);

		StarLoader.registerListener(SimulationJobExecuteEvent.class, new Listener<SimulationJobExecuteEvent>() {
			@Override
			public void onEvent(SimulationJobExecuteEvent event) {
				if(event.getSimulationJob() instanceof SpawnPiratePatrolPartyJob) {
					SpawnPiratePatrolPartyJob job = (SpawnPiratePatrolPartyJob) event.getSimulationJob();
					Vector3i from = (Vector3i) ClassUtils.getField(job, "from");
					Vector3i to = (Vector3i) ClassUtils.getField(job, "to");
					if(BuildSectorDataManager.getInstance().isBuildSector(from) || BuildSectorDataManager.getInstance().isBuildSector(to)) event.setCanceled(true);
				} else if(BuildSectorDataManager.getInstance().isBuildSector(event.getStartLocation())) event.setCanceled(true);
			}
		}, instance);

		StarLoader.registerListener(MainWindowTabAddEvent.class, new Listener<MainWindowTabAddEvent>() {
			@Override
			public void onEvent(MainWindowTabAddEvent event) {
				if(BuildSectorDataManager.getInstance().isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
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
				event.getModDrawables().add(new BuildSectorHudDrawer());
			}
		}, instance);

		StarLoader.registerListener(SegmentPieceActivateEvent.class, new Listener<SegmentPieceActivateEvent>() {
			@Override
			public void onEvent(SegmentPieceActivateEvent event) {
				try {
					if(BuildSectorDataManager.getInstance().isBuildSector(event.getSegmentPiece().getSegmentController().getSector(new Vector3i()))) {
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
					if(BuildSectorDataManager.getInstance().isPlayerInAnyBuildSector(event.getPlayer())) {
						for(short id : disabledBlocks) {
							if(event.getSegmentPiece().getType() == id && !event.getPlayer().isAdmin()) {
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
	}
}

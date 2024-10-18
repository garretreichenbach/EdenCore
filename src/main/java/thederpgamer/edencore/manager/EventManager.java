package thederpgamer.edencore.manager;

import api.common.GameClient;
import api.listener.Listener;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceActivateEvent;
import api.listener.events.draw.RegisterWorldDrawersEvent;
import api.listener.events.gui.GUITopBarCreateEvent;
import api.listener.events.gui.MainWindowTabAddEvent;
import api.listener.events.input.KeyPressEvent;
import api.listener.events.world.SimulationJobExecuteEvent;
import api.mod.StarLoader;
import api.utils.gui.ModGUIHandler;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.view.gui.newgui.GUITopBar;
import org.schema.game.server.data.simulation.jobs.SpawnPiratePatrolPartyJob;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIActivationHighlightCallback;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.input.InputState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.buildsectordata.BuildSectorDataManager;
import thederpgamer.edencore.data.misc.ControlBindingData;
import thederpgamer.edencore.drawer.BuildSectorHudDrawer;
import thederpgamer.edencore.gui.bankingmenu.BankingDialog;
import thederpgamer.edencore.gui.buildsectormenu.BuildSectorDialog;
import thederpgamer.edencore.gui.controls.ControlBindingsScrollableList;
import thederpgamer.edencore.gui.exchangemenu.ExchangeDialog;
import thederpgamer.edencore.utils.ClassUtils;

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

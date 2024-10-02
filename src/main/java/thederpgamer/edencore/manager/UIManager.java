package thederpgamer.edencore.manager;

import api.common.GameClient;
import api.utils.gui.ModGUIHandler;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.gui.buildsectormenu.BuildSectorMenuControlManager;
import thederpgamer.edencore.gui.eventsmenu.EventsMenuControlManager;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class UIManager {

	private static BuildSectorMenuControlManager buildSectorMenuControlManager;
	private static EventsMenuControlManager eventsMenuControlManager;

	public static void openBuildSectorMenu() {
		if(buildSectorMenuControlManager == null) {
			buildSectorMenuControlManager = new BuildSectorMenuControlManager();
			ModGUIHandler.registerNewControlManager(EdenCore.getInstance().getSkeleton(), buildSectorMenuControlManager);
		}
		if(!GameClient.getClientState().getController().isChatActive() && GameClient.getClientState().getController().getPlayerInputs().isEmpty()) {
			GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
			GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().deactivateAll();
			buildSectorMenuControlManager.setActive(true);
		}
	}

	public static void openGuideMenu() {
		try {
			ModGUIHandler.getGUIControlManager("glossarPanel").setActive(true);
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void openEventsMenu() {
		if(eventsMenuControlManager == null) {
			eventsMenuControlManager = new EventsMenuControlManager();
			ModGUIHandler.registerNewControlManager(EdenCore.getInstance().getSkeleton(), eventsMenuControlManager);
		}
		if(!GameClient.getClientState().getController().isChatActive() && GameClient.getClientState().getController().getPlayerInputs().isEmpty()) {
			GameClient.getClientState().getController().queueUIAudio("0022_menu_ui - enter");
			GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().deactivateAll();
			eventsMenuControlManager.setActive(true);
		}
	}
}

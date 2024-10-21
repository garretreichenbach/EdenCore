package thederpgamer.edencore.manager;

import api.common.GameServer;
import api.utils.game.PlayerUtils;
import api.utils.game.inventory.InventoryUtils;
import org.schema.game.common.data.player.AbstractOwnerState;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.inventory.Inventory;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.element.ElementManager;

import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class ThreadManager {

	private static final int THREAD_COUNT = 5;
	private static ThreadPoolExecutor executorService;

	public static void initialize(EdenCore instance) {
		executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_COUNT);
		executorService.submit((Runnable) () -> {
			while(true) {
				try {
					String tip = ConfigManager.getRandomTip();
					for(PlayerState playerState : GameServer.getServerState().getPlayerStatesByName().values()) PlayerUtils.sendMessage(playerState, tip);
					Thread.sleep(ConfigManager.getMainConfig().getLong("tip_interval"));
				} catch(InterruptedException exception) {
					instance.logException("An error occurred while sending tips to players", exception);
				}
			}
		});
	}

	public static void addLoginTimer(String playerName) {
		executorService.submit(() -> {
			try {
				Thread.sleep(ConfigManager.getMainConfig().getLong("player_login_reward_timer"));
				PlayerState playerState = GameServer.getServerState().getPlayerFromName(playerName);
				if(playerState != null) {
					Inventory inventory = playerState.getInventory();
					if(inventory.isInfinite()) { //They are in creative mode, we need to get their survival inventory specifically
						Field infiniteField = AbstractOwnerState.class.getDeclaredField("inventory");
						infiniteField.setAccessible(true);
						inventory = (Inventory) infiniteField.get(playerState);
					}
					int prizeBarCount = 1; //Todo: Donators get extra bars
					InventoryUtils.addItem(inventory, ElementManager.getItem("Bronze Bar").getId(), prizeBarCount);
					PlayerUtils.sendMessage(playerState, "You have received " + prizeBarCount + " Bronze Bars for logging in today! Thanks for playing!");
				}
			} catch(Exception exception) {
				EdenCore.getInstance().logException("An error occurred while adding login timer for player \"" + playerName + "\"", exception);
			}
		});
	}
}

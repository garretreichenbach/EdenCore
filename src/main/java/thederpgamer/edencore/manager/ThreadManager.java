package thederpgamer.edencore.manager;

import api.common.GameCommon;
import api.common.GameServer;
import api.utils.game.PlayerUtils;
import api.utils.game.inventory.InventoryUtils;
import org.schema.game.common.data.player.AbstractOwnerState;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.inventory.Inventory;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.element.ElementManager;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class ThreadManager {

	private static final long TASK_INTERVAL = 10000;
	private static final ConcurrentLinkedQueue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();

	public static void initialize(final EdenCore instance) {
		final long tipInterval = ConfigManager.getMainConfig().getLong("tip_interval");
		Thread loginTimerThread = new Thread("EdenCore_Login_Timer_Thread") {
			@Override
			public void run() {
				while(true) {
					try {
						sleep(tipInterval);
						String tip = ConfigManager.getRandomTip();
						for(PlayerState playerState : GameServer.getServerState().getPlayerStatesByName().values()) PlayerUtils.sendMessage(playerState, tip);
					} catch(InterruptedException exception) {
						instance.logException("An error occurred while checking player login timers", exception);
					}
				}
			}
		};
		loginTimerThread.start();

		Thread taskRunnerThread = new Thread("EdenCore_Task_Runner_Thread") {
			@Override
			public void run() {
				while(true) {
					try {
						if(!taskQueue.isEmpty()) taskQueue.poll().run();
						else sleep(TASK_INTERVAL);
					} catch(InterruptedException exception) {
						instance.logException("An error occurred while running task queue", exception);
					}
				}
			}
		};
		taskRunnerThread.start();
	}

	public static void addLoginTimer(final String playerName) {
		final long rewardTimer = ConfigManager.getMainConfig().getLong("player_login_reward_timer");
		taskQueue.add(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(rewardTimer);
					PlayerState playerState = GameCommon.getPlayerFromName(playerName);
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
					EdenCore.getInstance().logException("An error occurred while adding login timer for player", exception);
				}
			}
		});
	}
}

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

import static java.lang.Thread.sleep;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class ThreadManager {

	private static final long TASK_INTERVAL = 1000;
	private static final ConcurrentLinkedQueue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();

	public static void initialize(final EdenCore instance) {
		Thread taskRunnerThread = new Thread("EdenCore_Task_Runner_Thread") {
			@Override
			public void run() {
				while(true) {
					try {
						sleep(TASK_INTERVAL);
						if(!taskQueue.isEmpty()) (new Thread(taskQueue.poll())).start();
					} catch(InterruptedException exception) {
						instance.logException("An error occurred while running task queue", exception);
						return;
					}
				}
			}
		};
//		taskRunnerThread.start();
	}

	public static void addTask(Runnable task) {
		taskQueue.add(task);
	}

	public static void addLoginTimer(final String playerName) {
		final long rewardTimer = ConfigManager.getMainConfig().getLong("player_login_reward_timer");
		taskQueue.add(new Runnable() {
			@Override
			public void run() {
				try {
					sleep(rewardTimer);
					PlayerState playerState = GameCommon.getPlayerFromName(playerName);
					if(playerState != null && playerState.spawnedOnce) {
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

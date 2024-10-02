package thederpgamer.edencore.manager;

import api.common.GameServer;
import api.utils.StarRunnable;
import api.utils.game.PlayerUtils;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class TipManager {

	private static final String[] tips = {
			"The guide can be accessed via the /guide command or via the GUIDE button under the PLAYER tab in the top right."
	};

	public static void initialize() {
		(new StarRunnable() {
			long last = System.currentTimeMillis() - 1800000L + 10000L;
			public void run() {
				if(last + 1800000L < System.currentTimeMillis()) {
					last = System.currentTimeMillis();
					String randomTip = getRandomTip();
					for(PlayerState playerState : GameServer.getServerState().getPlayerStatesByName().values()) PlayerUtils.sendMessage(playerState, randomTip);
				}
			}
		}).runTimer(EdenCore.getInstance(), 10L);
	}

	private static String getRandomTip() {
		return tips[(int) (Math.random() * tips.length)];
	}
}

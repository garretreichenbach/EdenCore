package thederpgamer.edencore.manager;

import api.common.GameCommon;
import api.utils.gui.ModGUIHandler;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerState;
import org.schema.schine.network.objects.Sendable;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.utils.EntityUtils;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class PlayerActionManager {

	public static final int OPEN_GUIDE = 0;
	public static final int WARP_INTO_ENTITY = 1;
	public static final int SET_CREDITS = 2;

	public static void processAction(int type, String[] args) {
		try {
			switch(type) {
				case OPEN_GUIDE:
					ModGUIHandler.getGUIControlManager("glossarPanel").setActive(true);
					break;
				case WARP_INTO_ENTITY:
					int entityId = Integer.parseInt(args[0]);
					Sendable sendable = GameCommon.getGameObject(entityId);
					if(sendable instanceof SegmentController) EntityUtils.warpPlayerIntoEntity((SegmentController) sendable);
					break;
				case SET_CREDITS:
					PlayerState playerState = GameCommon.getPlayerFromName(args[0]);
					long credits = Long.parseLong(args[1]);
					if(playerState != null) playerState.setCredits(credits);
					break;
			}
		} catch(Exception exception) {
			EdenCore.getInstance().logException("An error occurred while processing player action", exception);
		}
	}
}

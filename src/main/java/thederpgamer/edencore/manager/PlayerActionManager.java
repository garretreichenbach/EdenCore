package thederpgamer.edencore.manager;

import api.common.GameClient;
import api.common.GameCommon;
import api.utils.gui.ModGUIHandler;
import org.schema.game.common.controller.SegmentController;
import org.schema.schine.network.objects.Sendable;
import thederpgamer.edencore.utils.EntityUtils;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class PlayerActionManager {
	
	public static final int OPEN_GUIDE = 0;
	public static final int WARP_INTO_ENTITY = 1;

	public static void processAction(int type, String[] args) {
		switch(type) {
			case OPEN_GUIDE:
				ModGUIHandler.getGUIControlManager("glossarPanel").setActive(true);
				break;
			case WARP_INTO_ENTITY:
				int entityId = Integer.parseInt(args[0]);
				Sendable sendable = GameCommon.getGameObject(entityId);
				if(sendable instanceof SegmentController) EntityUtils.warpPlayerIntoEntity(GameClient.getClientPlayerState(), (SegmentController) sendable);
				break;
		}
	}
}

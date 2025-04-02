package thederpgamer.edencore.manager;

import api.common.GameCommon;
import api.common.GameServer;
import api.utils.game.PlayerUtils;
import api.utils.gui.ModGUIHandler;
import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.server.controller.SectorSwitch;
import org.schema.schine.network.objects.Sendable;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.buildsectordata.BuildSectorData;
import thederpgamer.edencore.data.buildsectordata.BuildSectorDataManager;
import thederpgamer.edencore.data.playerdata.PlayerData;
import thederpgamer.edencore.data.playerdata.PlayerDataManager;
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
	public static final int ENTER_BUILD_SECTOR = 3;
	public static final int LEAVE_BUILD_SECTOR = 4;

	public static void processAction(int type, String[] args) {
		try {
			PlayerState playerState;
			PlayerData playerData;
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
					playerState = GameCommon.getPlayerFromName(args[0]);
					long credits = Long.parseLong(args[1]);
					if(playerState != null) playerState.setCredits(credits);
					break;
				case ENTER_BUILD_SECTOR:
					playerState = GameCommon.getPlayerFromName(args[0]);
					if(playerState.getFirstControlledTransformableWOExc() instanceof SegmentController) {
						PlayerUtils.sendMessage(playerState, "You cannot enter a build sector while piloting a ship. Please leave the ship first.");
						return;
					}
					playerData = PlayerDataManager.getInstance(playerState.isOnServer()).getFromName(playerState.getName(), playerState.isOnServer());
					BuildSectorData data = BuildSectorDataManager.getInstance(playerState.isOnServer()).getFromUUID(args[1], playerState.isOnServer());
					playerData.setLastRealSector(playerState.getCurrentSector());
					Transform lastRealTransform = new Transform();
					lastRealTransform.setIdentity();
					if(playerState.getFirstControlledTransformableWOExc() instanceof SegmentController) lastRealTransform.origin.set(playerState.getFirstControlledTransformableWOExc().getWorldTransform().origin);
					else lastRealTransform.origin.set(playerState.getBuildModePosition().getWorldTransform().origin);
					playerData.setLastRealTransform(lastRealTransform);
					Vector3i sector = data.getSector();
					GameServer.getServerState().getController().queueSectorSwitch(playerState.getFirstControlledTransformableWOExc(), sector, SectorSwitch.TRANS_JUMP, false, true, true);
					playerState.setHasCreativeMode(true);
					playerState.setUseCreativeMode(true);
					break;
				case LEAVE_BUILD_SECTOR:
					playerState = GameCommon.getPlayerFromName(args[0]);
					if(playerState.getFirstControlledTransformableWOExc() instanceof SegmentController) {
						PlayerUtils.sendMessage(playerState, "You cannot exit a build sector while piloting a ship. Please leave the ship first.");
						return;
					}
					playerData = PlayerDataManager.getInstance(playerState.isOnServer()).getFromName(playerState.getName(), playerState.isOnServer());
					Vector3i lastRealSector = playerData.getLastRealSector();
					Transform lastRealTransform1 = playerData.getLastRealTransform();
					playerState.setHasCreativeMode(false);
					playerState.setUseCreativeMode(false);
					
					GameServer.getServerState().getController().queueSectorSwitch(playerState.getFirstControlledTransformableWOExc(), lastRealSector, SectorSwitch.TRANS_JUMP, false, true, true);
					playerState.getAssingedPlayerCharacter().warpTransformable(lastRealTransform1, true, false, null);
					break;
			}
		} catch(Exception exception) {
			EdenCore.getInstance().logException("An error occurred while processing player action", exception);
		}
	}
}

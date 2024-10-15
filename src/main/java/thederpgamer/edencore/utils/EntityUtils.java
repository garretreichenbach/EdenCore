package thederpgamer.edencore.utils;

import api.common.GameClient;
import api.common.GameCommon;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.data.PlayerControllable;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.controller.SpaceStation;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;

import javax.vecmath.Vector3f;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/20/2021]
 */
public class EntityUtils {
	public static void warpPlayerIntoEntity(SegmentController entity) {
		SegmentPiece toEnter = null;
		if(entity.getType() == SimpleTransformableSendableObject.EntityType.SHIP) toEnter = entity.getSegmentBuffer().getPointUnsave(Ship.core);
		else if(entity.getType() == SimpleTransformableSendableObject.EntityType.SPACE_STATION) toEnter = getAvailableBuildBlock(entity);
		if(toEnter != null) {
			if(GameCommon.isOnSinglePlayer() || GameCommon.isClientConnectedToServer()) {
				GameClient.getClientPlayerState().getControllerState().forcePlayerOutOfSegmentControllers();
				GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().getInShipControlManager().setEntered(toEnter);
				GameClient.getClientState().getController().requestControlChange(GameClient.getClientPlayerState().getAssingedPlayerCharacter(), (PlayerControllable) entity, new Vector3i(), toEnter.getAbsolutePos(new Vector3i()), true);
			}
		}
	}

	public static SegmentPiece getAvailableBuildBlock(SegmentController segmentController) {
		ManagerContainer<?> managerContainer = null;
		if(segmentController.getType() == SimpleTransformableSendableObject.EntityType.SHIP) managerContainer = ((Ship) segmentController).getManagerContainer();
		else if(segmentController.getType() == SimpleTransformableSendableObject.EntityType.SPACE_STATION) managerContainer = ((SpaceStation) segmentController).getManagerContainer();
		if(managerContainer != null && !managerContainer.getBuildBlocks().isEmpty()) return segmentController.getSegmentBuffer().getPointUnsave(managerContainer.getBuildBlocks().toLongArray()[0]);
		else return null;
	}

	public static float distance(Vector3f vectorA, Vector3f vectorB) {
		return (new Vector3f(Math.abs(vectorA.x - vectorB.x), Math.abs(vectorA.y - vectorB.y), Math.abs(vectorA.z - vectorB.z))).length();
	}
	
	/*
	public static void spawnEntry(PlayerState owner, BlueprintEntry entry, String spawnName, int factionId, Vector3i buildSector) {
		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(owner.getFirstControlledTransformableWOExc().getWorldTransform().origin);
		Vector3f forward = GlUtil.getForwardVector(new Vector3f(), transform);
		Vector3f size = entry.getBb().calculateHalfSize(new Vector3f());
		size.scale(0.5f);
		forward.scaleAdd(1.15f, size);
		transform.origin.set(forward);
		try {
			SegmentControllerOutline<?> outline = BluePrintController.active.loadBluePrint(GameServerState.instance, entry.getName(), spawnName, transform, -1, factionId, owner.getCurrentSector(), owner.getName(), PlayerState.buffer, null, false, new ChildStats(false));
			SegmentController entity = outline.spawn(owner.getCurrentSector(), false, new ChildStats(false), new SegmentControllerSpawnCallbackDirect(GameServer.getServerState(), owner.getCurrentSector()) {
				@Override
				public void onNoDocker() {
				}
			});
			BuildSectorUtils.recordSpawn(entity, buildSector);
			PlayerUtils.sendMessage(owner, "Successfully spawned entity \"" + entity.getName() + "\".");
		} catch(EntityNotFountException | IOException | EntityAlreadyExistsException | StateParameterNotFoundException exception) {
			exception.printStackTrace();
		}
	}

	public static void spawnEntryOnDock(PlayerState owner, BlueprintEntry entry, String spawnName, int factionId) {
		SegmentPiece spawnOnBlock = null;
		try {
			spawnOnBlock = ServerUtils.getBlockLookingAt(GameServer.getServerState(), owner);
		} catch(PlayerNotFountException | PlayerControlledTransformableNotFound | IOException ignored) {
		}
		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(owner.getFirstControlledTransformableWOExc().getWorldTransform().origin);
		Vector3f forward = GlUtil.getForwardVector(new Vector3f(), transform);
		Vector3f size = entry.getBb().calculateHalfSize(new Vector3f());
		size.scale(0.5f);
		forward.scaleAdd(1.15f, size);
		transform.origin.set(forward);
		try {
			SegmentControllerOutline<?> outline = BluePrintController.active.loadBluePrint(GameServerState.instance, entry.getName(), spawnName, transform, -1, factionId, owner.getCurrentSector(), owner.getName(), PlayerState.buffer, spawnOnBlock, false, new ChildStats(false));
			SegmentController entity = outline.spawn(owner.getCurrentSector(), false, new ChildStats(false), new SegmentControllerSpawnCallbackDirect(GameServer.getServerState(), owner.getCurrentSector()) {
				@Override
				public void onNoDocker() {
				}
			});
			PlayerUtils.sendMessage(owner, "Successfully spawned entity \"" + entity.getName() + "\".");
		} catch(EntityNotFountException | IOException | EntityAlreadyExistsException | StateParameterNotFoundException exception) {
			exception.printStackTrace();
		}
	}

	public static void spawnEnemy(PlayerState owner, BlueprintEntry entry, String spawnName, Vector3i sector) {
		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(owner.getFirstControlledTransformableWOExc().getWorldTransform().origin);
		Vector3f forward = GlUtil.getForwardVector(new Vector3f(), transform);
		Vector3f size = entry.getBb().calculateHalfSize(new Vector3f());
		size.scale(0.5f);
		forward.scaleAdd(1.15f, size);
		transform.origin.set(forward);
		try {
			SegmentControllerOutline<?> outline = BluePrintController.active.loadBluePrint(GameServerState.instance, entry.getName(), spawnName, transform, -1, FactionManager.PIRATES_ID, owner.getCurrentSector(), owner.getName(), PlayerState.buffer, null, false, new ChildStats(false));
			SegmentController entity = outline.spawn(owner.getCurrentSector(), false, new ChildStats(false), new SegmentControllerSpawnCallbackDirect(GameServer.getServerState(), owner.getCurrentSector()) {
				@Override
				public void onNoDocker() {
				}
			});
			BuildSectorUtils.recordSpawn(entity, sector);
			BuildSectorUtils.toggleAI(entity, true);
			PlayerUtils.sendMessage(owner, "Successfully spawned entity \"" + entity.getName() + "\".");
		} catch(EntityNotFountException | IOException | EntityAlreadyExistsException | StateParameterNotFoundException exception) {
			exception.printStackTrace();
		}
	}

	 */
}

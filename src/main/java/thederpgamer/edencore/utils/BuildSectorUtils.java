package thederpgamer.edencore.utils;

import api.common.GameCommon;
import api.common.GameServer;
import api.mod.config.PersistentObjectUtil;
import api.utils.game.PlayerUtils;
import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.player.PlayerControlledTransformableNotFound;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.catalog.CatalogPermission;
import org.schema.game.common.data.player.faction.FactionManager;
import org.schema.game.common.data.world.Sector;
import org.schema.game.server.controller.BluePrintController;
import org.schema.game.server.controller.EntityAlreadyExistsException;
import org.schema.game.server.controller.EntityNotFountException;
import org.schema.game.server.data.GameServerState;
import org.schema.game.server.data.PlayerNotFountException;
import org.schema.game.server.data.blueprint.ChildStats;
import org.schema.game.server.data.blueprint.SegmentControllerOutline;
import org.schema.game.server.data.blueprint.SegmentControllerSpawnCallbackDirect;
import org.schema.game.server.data.blueprintnw.BlueprintEntry;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.core.settings.StateParameterNotFoundException;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.entity.SegmentControllerRecord;
import thederpgamer.edencore.data.other.BuildSectorData;

import javax.vecmath.Vector3f;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/27/2021]
 */
public class BuildSectorUtils {
	public static ArrayList<String> getPlayersWithEnemySpawnPerms(BuildSectorData sectorData) {
		ArrayList<String> players = new ArrayList<>();
		for(String playerName : sectorData.getAllowedPlayersByName()) {
			if(sectorData.hasPermission(playerName, "SPAWN_ENEMIES")) players.add(playerName);
		}
		return players;
	}

	public static BlueprintEntry getEntry(PlayerState sender, String entryName) {
		for(CatalogPermission permission : sender.getCatalog().getAvailableCatalog()) {
			try {
				BlueprintEntry blueprintEntry = BluePrintController.active.getBlueprint(permission.getUid());
				if(blueprintEntry.getName().toLowerCase().contains(entryName.toLowerCase())) return blueprintEntry;
			} catch(EntityNotFountException ignored) {
			}
		}
		return null;
	}

	public static void spawnEntry(PlayerState sender, BlueprintEntry entry, boolean aiEnabled, Vector3i buildSector) {
		SegmentPiece spawnOnBlock = null;
		try {
			spawnOnBlock = ServerUtils.getBlockLookingAt(GameServer.getServerState(), sender);
		} catch(PlayerNotFountException | PlayerControlledTransformableNotFound | IOException ignored) {
		}
		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(sender.getFirstControlledTransformableWOExc().getWorldTransform().origin);
		Vector3f forward = GlUtil.getForwardVector(new Vector3f(), transform);
		Vector3f size = entry.getBb().calculateHalfSize(new Vector3f());
		size.scale(0.5f);
		forward.scaleAdd(1.15f, size);
		transform.origin.set(forward);
		try {
			SegmentControllerOutline<?> outline = BluePrintController.active.loadBluePrint(GameServerState.instance, entry.getName(), entry.getName(), transform, -1, sender.getFactionId(), buildSector, sender.getName(), PlayerState.buffer, spawnOnBlock, false, new ChildStats(false));
			SegmentController entity = outline.spawn(sender.getCurrentSector(), false, new ChildStats(false), new SegmentControllerSpawnCallbackDirect(GameServer.getServerState(), buildSector) {
				@Override
				public void onNoDocker() {
				}
			});
			recordSpawn(entity, buildSector);
			toggleAI(entity, aiEnabled);
		} catch(EntityNotFountException | IOException | EntityAlreadyExistsException | StateParameterNotFoundException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Security measure to record the spawn of a ship in a build sector that can be checked later to prevent exploits.
	 * @param entity the entity that was spawned.
	 */
	public static void recordSpawn(SegmentController entity, Vector3i buildSector) {
		PersistentObjectUtil.addObject(EdenCore.getInstance().getSkeleton(), new SegmentControllerRecord(entity, buildSector));
		PersistentObjectUtil.save(EdenCore.getInstance().getSkeleton());
	}

	/**
	 * Checks if a ship has been spawned in a build sector and if it has, checks if it's in the correct sector.
	 * @param entity the entity to check.
	 * @return whether or not the entity is legal.
	 */
	public static boolean isLegal(SegmentController entity) {
		for(Object obj : PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), SegmentControllerRecord.class)) {
			SegmentControllerRecord record = (SegmentControllerRecord) obj;
			if(record.id == entity.getDbId() && DataUtils.isBuildSector(record.buildSector) && !DataUtils.isBuildSector(entity.getSector(new Vector3i()))) return false;
		}
		return true;
	}

	public static void toggleAI(SegmentController entity, boolean toggle) {
		if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) entity.railController.getRoot().railController.activateAllAIServer(toggle, true, true, true);
		else entity.railController.getRoot().railController.activateAllAIClient(toggle, true, true);
		try {
			setPeace(DataUtils.getSectorData(entity.getSector(new Vector3i())), !toggle);
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void setPeace(BuildSectorData sectorData, boolean protect) {
		if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) {
			try {
				DataUtils.deleteEnemies(sectorData, 0);
				Sector sector = GameServer.getUniverse().getSector(sectorData.sector);
				sector.peace(protect);
				sector.protect(protect);
				sectorData.allAIDisabled = protect;
			} catch(IOException exception) {
				EdenCore.getInstance().logException("Failed to protect " + sectorData.ownerName + "'s build sector from enemy spawns due to an unexpected error", exception);
				PlayerUtils.sendMessage(GameCommon.getPlayerFromName(sectorData.ownerName), "An unexpected error occurred while trying to protect your sector from pirate spawns." + " Let an admin know ASAP!");
			}
		}
	}

	public static void spawnEnemy(PlayerState sender, BlueprintEntry entry, boolean aiEnabled, Vector3i buildSector) {
		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(sender.getFirstControlledTransformableWOExc().getWorldTransform().origin);
		Vector3f forward = GlUtil.getForwardVector(new Vector3f(), transform);
		Vector3f size = entry.getBb().calculateHalfSize(new Vector3f());
		size.scale(0.5f);
		forward.scaleAdd(1.15f, size);
		transform.origin.set(forward);
		try {
			SegmentControllerOutline<?> outline = BluePrintController.active.loadBluePrint(GameServerState.instance, entry.getName(), entry.getName(), transform, -1, FactionManager.PIRATES_ID, sender.getCurrentSector(), sender.getName(), PlayerState.buffer, null, false, new ChildStats(false));
			SegmentController entity = outline.spawn(sender.getCurrentSector(), false, new ChildStats(false), new SegmentControllerSpawnCallbackDirect(GameServer.getServerState(), sender.getCurrentSector()) {
				@Override
				public void onNoDocker() {
				}
			});
			recordSpawn(entity, buildSector);
			toggleAI(entity, aiEnabled);
		} catch(EntityNotFountException | IOException | EntityAlreadyExistsException | StateParameterNotFoundException exception) {
			exception.printStackTrace();
		}
	}

	public static SegmentController getEntityByName(PlayerState sender, String entityName) {
		Map<String, SegmentController> entityMap = GameServer.getServerState().getSegmentControllersByName();
		for(Map.Entry<String, SegmentController> entry : entityMap.entrySet()) {
			if(entry.getKey().toLowerCase().contains(entityName.toLowerCase())) {
				if(entry.getValue().getSectorId() == sender.getSectorId()) return entry.getValue();
			}
		}
		return null;
	}
}

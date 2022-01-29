package thederpgamer.edencore.utils;

import api.common.GameCommon;
import api.common.GameServer;
import api.utils.game.PlayerUtils;
import com.bulletphysics.linearmath.Transform;
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
import thederpgamer.edencore.data.other.BuildSectorData;
import thederpgamer.edencore.manager.LogManager;

import javax.vecmath.Vector3f;
import java.io.IOException;
import java.util.Map;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/27/2021]
 */
public class BuildSectorUtils {

    public static BlueprintEntry getEntry(PlayerState sender, String entryName) {
        for(CatalogPermission permission : sender.getCatalog().getAvailableCatalog()) {
            try {
                BlueprintEntry blueprintEntry = BluePrintController.active.getBlueprint(permission.getUid());
                if(blueprintEntry.getName().toLowerCase().contains(entryName.toLowerCase())) return blueprintEntry;
            } catch(EntityNotFountException ignored) { }
        }
        return null;
    }

    public static void spawnEntry(PlayerState sender, BlueprintEntry entry, boolean aiEnabled) {
        SegmentPiece spawnOnBlock = null;
        try {
            spawnOnBlock = ServerUtils.getBlockLookingAt(GameServer.getServerState(), sender);
        } catch(PlayerNotFountException | PlayerControlledTransformableNotFound | IOException ignored) { }

        Transform transform = new Transform();
        transform.setIdentity();
        transform.origin.set(sender.getFirstControlledTransformableWOExc().getWorldTransform().origin);
        Vector3f forward = GlUtil.getForwardVector(new Vector3f(), transform);
        Vector3f size = entry.getBb().calculateHalfSize(new Vector3f());
        size.scale(0.5f);
        forward.scaleAdd(1.15f, size);
        transform.origin.set(forward);

        try {
            SegmentControllerOutline<?> outline = BluePrintController.active.loadBluePrint(GameServerState.instance, entry.getName(), entry.getName(), transform, -1, sender.getFactionId(), sender.getCurrentSector(), sender.getName(), PlayerState.buffer, spawnOnBlock, false, new ChildStats(false));
            SegmentController entity = outline.spawn(sender.getCurrentSector(), false, new ChildStats(false), new SegmentControllerSpawnCallbackDirect(GameServer.getServerState(), sender.getCurrentSector()) {
                @Override
                public void onNoDocker() {

                }
            });
            toggleAI(entity, aiEnabled);
        } catch(EntityNotFountException | IOException | EntityAlreadyExistsException | StateParameterNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public static void spawnEnemy(PlayerState sender, BlueprintEntry entry, boolean aiEnabled) {
        Transform transform = new Transform();
        transform.setIdentity();
        transform.origin.set(sender.getFirstControlledTransformableWOExc().getWorldTransform().origin);
        Vector3f forward = GlUtil.getForwardVector(new Vector3f(), transform);
        Vector3f size = entry.getBb().calculateHalfSize(new Vector3f());
        size.scale(0.5f);
        forward.scaleAdd(1.15f, size);
        transform.origin.set(forward);
        try {
            SegmentControllerOutline<?> outline = BluePrintController.active.loadBluePrint(
                    GameServerState.instance,
                    entry.getName(),
                    entry.getName(),
                    transform,
                    -1,
                    FactionManager.PIRATES_ID,
                    sender.getCurrentSector(),
                    sender.getName(),
                    PlayerState.buffer,
                    null,
                    false,
                    new ChildStats(false));
            SegmentController entity = outline.spawn(sender.getCurrentSector(), false, new ChildStats(false), new SegmentControllerSpawnCallbackDirect(GameServer.getServerState(), sender.getCurrentSector()) {
                @Override
                public void onNoDocker() {

                }
            });
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

    public static void toggleAI(SegmentController entity, boolean toggle) {
        if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) entity.railController.getRoot().railController.activateAllAIServer(toggle, true, true, true);
        else entity.railController.getRoot().railController.activateAllAIClient(toggle, true, true);
    }

    public static void setPeace(BuildSectorData sectorData, boolean protect) {
        if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) {
            try {
                DataUtils.deleteEnemies(sectorData, 0);
                Sector sector = GameServer.getUniverse().getSector(sectorData.sector);
                sector.peace(protect);
                //sector.protect(protect); Todo: Make this a toggleable option
            } catch(IOException exception) {
                LogManager.logException("Failed to protect " + sectorData.ownerName + "'s build sector from enemy spawns due to an unexpected error", exception);
                PlayerUtils.sendMessage(GameCommon.getPlayerFromName(sectorData.ownerName), "An unexpected error occurred while trying to protect your sector from pirate spawns. Let an admin know ASAP!");
            }
        }
    }
}

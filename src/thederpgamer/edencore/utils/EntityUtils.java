package thederpgamer.edencore.utils;

import api.common.GameServer;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.controller.SpaceStation;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.server.ai.SegmentControllerAIEntity;
import org.schema.game.server.controller.BluePrintController;
import org.schema.game.server.controller.EntityAlreadyExistsException;
import org.schema.game.server.controller.EntityNotFountException;
import org.schema.game.server.data.GameServerState;
import org.schema.game.server.data.blueprint.ChildStats;
import org.schema.game.server.data.blueprint.SegmentControllerOutline;
import org.schema.game.server.data.blueprint.SegmentControllerSpawnCallbackDirect;
import org.schema.game.server.data.blueprintnw.BlueprintEntry;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.graphicsengine.core.settings.StateParameterNotFoundException;
import thederpgamer.edencore.manager.LogManager;

import javax.vecmath.Vector3f;
import java.io.IOException;
import java.util.ArrayList;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/20/2021]
 */
public class EntityUtils {

    public static void spawnEntry(PlayerState owner, BlueprintEntry entry, boolean aiEnabled) {
        Transform transform = new Transform();
        transform.setIdentity();
        transform.origin.set(owner.getFirstControlledTransformableWOExc().getWorldTransform().origin);
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
                    owner.getFactionId(),
                    owner.getCurrentSector(),
                    owner.getName(),
                    PlayerState.buffer,
                    null,
                    false,
                    new ChildStats(false));
            SegmentController entity = outline.spawn(owner.getCurrentSector(), false, new ChildStats(false), new SegmentControllerSpawnCallbackDirect(GameServer.getServerState(), owner.getCurrentSector()) {
                @Override
                public void onNoDocker() {

                }
            });
            toggleAI(entity, aiEnabled);
        } catch(EntityNotFountException | IOException | EntityAlreadyExistsException | StateParameterNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public static SegmentControllerAIEntity<?> getAIEntity(SegmentController entity) {
        try {
            switch(entity.getType()) {
                case SHIP: return ((Ship) entity).getAiConfiguration().getAiEntityState();
                case SPACE_STATION: return ((SpaceStation) entity).getAiConfiguration().getAiEntityState();
                default: throw new IllegalArgumentException("Entity must either be a Ship or Station!");
            }
        } catch(IllegalArgumentException exception) {
            LogManager.logCritical("A critical exception occurred while trying to get an AIEntityState from an invalid or corrupted entity!", exception);
        }
        return null;
    }

    public static void toggleAI(SegmentController entity, boolean toggle) {
        SegmentControllerAIEntity<?> aiEntity = getAIEntity(entity);
        if(aiEntity != null) {
            if(!toggle && aiEntity.getCurrentProgram() != null) aiEntity.getCurrentProgram().suspend(true);
            else if(aiEntity.getCurrentProgram() != null) aiEntity.getCurrentProgram().suspend(false);
        }
        ArrayList<SegmentController> dockedList = new ArrayList<SegmentController>();
        entity.railController.getDockedRecusive(dockedList);
        for(SegmentController docked : dockedList) {
            aiEntity = getAIEntity(docked);
            if(aiEntity != null) {
                if(!toggle && aiEntity.getCurrentProgram() != null) aiEntity.getCurrentProgram().suspend(true);
                else if(aiEntity.getCurrentProgram() != null) aiEntity.getCurrentProgram().suspend(false);

            }
        }
    }
}

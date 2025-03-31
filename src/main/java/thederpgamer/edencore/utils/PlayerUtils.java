package thederpgamer.edencore.utils;

import api.common.GameServer;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.physics.CubeRayCastResult;
import org.schema.game.common.data.physics.PhysicsExt;
import org.schema.game.common.data.player.AbstractCharacter;
import org.schema.game.common.data.player.PlayerControlledTransformableNotFound;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.world.Sector;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.schine.graphicsengine.core.GlUtil;

import javax.vecmath.Vector3f;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class PlayerUtils {
	
	public static SegmentPiece getBlockLookingAt(PlayerState player) throws PlayerControlledTransformableNotFound {
		Vector3f pos;
		Vector3f to;
		SimpleTransformableSendableObject<?> firstControlledTransformable = player.getFirstControlledTransformable();
		if(player.getNetworkObject().isInBuildMode.getBoolean()) {
			pos = new Vector3f(player.getBuildModePosition().getWorldTransform().origin);
			Vector3f forw = GlUtil.getForwardVector(new Vector3f(), player.getBuildModePosition().getWorldTransform());
			forw.scale(5000);
			to = new Vector3f(pos);
			to.add(forw);
		} else {
			if(firstControlledTransformable instanceof AbstractCharacter<?>) pos = new Vector3f(((AbstractCharacter<?>) firstControlledTransformable).getHeadWorldTransform().origin);
			else pos = new Vector3f(firstControlledTransformable.getWorldTransform().origin);
			to = new Vector3f(pos);
			Vector3f forw = player.getForward(new Vector3f());
			forw.scale(5000);
			to.add(forw);
		}
		Sector sector = GameServer.getUniverse().getSector(firstControlledTransformable.getSectorId());
		CollisionWorld.ClosestRayResultCallback testRayCollisionPoint = ((PhysicsExt) sector.getPhysics()).testRayCollisionPoint(pos, to, false, null, null, false, true, false);
		if(testRayCollisionPoint.hasHit() && testRayCollisionPoint instanceof CubeRayCastResult && ((CubeRayCastResult) testRayCollisionPoint).getSegment() != null) {
			CubeRayCastResult c = (CubeRayCastResult) testRayCollisionPoint;
			SegmentPiece p = new SegmentPiece(c.getSegment(), c.getCubePos());
			if(p.isValid() && p.getInfo().isRailDockable()) return p;
		}
		return null;
	}
}

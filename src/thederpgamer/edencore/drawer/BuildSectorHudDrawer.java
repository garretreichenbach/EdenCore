package thederpgamer.edencore.drawer;

import api.common.GameClient;
import api.utils.draw.ModWorldDrawer;
import api.utils.game.PlayerUtils;
import com.bulletphysics.linearmath.Transform;
import org.schema.game.client.view.gui.shiphud.newhud.Hud;
import org.schema.game.common.controller.SegmentController;
import org.schema.schine.graphicsengine.core.Timer;
import thederpgamer.edencore.utils.DataUtils;

import java.util.Arrays;

/**
 * Modifies the Hud Drawer while the client is in a build sector in order to hide the actual coordinates.
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/08/2021]
 */
public class BuildSectorHudDrawer extends ModWorldDrawer {

	private boolean wasInBuildSectorLastFrame;
	private BuildSectorBoundary sectorBoundary;
	private float update = 0;

	@Override
	public void update(Timer timer) {
		if(DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
			if(update <= 0) {
				updateBoundary(timer);
				update = 50;
			} else update --;
		} else update = 0;
	}

	@Override
	public void draw() {
		Hud hud = GameClient.getClientState().getWorldDrawer().getGuiDrawer().getHud();
		if(DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
			try {
				if(!wasInBuildSectorLastFrame) {
					hud.getRadar().getLocation().setTextSimple("<Build Sector>");
					hud.getIndicator().drawSectorIndicators = false;
					hud.getIndicator().drawWaypoints = false;
					if(GameClient.getClientState().getController().getClientGameData().getWaypoint() != null) GameClient.getClientState().getController().getClientGameData().setWaypoint(null);
				}
			} catch(Exception ignored) { }
			if(sectorBoundary != null) sectorBoundary.draw();
			wasInBuildSectorLastFrame = true;
		} else {
			try {
				if(wasInBuildSectorLastFrame) {
					hud.getRadar().getLocation().setTextSimple(GameClient.getClientPlayerState().getCurrentSector().toStringPure());
					hud.getIndicator().drawSectorIndicators = true;
					hud.getIndicator().drawWaypoints = true;
				}
			} catch(Exception ignored) { }
			wasInBuildSectorLastFrame = false;
		}
	}

	@Override
	public void cleanUp() {
	}

	@Override
	public boolean isInvisible() {
		return false;
	}

	@Override
	public void onInit() {

	}

	private void updateBoundary(Timer timer) {
		Hud hud = GameClient.getClientState().getWorldDrawer().getGuiDrawer().getHud();
		Transform closest = null;
		Transform current = new Transform();
		if(PlayerUtils.getCurrentControl(GameClient.getClientPlayerState()) instanceof SegmentController) current.set(((SegmentController) PlayerUtils.getCurrentControl(GameClient.getClientPlayerState())).getWorldTransform());
		else GameClient.getClientPlayerState().getWordTransform(current);
		Transform[] transforms = hud.getIndicator().neighborSectors;
		for(Transform transform : transforms) {
			Transform t = new Transform(transform);
			t.origin.scale(0.5f);
			if(closest == null || getDistance(current, t) < getDistance(current, closest)) closest = new Transform(t);
		}

		if(sectorBoundary == null) (sectorBoundary = new BuildSectorBoundary(DataUtils.getPlayerCurrentBuildSector(GameClient.getClientPlayerState()))).onInit();
		if(closest != null && getDistance(current, closest) <= 1000) {
			sectorBoundary.setClosestBorder(closest);
			sectorBoundary.addIntersect(closest);
			sectorBoundary.update(timer);
		} else sectorBoundary.cleanUp();
	}

	private float getDistance(Transform transformA, Transform transformB) {
		float x = Math.abs(transformA.origin.x - transformB.origin.x);
		float y = Math.abs(transformA.origin.y - transformB.origin.y);
		float z = Math.abs(transformA.origin.z - transformB.origin.z);
		float[] values = new float[] {x, y, z};
		Arrays.sort(values);
		return values[0];
	}
}

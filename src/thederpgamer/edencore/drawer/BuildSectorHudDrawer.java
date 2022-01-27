package thederpgamer.edencore.drawer;

import api.common.GameClient;
import api.utils.draw.ModWorldDrawer;
import org.schema.game.client.view.gui.shiphud.newhud.Hud;
import org.schema.schine.graphicsengine.core.Timer;
import thederpgamer.edencore.utils.DataUtils;

/**
 * Modifies the Hud Drawer while the client is in a build sector in order to hide the actual
 * coordinates.
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/08/2021]
 */
public class BuildSectorHudDrawer extends ModWorldDrawer {
  private boolean wasInBuildSectorLastFrame;

  @Override
  public void onInit() {}

  @Override
  public void draw() {
    Hud hud = GameClient.getClientState().getWorldDrawer().getGuiDrawer().getHud();
    if (DataUtils.isPlayerInAnyBuildSector(GameClient.getClientPlayerState())) {
      try {
        if (!wasInBuildSectorLastFrame) {
          hud.getRadar().getLocation().setTextSimple("<Build Sector>");
          hud.getIndicator().drawSectorIndicators = false;
          hud.getIndicator().drawWaypoints = false;
          if (GameClient.getClientState().getController().getClientGameData().getWaypoint() != null)
            GameClient.getClientState().getController().getClientGameData().setWaypoint(null);
        }
      } catch (Exception ignored) {
      }
      wasInBuildSectorLastFrame = true;
    } else {
      try {
        if (wasInBuildSectorLastFrame) {
          hud.getRadar()
              .getLocation()
              .setTextSimple(GameClient.getClientPlayerState().getCurrentSector().toStringPure());
          hud.getIndicator().drawSectorIndicators = true;
          hud.getIndicator().drawWaypoints = true;
        }
      } catch (Exception ignored) {
      }
      wasInBuildSectorLastFrame = false;
    }
  }

  @Override
  public void update(Timer timer) {}

  @Override
  public void cleanUp() {}

  @Override
  public boolean isInvisible() {
    return false;
  }
}

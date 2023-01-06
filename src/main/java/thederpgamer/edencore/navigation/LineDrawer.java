package thederpgamer.edencore.navigation;

import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector4f;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.view.gamemap.GameMapDrawer;

import java.io.Serializable;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 26.09.2021
 * TIME: 15:34
 */
public interface LineDrawer {
    void drawLines(GameMapDrawer gameMapDrawer);
}

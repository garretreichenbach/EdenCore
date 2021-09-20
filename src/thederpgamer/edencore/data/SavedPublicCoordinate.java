package thederpgamer.edencore.data;

import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.player.SavedCoordinate;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 18.09.2021
 * TIME: 18:03
 */
public class SavedPublicCoordinate extends SavedCoordinate {
    public SavedPublicCoordinate(Vector3i var1, String var2) {
        this.setSector(var1);
        this.setName(var2);
    }
}

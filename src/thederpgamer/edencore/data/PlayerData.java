package thederpgamer.edencore.data;

import org.schema.common.util.linAlg.Vector3i;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/03/2021
 */
public class PlayerData implements ComparableData {

    public String playerName;
    public String playerId;
    public Vector3i lastRealSector;

    public PlayerData(String playerName, String playerId, Vector3i lastRealSector) {
        this.playerName = playerName;
        this.playerId = playerId;
        this.lastRealSector = lastRealSector;
    }

    @Override
    public boolean equalTo(ComparableData data) {
        if(data instanceof PlayerData) {
            PlayerData playerData = (PlayerData) data;
            return playerData.playerName.equals(playerName) && playerData.playerId.equals(playerId);
        } else return false;
    }
}

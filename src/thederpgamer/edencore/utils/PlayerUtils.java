package thederpgamer.edencore.utils;

import api.common.GameClient;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.manager.ConfigManager;

/**
 * Various player related utilities.
 *
 * @author TheDerpGamer
 * @since 06/27/2021
 */
public class PlayerUtils {

    public enum PlayerType {NORMAL, ADMIN, OPERATOR}

    public static PlayerType getPlayerType(PlayerState player) {
        if(ConfigManager.getMainConfig().getList("operators").contains(player.getName()) && ConfigManager.getMainConfig().getList("operators").contains(GameClient.getClientPlayerState().getName())) return PlayerType.OPERATOR;
        else if(player.isAdmin()) return PlayerType.ADMIN;
        else return PlayerType.NORMAL;
    }
}

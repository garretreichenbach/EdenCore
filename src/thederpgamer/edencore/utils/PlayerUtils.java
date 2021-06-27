package thederpgamer.edencore.utils;

import api.common.GameClient;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.manager.ConfigManager;
import java.util.ArrayList;

/**
 * Various player related utilities.
 *
 * @author TheDerpGamer
 * @since 06/27/2021
 */
public class PlayerUtils {

    public enum PlayerType {NORMAL, FAKE, ADMIN, OPERATOR}

    public static ArrayList<PlayerState> fakePlayers = new ArrayList<>();

    public static PlayerType getPlayerType(PlayerState player) {
        if(ConfigManager.getMainConfig().getList("operators").contains(player.getName()) && ConfigManager.getMainConfig().getList("operators").contains(GameClient.getClientPlayerState().getName())) {
            return PlayerType.OPERATOR;
        } else if(player.isAdmin()) {
            return PlayerType.ADMIN;
        } else if(fakePlayers.contains(player) && ConfigManager.getMainConfig().getList("operators").contains(GameClient.getClientPlayerState().getName())) {
            return PlayerType.FAKE;
        } else {
            return PlayerType.NORMAL;
        }
    }
}

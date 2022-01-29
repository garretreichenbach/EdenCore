package thederpgamer.edencore.commands;

import api.mod.StarMod;
import api.utils.game.chat.CommandInterface;
import org.apache.commons.lang3.math.NumberUtils;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;

import javax.annotation.Nullable;

/**
 * Player countdown command for events and duels.
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/21/2021]
 */
public class CountdownCommand implements CommandInterface {

    @Override
    public String getCommand() {
        return "countdown";
    }

    @Override
    public String[] getAliases() {
        return new String[] {
                "countdown",
                "count_down"
        };
    }

    @Override
    public String getDescription() {
        return "Creates a countdown that displays for all players in the current squad. Requires the user to be the leader of an active squad.\n" +
               "- /%COMMAND% <seconds> [label]";
    }

    @Override
    public boolean isAdminOnly() {
        return false;
    }

    @Override
    public boolean onCommand(PlayerState sender, String[] args) {
        if(args.length >= 1) {
            if(NumberUtils.isNumber(args[0])) {
                String label = null;
                if(args.length == 1) label = "";
                else if(args.length == 2) label = args[1];
                else return false;

            }
        }
        return true;
    }

    @Override
    public void serverAction(@Nullable PlayerState sender, String[] args) {

    }

    @Override
    public StarMod getMod() {
        return EdenCore.getInstance();
    }
}

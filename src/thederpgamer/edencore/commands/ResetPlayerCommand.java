package thederpgamer.edencore.commands;

import api.mod.StarMod;
import api.utils.game.PlayerUtils;
import api.utils.game.chat.CommandInterface;
import javax.annotation.Nullable;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.utils.ServerUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/10/2021]
 */
public class ResetPlayerCommand implements CommandInterface {

  @Override
  public String getCommand() {
    return "reset_player";
  }

  @Override
  public String[] getAliases() {
    return new String[] {"reset_player", "player_reset"};
  }

  @Override
  public String getDescription() {
    return "Resets the specified data for a player. Useful for fixing player related bugs.\n"
               + "- /%COMMAND% <player> <inventory|faction|fleets|spawn|location|navigation|all/*>"
               + " : Resets the specified data for a player.";
  }

  @Override
  public boolean isAdminOnly() {
    return true;
  }

  @Override
  public boolean onCommand(PlayerState sender, String[] args) {
    if (args.length == 2) {
      PlayerState target = ServerUtils.getPlayerByName(args[0]);
      if (target != null) {
        boolean online = target.isOnServer();
        switch (args[1].toLowerCase()) {
          case "inventory":
            ServerUtils.clearInventoryFull(target);
            PlayerUtils.sendMessage(sender, "Reset inventory data for player \"" + args[0] + "\".");
            return true;
          case "location":
            return true;
          default:
            return false;
        }
      } else PlayerUtils.sendMessage(sender, "Player \"" + args[0] + "\" doesn't exist!");
    } else return false;
    return true;
  }

  @Override
  public void serverAction(@Nullable PlayerState sender, String[] args) {}

  @Override
  public StarMod getMod() {
    return EdenCore.getInstance();
  }
}

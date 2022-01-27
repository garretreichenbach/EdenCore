package thederpgamer.edencore.commands;

import api.mod.StarMod;
import api.utils.game.PlayerUtils;
import api.utils.game.chat.CommandInterface;
import javax.annotation.Nullable;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.manager.TransferManager;

/**
 * <Description>
 *
 * @author TheDerpGamer
 */
public class ListEntityCommand implements CommandInterface {

  @Override
  public String getCommand() {
    return "entity_list";
  }

  @Override
  public String[] getAliases() {
    return new String[] {"entity_list"};
  }

  @Override
  public String getDescription() {
    return "Returns a list of all your saved entities.\n"
        + "- /%COMMAND% : Lists all entities saved by the player.";
  }

  @Override
  public boolean isAdminOnly() {
    return false;
  }

  @Override
  public boolean onCommand(PlayerState sender, String[] args) {
    if (args == null || args.length == 0 || args[0] == null || args[0].isEmpty())
      PlayerUtils.sendMessage(sender, TransferManager.getSavedEntitiesList(sender));
    else return false;
    return true;
  }

  @Override
  public void serverAction(@Nullable PlayerState sender, String[] args) {}

  @Override
  public StarMod getMod() {
    return EdenCore.getInstance();
  }
}

package thederpgamer.edencore.commands;

import api.common.GameCommon;
import api.common.GameServer;
import api.mod.StarMod;
import api.utils.game.PlayerUtils;
import api.utils.game.chat.CommandInterface;
import api.utils.game.inventory.InventoryUtils;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.annotation.Nullable;
import org.apache.commons.lang3.math.NumberUtils;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.element.ElementManager;
import thederpgamer.edencore.element.items.Item;
import thederpgamer.edencore.manager.LogManager;
import thederpgamer.edencore.utils.ServerUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/19/2021]
 */
public class AwardBarsCommand implements CommandInterface {

  @Override
  public String getCommand() {
    return "award_bars";
  }

  @Override
  public String[] getAliases() {
    return new String[] {"award_bars"};
  }

  @Override
  public String getDescription() {
    return "Awards the specified player prize bars which can be used at the exchange.\n"
               + "- /%COMMAND% <player|all/*> <bronze|silver|gold> <count> [online_only] : Awards a"
               + " player bars of the specified type.";
  }

  @Override
  public boolean isAdminOnly() {
    return true;
  }

  @Override
  public boolean onCommand(PlayerState sender, String[] args) {
    if (!(args.length == 3 || args.length == 4)) return false;
    boolean onlineOnly = args.length == 4 && args[3].toLowerCase().equals("true");
    if (args[0].toLowerCase().equals("all") || args[0].toLowerCase().equals("*")) {
      Item bar;
      switch (args[1].toLowerCase()) {
        case "bronze":
          bar = ElementManager.getItem("Bronze Bar");
          break;
        case "silver":
          bar = ElementManager.getItem("Silver Bar");
          break;
        case "gold":
          bar = ElementManager.getItem("Gold Bar");
          break;
        default:
          return false;
      }

      if (NumberUtils.isNumber(args[2].trim()) && bar != null) {
        int amount = Integer.parseInt(args[2].trim());
        if (amount <= 0) return false;
        ArrayList<PlayerState> allPlayers = ServerUtils.getAllPlayers();
        for (PlayerState playerState : allPlayers) {
          if (onlineOnly
              && (!playerState.isOnServer()
                  || GameCommon.getPlayerFromName(playerState.getName()) == null)) continue;

          InventoryUtils.addItem(playerState.getInventory(), bar.getId(), amount);
          String name =
              (amount > 1) ? bar.getItemInfo().getName() + "s" : bar.getItemInfo().getName();
          if (playerState.isOnServer()
              && GameCommon.getPlayerFromName(playerState.getName()) != null) { // Player is online
            PlayerUtils.sendMessage(
                playerState, "You have been awarded " + amount + " " + name + ".");
            // PlayerUtils.sendMessage(sender, "Awarded " + amount + " " + name + " to player " +
            // playerState.getName() + ".");
          } else { // Player is offline
            try {
              GameServer.getServerState()
                  .getDatabaseIndex()
                  .getTableManager()
                  .getPlayerTable()
                  .updateOrInsertPlayer(playerState);
              // PlayerUtils.sendMessage(sender, "Awarded " + amount + " " + name + " to player " +
              // playerState.getName() + ".");
            } catch (SQLException exception) {
              LogManager.logException(
                  "Encountered an exception while trying to modify player data in Server Database",
                  exception);
              // PlayerUtils.sendMessage(sender, "Encountered an exception while trying to modify
              // player data in Server Database.");
            }
          }
        }
      }
    } else {
      PlayerState targetPlayer = ServerUtils.getPlayerByName(args[0]);
      if (targetPlayer == null)
        PlayerUtils.sendMessage(sender, "Player \"" + args[0] + "\" doesn't exist.");
      else {
        Item bar;
        switch (args[1].toLowerCase()) {
          case "bronze":
            bar = ElementManager.getItem("Bronze Bar");
            break;
          case "silver":
            bar = ElementManager.getItem("Silver Bar");
            break;
          case "gold":
            bar = ElementManager.getItem("Gold Bar");
            break;
          default:
            return false;
        }

        if (NumberUtils.isNumber(args[2].trim()) && bar != null) {
          int amount = Integer.parseInt(args[2].trim());
          if (amount <= 0) return false;

          if (onlineOnly
              && (!targetPlayer.isOnServer()
                  || GameCommon.getPlayerFromName(targetPlayer.getName()) == null)) {
            PlayerUtils.sendMessage(sender, "Player \"" + args[1] + "\" isn't online right now.");
            return true;
          }

          InventoryUtils.addItem(targetPlayer.getInventory(), bar.getId(), amount);
          String name =
              (amount > 1) ? bar.getItemInfo().getName() + "s" : bar.getItemInfo().getName();
          if (targetPlayer.isOnServer()
              && GameCommon.getPlayerFromName(targetPlayer.getName()) != null) { // Player is online
            PlayerUtils.sendMessage(
                targetPlayer, "You have been awarded " + amount + " " + name + ".");
            PlayerUtils.sendMessage(
                sender,
                "Awarded " + amount + " " + name + " to player " + targetPlayer.getName() + ".");
          } else { // Player is offline
            try {
              GameServer.getServerState()
                  .getDatabaseIndex()
                  .getTableManager()
                  .getPlayerTable()
                  .updateOrInsertPlayer(targetPlayer);
              PlayerUtils.sendMessage(
                  sender,
                  "Awarded " + amount + " " + name + " to player " + targetPlayer.getName() + ".");
            } catch (SQLException exception) {
              LogManager.logException(
                  "Encountered an exception while trying to modify player data in Server Database",
                  exception);
              PlayerUtils.sendMessage(
                  sender,
                  "Encountered an exception while trying to modify player data in Server"
                      + " Database.");
            }
          }
        }
      }
    }
    return true;
  }

  @Override
  public void serverAction(@Nullable PlayerState sender, String[] args) {}

  @Override
  public StarMod getMod() {
    return EdenCore.getInstance();
  }
}

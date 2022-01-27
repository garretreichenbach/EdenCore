package thederpgamer.edencore.commands;

import api.mod.StarMod;
import api.utils.game.PlayerUtils;
import api.utils.game.chat.CommandInterface;
import javax.annotation.Nullable;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.element.ElementDocking;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.manager.ConfigManager;
import thederpgamer.edencore.manager.LogManager;
import thederpgamer.edencore.manager.TransferManager;
import thederpgamer.edencore.utils.DataUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/21/2021
 */
public class SaveEntityCommand implements CommandInterface {

  @Override
  public String getCommand() {
    return "entity_save";
  }

  @Override
  public String[] getAliases() {
    return new String[] {"entity_save"};
  }

  @Override
  public String getDescription() {
    return "Saves your current entity including cargo and prepares it so it can be transferred to"
        + " the new save after the server reset. Will only work if the server has an"
        + " upcoming reset.\n"
        + "- /%COMMAND% : Saves your current entity and it's cargo so it can be transferred"
        + " to the new save. This will de-spawn the entity after saving and it will not be"
        + " accessible until after the reset.";
  }

  @Override
  public boolean isAdminOnly() {
    return false;
  }

  @Override
  public boolean onCommand(PlayerState playerState, String[] args) {
    if (TransferManager.getTransferMode() == TransferManager.SAVE
        || (ConfigManager.getMainConfig().getBoolean("debug-mode") && playerState.isAdmin())) {
      if (TransferManager.canTransfer(playerState)) {
        SegmentController entity =
            (SegmentController) playerState.getFirstControlledTransformableWOExc();
        if (TransferManager.isValidTransfer(playerState)) {
          try {
            TransferManager.saveEntity(playerState, entity);
            PlayerUtils.sendMessage(
                playerState,
                "Successfully saved entity \""
                    + DataUtils.getEntityNameFormatted(entity)
                    + "\" for transferring. Use /entity_load \""
                    + DataUtils.getEntityNameFormatted(entity)
                    + "\" after the reset to complete the transfer.");
            entity.railController.destroyDockedRecursive();
            for (ElementDocking dock : entity.getDockingController().getDockedOnThis()) {
              dock.from.getSegment().getSegmentController().markForPermanentDelete(true);
              dock.from.getSegment().getSegmentController().setMarkedForDeleteVolatile(true);
            }
            entity.markForPermanentDelete(true);
            entity.setMarkedForDeleteVolatile(true);
          } catch (Exception exception) {
            LogManager.logException(
                "Failed to save entity \""
                    + DataUtils.getEntityNameFormatted(entity)
                    + "\" to world transfer folder",
                exception);
            PlayerUtils.sendMessage(
                playerState,
                "An exception occurred while trying to save \""
                    + DataUtils.getEntityNameFormatted(entity)
                    + "\" to world transfer folder. Let an admin know if this continues to occur!");
          }
        } else
          PlayerUtils.sendMessage(
              playerState,
              "This entity was spawned by an admin or ai and therefore cannot be transferred.");
      } else
        PlayerUtils.sendMessage(
            playerState, "You must be inside a ship or space station to use this command.");
    } else
      PlayerUtils.sendMessage(
          playerState,
          "Sorry, but there is either no upcoming or recent resets or you have missed the transfer"
              + " deadline.");
    return true;
  }

  @Override
  public void serverAction(@Nullable PlayerState playerState, String[] strings) {}

  @Override
  public StarMod getMod() {
    return EdenCore.getInstance();
  }
}

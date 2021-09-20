package thederpgamer.edencore.commands;

import api.mod.StarMod;
import api.utils.game.PlayerUtils;
import api.utils.game.chat.CommandInterface;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.manager.ConfigManager;
import thederpgamer.edencore.manager.TransferManager;

import javax.annotation.Nullable;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/21/2021
 */
public class LoadEntityCommand implements CommandInterface {

    @Override
    public String getCommand() {
        return "entity_load";
    }

    @Override
    public String[] getAliases() {
        return new String[] {
                "entity_load"
        };
    }

    @Override
    public String getDescription() {
        return "Loads the specified entity from the world transfer folder and spawns it in front of you. Will only work if the server was recently reset.\n" +
                "- /%COMMAND% : Loads the specified entity if it exists in the world transfer folder. This will remove the entity from the save folder once it's been spawned.";
    }

    @Override
    public boolean isAdminOnly() {
        return false;
    }

    @Override
    public boolean onCommand(PlayerState playerState, String[] args) {
        if(TransferManager.getTransferMode() == TransferManager.LOAD || (ConfigManager.getMainConfig().getBoolean("debug-mode") && playerState.isAdmin())) {
            if(args != null && args.length == 1) {
                String entityName = args[0];
                try {
                    TransferManager.loadEntity(playerState, entityName);
                    PlayerUtils.sendMessage(playerState, "Successfully loaded entity \"" + entityName + "\".");
                } catch(Exception exception) {
                    if(exception instanceof IllegalArgumentException) PlayerUtils.sendMessage(playerState, "The entity saved was neither a ship or station, and therefore cannot be transferred.");
                    else if(exception instanceof NullPointerException) PlayerUtils.sendMessage(playerState, "There is no entity by the name \"" + entityName + "\" saved in the world transfer folder.");
                    else if(exception instanceof SecurityException) PlayerUtils.sendMessage(playerState, "A critical exception occurred on server while trying to load \"" + entityName + "\" and therefore the entity cannot be loaded. Report this to an admin ASAP!");
                    else exception.printStackTrace();
                }
            }
        } else PlayerUtils.sendMessage(playerState, "Sorry, but there is either no upcoming or recent resets or you have missed the transfer deadline.");
        return true;
    }

    @Override
    public void serverAction(@Nullable PlayerState playerState, String[] strings) {

    }

    @Override
    public StarMod getMod() {
        return EdenCore.getInstance();
    }
}

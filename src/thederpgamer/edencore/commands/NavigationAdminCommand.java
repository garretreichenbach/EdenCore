package thederpgamer.edencore.commands;

import api.mod.StarMod;
import api.utils.game.PlayerUtils;
import api.utils.game.chat.CommandInterface;
import org.lwjgl.util.vector.Vector;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.SavedCoordinate;
import thederpgamer.edencore.data.PlayerDataUtil;
import thederpgamer.edencore.manager.NavigationUtilManager;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 18.09.2021
 * TIME: 15:31
 */
public class NavigationAdminCommand implements CommandInterface {
    @Override
    public String getCommand() {
        return "navigation";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"nav"};
    }

    @Override
    public String getDescription() {
        return "adds, removes or lists coordinates to be displayed in each players 'saved coordinates' tab \n" +
                "%COMMAND% add 5 5 5 \"burgerking\"\n"+
                "%COMMAND% remove 5 5 5\n"+
                "%COMMAND% list \n";
    }

    @Override
    public boolean isAdminOnly() {
        return true;
    }

    @Override
    public boolean onCommand(PlayerState admin, String[] strings) {
        switch (strings[0]) {
            case "add": {
                //add 3 3 3 "burgerking"
                if (strings.length!=5)
                    return false;
                Vector3i pos = new Vector3i();
                try {
                    pos.x = Integer.parseInt(strings[1]);
                    pos.y = Integer.parseInt(strings[2]);
                    pos.z = Integer.parseInt(strings[3]);
                } catch (NumberFormatException ex) {
                    return false;
                }
                String name = strings[4];
                NavigationUtilManager.instance.addCoordinateToList(pos,name);
                PlayerUtils.sendMessage(admin,"added " + pos.toString() + name +" to list, will update everyone at restart");

                return true;
            }

            case "remove": {
                //remove 3 3 3 "burgerking"
                if (strings.length!=5)
                    return false;
                Vector3i pos = new Vector3i();
                try {
                    pos.x = Integer.parseInt(strings[1]);
                    pos.y = Integer.parseInt(strings[2]);
                    pos.z = Integer.parseInt(strings[3]);
                } catch (NumberFormatException ex) {
                    return false;
                }
                String name = strings[4];
                NavigationUtilManager.instance.removeCoordinateFromList(pos,name);
                PlayerUtils.sendMessage(admin,"removing coords for all players");
                return true;
            }

            case "list": {
                PlayerUtils.sendMessage(admin,getNavlistString());
                return true;
            }

            case "update": {
                NavigationUtilManager.instance.updateAllPlayerFiles();
                PlayerUtils.sendMessage(admin,"updated all nav points");
                return true;
            }

            case "allnames": {
                try {
                    PlayerDataUtil.getAllPlayerNamesEver();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                return true;
            }
            case "insert": {
                return true;
            }

        }
        return false;
    }

    private String getNavlistString() {
        StringBuilder out = new StringBuilder();
        out.append("Listing all public coords: \n");
        for (SavedCoordinate c: NavigationUtilManager.instance.getCoordsAddList().values()) {
            out.append(c.getSector().toString()).append(": ").append(c.getName());
            out.append("\n");
        }
        out.append("\n removal list: \n");
        for (Long code: NavigationUtilManager.instance.getCoordsRemoveList()) {
            out.append(code).append("\n");
        }
        out.append("end of list");
        return out.toString();
    }

    @Override
    public void serverAction(@Nullable PlayerState playerState, String[] strings) {

    }

    @Override
    public StarMod getMod() {
        return null;
    }
}

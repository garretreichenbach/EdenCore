package thederpgamer.edencore.navigation;

import api.mod.StarMod;
import api.utils.game.PlayerUtils;
import api.utils.game.chat.CommandInterface;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.SavedCoordinate;
import thederpgamer.edencore.utils.PlayerDataUtil;

import javax.annotation.Nullable;
import javax.vecmath.Vector4f;
import java.sql.SQLException;
import java.util.ArrayList;

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
        return "Adds, removes or lists coordinates to be displayed in each players saved coordinates.\n" +
                "%COMMAND% add [<x> <y> <z>] <\"name\"> <icon index>: Adds a public navigation point to the list of saved coordinates of all players.\n"+
                "%COMMAND% remove <x> <y> <z> : Removes a public navigation point to list of saved coordinates of all players.\n"+
                "%COMMAND% list : Lists all public navigation points.\n";
    }

    @Override
    public boolean isAdminOnly() {
        return true;
    }

    @Override
    public boolean onCommand(PlayerState admin, String[] strings) {
        switch(strings[0]) {
            case "add": {
                try {

                } catch (Exception e) {

                }
                //add 3 3 3 "burgerking" 0 || add "burgerking" 0 (this sector)
                if(strings.length != 6 && strings.length != 3)
                    return false;

                Vector3i pos = new Vector3i();
                Integer iconIdx;
                String name;
                if (strings.length == 5) {
                    pos.x = Integer.parseInt(strings[1]);
                    pos.y = Integer.parseInt(strings[2]);
                    pos.z = Integer.parseInt(strings[3]);
                    name = strings[4];
                    iconIdx = Integer.parseInt(strings[5]);
                } else {
                    pos.set(admin.getCurrentSector());
                    name = strings[1];
                    iconIdx = Integer.parseInt(strings[2]);
                }
                MapMarker marker = new MapMarker(pos,name,MapIcon.values()[iconIdx],new Vector4f(0,0,1,1)); //TODO add way to parse color
                NavigationUtilManager.instance.addCoordinateToList(marker);
                PlayerUtils.sendMessage(admin,"Added " + marker.toString() +" to list");
                return true;
            }
            case "remove": {
                //remove 3 3 3 "burgerking"
                if(strings.length != 5) return false;
                Vector3i pos = new Vector3i();
                try {
                    pos.x = Integer.parseInt(strings[1]);
                    pos.y = Integer.parseInt(strings[2]);
                    pos.z = Integer.parseInt(strings[3]);
                } catch(NumberFormatException ex) {
                    return false;
                }
                String name = strings[4];
                NavigationUtilManager.instance.removeCoordinateFromList(pos,name);
                PlayerUtils.sendMessage(admin,"Removing coords for all players");
                return true;
            }
            case "list": {
                PlayerUtils.sendMessage(admin,getNavlistString());
                return true;
            }

            case "synch": {
                new NavigationMapPacket(NavigationUtilManager.instance.getPublicMarkers().values()).sendToAllServer();
                return true;
            }
        }
        return false;
    }

    private String getNavlistString() {
        StringBuilder out = new StringBuilder();
        out.append("Listing all public coords: \n");
        for (MapMarker c: NavigationUtilManager.instance.getPublicMarkers().values()) {
            out.append(c.getSector().toString()).append(": ").append(c.getName());
            out.append("\n");
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

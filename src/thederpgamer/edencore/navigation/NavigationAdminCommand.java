package thederpgamer.edencore.navigation;

import api.mod.StarMod;
import api.utils.game.PlayerUtils;
import api.utils.game.chat.CommandInterface;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SpaceStation;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.world.FTLConnection;
import org.schema.game.server.data.GameServerState;
import org.schema.schine.network.objects.Sendable;

import javax.annotation.Nullable;
import javax.vecmath.Vector4f;
import java.util.ArrayList;
import java.util.Random;

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
                } catch (Exception e) {
                    return false;
                }
            }
            case "remove": {
                //remove 3 3 3 || remove
                if(strings.length != 4 && strings.length != 1) return false;

                Vector3i pos = new Vector3i();
                if (strings.length == 4) {
                    pos.x = Integer.parseInt(strings[1]);
                    pos.y = Integer.parseInt(strings[2]);
                    pos.z = Integer.parseInt(strings[3]);
                } else {
                    pos.set(admin.getCurrentSector());
                }
                NavigationUtilManager.instance.removeCoordinateFromList(pos);

                PlayerUtils.sendMessage(admin,"Removed marker at " + pos);
                return true;
            }

            case "gate": { //gate "Space KFC"
                //get name
                String gateName = null;
                if (strings.length==2) {
                    gateName = strings[1];
                } else {
                    PlayerUtils.sendMessage(admin,"need to give name for gate");
                    return false;
                }

                int selectedEntId = admin.getSelectedEntityId();
                Sendable selected = GameServerState.instance.getLocalAndRemoteObjectContainer().getLocalObjects().get(selectedEntId);
                //Sendable sendable = getEntityState().getState().getLocalAndRemoteObjectContainer().getLocalObjects().get(selectedEntityId);
                if (selected == null) {
                    PlayerUtils.sendMessage(admin,"No entity selected");
                    return false;
                }

                if (!(selected instanceof SpaceStation)) {
                    PlayerUtils.sendMessage(admin,"Selected not a spacestation.");
                    return false;
                }
                //get connection from this sector
                SpaceStation gate = (SpaceStation) selected;
                Vector3i gateSector = gate.getSector(new Vector3i());
                FTLConnection connection = GameServerState.instance.getUniverse().getGalaxyManager().getFtlData().get(gateSector); //houses list of outgoing connections and their types.
                if (connection == null) {
                    PlayerUtils.sendMessage(admin,"no FTLconnection exist for this sector.");
                    return false;
                }
                ArrayList<Vector3i> gateLines = new ArrayList<>();
                for (int i = 0; i < connection.to.size(); i++) {
                    Vector3i param = connection.param.get(i);
                    if (param.x == FTLConnection.TYPE_WARP_GATE) {
                        Vector3i to = new Vector3i(connection.to.get(i));
                        gateLines.add(to);
                    }
                }
                if (gateLines.isEmpty()) {
                    PlayerUtils.sendMessage(admin,"zero outgoing warpgate connections exist from this sector.");
                    return false;
                }

                //get existing gate
                MapMarker marker = NavigationUtilManager.instance.getPublicMarkers().get(gateSector.code());
                GateMarker gMarker;
                if (!(marker instanceof GateMarker)) {
                    gMarker = new GateMarker(gateSector,gateName,MapIcon.GATE_3,GateMarker.publicGateColor);
                } else {
                    //already exists
                    gMarker = (GateMarker)marker;
                }
                gMarker.getConnectionTargetSectors().addAll(gateLines);
                NavigationUtilManager.instance.addCoordinateToList(gMarker);
                NavigationUtilManager.instance.saveListsPersistent();
                NavigationUtilManager.instance.synchPlayers();
                return true;
            }

            case "list": {
                PlayerUtils.sendMessage(admin,getNavlistString());
                return true;
            }

            case "blue": {
                for (MapMarker marker: NavigationUtilManager.instance.getPublicMarkers().values()) {
                    if (marker.getIcon().subSpriteIndex==3)
                        continue;//magges
                    marker.color = new Vector4f(0,0.667f,1,1);
                }
                NavigationUtilManager.instance.synchPlayers();
            }

            case "synch": {
                NavigationUtilManager.instance.synchPlayers();
                return true;
            }

            case "clear": {
                NavigationUtilManager.instance.getPublicMarkers().clear();
                NavigationUtilManager.instance.saveListsPersistent();
                NavigationUtilManager.instance.synchPlayers();
                PlayerUtils.sendMessage(admin,"deleted all public markers");
                return true;
            }

            case "mock": {
                ArrayList<MapMarker> markers = DebugUtil.mockGateNetwork( new Vector3i(2,2,2),20,420);
                Random rand = new Random(420);
                for (MapMarker m: markers) {
                    //make arm with 20
                    if (rand.nextBoolean()) {
                        //add a splitoff arm to the gate
                        ArrayList<MapMarker> arm = DebugUtil.mockGateNetwork(m.sector, 1+ rand.nextInt(4), rand.nextLong());
                        ((GateMarker)arm.get(0)).addLine(m.sector); //connect to arm start
                        for (MapMarker armM : arm) {
                            NavigationUtilManager.instance.addCoordinateToList(armM);
                        }
                    }
                    NavigationUtilManager.instance.addCoordinateToList(m);
                }
                NavigationUtilManager.instance.saveListsPersistent();
                NavigationUtilManager.instance.synchPlayers();
                return true;
            }
            case "load": {
                NavigationListContainer container = NavigationListContainer.getContainer(false);
                return true;
            }

            case "save": {
                NavigationUtilManager.instance.saveListsPersistent();
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

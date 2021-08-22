package thederpgamer.edencore.manager;

import api.common.GameClient;
import api.common.GameCommon;
import api.common.GameServer;
import org.schema.game.common.Starter;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.controller.SpaceStation;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.schine.resource.FileExt;
import org.schema.schine.resource.tag.Tag;
import thederpgamer.edencore.utils.DataUtils;

import java.io.*;
import java.util.Locale;
import java.util.Objects;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/21/2021
 */
public class TransferManager {

    public static final int NONE = 0;
    public static final int SAVE = 1;
    public static final int LOAD = 2;

    public static int getTransferMode() {
        String s = ConfigManager.getMainConfig().getString("entity-transfer-mode");
        switch(s) {
            case "SAVE": return SAVE;
            case "LOAD": return LOAD;
            default: return NONE;
        }
    }

    public static File getTransferFolder(PlayerState playerState) {
        try {
            if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) {
                File transferFolder = new File(DataUtils.getWorldDataPath() + "/transfer_data/" + playerState.getName());
                if(!transferFolder.exists()) transferFolder.mkdirs();
                return transferFolder;
            } else throw new IllegalAccessException("Cannot access server transfer data for " + playerState.getName() + " as a client.");
        } catch(IllegalAccessException exception) {
            LogManager.logException("Client " + GameClient.getClientPlayerState().getName() + " attempted to illegally access server data.", exception);
        }
        return null;
    }

    public static boolean canTransfer(PlayerState playerState) {
        return playerState.getFirstControlledTransformableWOExc() != null && (playerState.getFirstControlledTransformableWOExc().getType().equals(SimpleTransformableSendableObject.EntityType.SHIP) || playerState.getFirstControlledTransformableWOExc().getType().equals(SimpleTransformableSendableObject.EntityType.SPACE_STATION));
    }

    public static boolean isValidTransfer(PlayerState playerState) {
        return playerState.getFirstControlledTransformableWOExc() != null && (playerState.getFirstControlledTransformableWOExc().getType().equals(SimpleTransformableSendableObject.EntityType.SHIP) && !(((Ship) playerState.getFirstControlledTransformableWOExc()).getSpawner().toLowerCase(Locale.ENGLISH).equals("<system>"))) || (playerState.getFirstControlledTransformableWOExc().getType().equals(SimpleTransformableSendableObject.EntityType.SPACE_STATION) && !(((SpaceStation) playerState.getFirstControlledTransformableWOExc()).getSpawner().toLowerCase(Locale.ENGLISH).equals("<system>")));
    }

    public static void saveEntity(PlayerState playerState, SegmentController entity) throws Exception {
        playerState.getControllerState().forcePlayerOutOfSegmentControllers();
        Tag tag = entity.toTagStructure();
        File transferFolder = getTransferFolder(playerState);
        if(transferFolder != null && transferFolder.isDirectory()) {
            FileExt entityFile = new FileExt(transferFolder,  entity.getUniqueIdentifierFull() + ".ent");
            if(entityFile.exists()) entityFile.delete();
            entityFile.createNewFile();
            tag.writeTo(new BufferedOutputStream(new FileOutputStream(entityFile)), true);
        }
    }

    public static void loadEntity(PlayerState playerState, String entityName) throws Exception {
        File transferFolder = getTransferFolder(playerState);
        FileExt entityFile = null;
        if(transferFolder != null && transferFolder.isDirectory()) {
            if(transferFolder.listFiles() != null && Objects.requireNonNull(transferFolder.listFiles()).length > 0) {
                for(File file : Objects.requireNonNull(transferFolder.listFiles())) {
                    String dbPrefix = file.getName().substring(0, file.getName().indexOf('_'));
                    Tag entityTag = Tag.readFrom(new BufferedInputStream(new FileInputStream(file)), true, false);
                    String realName = ((String) ((Tag[]) entityTag.getValue())[5].getValue());
                    if(realName.toLowerCase().contains(entityName.toLowerCase())) {
                        if(dbPrefix.equals(SimpleTransformableSendableObject.EntityType.SHIP.dbPrefix)) {
                            Ship ship = new Ship(GameServer.getServerState());
                            ship.setId(GameServer.getServerState().getNextFreeObjectId());
                            ship.setSectorId(playerState.getSectorId());
                            ship.dbId = -1L;
                            ship.initialize();
                            ship.fromTagStructure(entityTag);
                            GameServer.getServerState().getController().getSynchController().addNewSynchronizedObjectQueued(ship);
                            Starter.modManager.onSegmentControllerSpawn(ship);
                            //entityFile.delete();
                            return;
                        } else if(dbPrefix.equals(SimpleTransformableSendableObject.EntityType.SPACE_STATION.dbPrefix)) {
                            SpaceStation station = new SpaceStation(GameServer.getServerState());
                            station.setId(GameServer.getServerState().getNextFreeObjectId());
                            station.setSectorId(playerState.getSectorId());
                            station.dbId = -1L;
                            station.initialize();
                            station.fromTagStructure(entityTag);
                            GameServer.getServerState().getController().getSynchController().addNewSynchronizedObjectQueued(station);
                            Starter.modManager.onSegmentControllerSpawn(station);
                            entityFile.delete();
                            return;
                        } else throw new IllegalArgumentException("Entity \"" + entityName + "\" is neither a ship or station and therefore cannot be transferred.");
                    }
                }

                throw new NullPointerException("There is no entity by the name \"" + entityName + "\" saved in the world transfer folder.");
            }
        }
    }
}
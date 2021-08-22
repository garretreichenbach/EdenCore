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
import org.schema.schine.network.StateInterface;
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

    public static void saveEntity(PlayerState playerState) throws Exception {
        SegmentController entity = (SegmentController) playerState.getFirstControlledTransformableWOExc();
        playerState.getControllerState().forcePlayerOutOfSegmentControllers();
        Tag tag = entity.toTagStructure();
        File transferFolder = getTransferFolder(playerState);
        if(transferFolder != null && transferFolder.isDirectory()) {
            File entityFile = new File(transferFolder.getPath() + "/" + entity.getType().ordinal() + "|" + entity.getName() + "|" + entity.getUniqueIdentifier() + ".ent");
            if(!entityFile.exists()) entityFile.createNewFile();
            tag.writeTo(new BufferedOutputStream(new FileOutputStream(entityFile)), true);
        }
    }

    public static void loadEntity(PlayerState playerState, String entityName) throws Exception {
        playerState.getControllerState().forcePlayerOutOfSegmentControllers();
        File transferFolder = getTransferFolder(playerState);
        File entityFile = null;
        int entityType = -1;
        if(transferFolder != null && transferFolder.isDirectory()) {
            if(transferFolder.listFiles() != null && Objects.requireNonNull(transferFolder.listFiles()).length > 0) {
                for(File file : Objects.requireNonNull(transferFolder.listFiles())) {
                    try {
                        entityType = Integer.parseInt(file.getName().substring(file.getName().lastIndexOf('/') + 1, file.getName().indexOf('|') - 1));
                        String name = file.getName().substring(file.getName().indexOf('|') + 1, file.getName().lastIndexOf('|') - 1);
                        if(name.equals(entityName)) {
                            entityFile = file;
                            break;
                        }
                    } catch(Exception ignored) { }
                }
                if(entityFile != null) {
                    Tag tag = Tag.readFrom(new BufferedInputStream(new FileInputStream(entityFile)), true, false);
                    if(entityType == SimpleTransformableSendableObject.EntityType.SHIP.ordinal()) {
                        Ship ship = new Ship((StateInterface) GameCommon.getGameState());
                        ship.setId(((StateInterface) Objects.requireNonNull(GameCommon.getGameState())).getNextFreeObjectId());
                        ship.setSectorId(playerState.getSectorId());
                        ship.dbId = -1L;
                        ship.initialize();
                        ship.fromTagStructure(tag);
                        GameServer.getServerState().getController().getSynchController().addNewSynchronizedObjectQueued(ship);
                        Starter.modManager.onSegmentControllerSpawn(ship);
                        entityFile.delete();
                    } else if(entityType == SimpleTransformableSendableObject.EntityType.SPACE_STATION.ordinal()) {
                        SpaceStation station = new SpaceStation((StateInterface) GameCommon.getGameState());
                        station.setId(((StateInterface) Objects.requireNonNull(GameCommon.getGameState())).getNextFreeObjectId());
                        station.setSectorId(playerState.getSectorId());
                        station.dbId = -1L;
                        station.initialize();
                        station.fromTagStructure(tag);
                        GameServer.getServerState().getController().getSynchController().addNewSynchronizedObjectQueued(station);
                        Starter.modManager.onSegmentControllerSpawn(station);
                        entityFile.delete();
                    } else throw new IllegalArgumentException("Entity \"" + entityName + "\" is neither a ship or station and therefore cannot be transferred.");
                } else throw new NullPointerException("There is no entity by the name \"" + entityName + "\" saved in the world transfer folder.");
            }
        }
    }
}
package thederpgamer.edencore.manager;

import api.common.GameClient;
import api.common.GameCommon;
import api.common.GameServer;
import com.bulletphysics.linearmath.Transform;
import org.apache.commons.io.IOUtils;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.controller.SpaceStation;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.game.server.controller.BluePrintController;
import org.schema.game.server.data.GameServerState;
import org.schema.game.server.data.blueprint.ChildStats;
import org.schema.game.server.data.blueprint.SegmentControllerOutline;
import org.schema.game.server.data.blueprint.SegmentControllerSpawnCallbackDirect;
import org.schema.game.server.data.blueprintnw.BlueprintEntry;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.resource.tag.Tag;
import thederpgamer.edencore.data.EntityHeaderData;
import thederpgamer.edencore.utils.DataUtils;

import javax.vecmath.Vector3f;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.*;

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
                File transferFolder = new File(DataUtils.getWorldDataPath() + "/transfer data/" + playerState.getName());
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
        assert GameCommon.getGameState().isOnServer();
        playerState.getControllerState().forcePlayerOutOfSegmentControllers();
        Tag tag = entity.toTagStructure();
        File transferFolder = getTransferFolder(playerState);
        if(transferFolder != null && transferFolder.isDirectory()) {
            //Todo: Make the file structure into it's own class
            File entityFile = new File(transferFolder.getPath() + "/" + entity.getUniqueIdentifierFull() + ".zip");
            ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(entityFile));

            ZipEntry fileHeader = new ZipEntry("info.dat");
            outputStream.putNextEntry(fileHeader);
            EntityHeaderData headerData = new EntityHeaderData(entity, playerState);
            byte[] data = headerData.serialize();
            outputStream.write(data, 0, data.length);
            outputStream.closeEntry();

            ZipEntry entityHeader = new ZipEntry("header.dat");
            outputStream.putNextEntry(entityHeader);
            tag.writeTo(outputStream, false);
            outputStream.closeEntry();

            ZipEntry entityData = new ZipEntry("entity.dat");
            outputStream.putNextEntry(entityData);
            String name = "entity_temp_" + headerData.saveDate;
            BlueprintEntry blueprintEntry = new BlueprintEntry(name);
            blueprintEntry.write(entity, false);
            File blueprintFile = blueprintEntry.export();
            File temp = new File(transferFolder.getPath() + "/" + name + ".dat");
            Files.move(blueprintFile.toPath(), temp.toPath());
            data = Files.readAllBytes(temp.toPath());
            outputStream.write(data, 0, data.length);
            outputStream.closeEntry();
            outputStream.close();
            blueprintFile.delete();
            temp.delete();
            blueprintEntry.getBbController().removeBluePrint(blueprintEntry);
        }
    }

    public static void loadEntity(PlayerState playerState, String entityName) throws Exception {
        assert GameCommon.getGameState().isOnServer();
        File transferFolder = getTransferFolder(playerState);
        if(transferFolder != null && transferFolder.isDirectory()) {
            if(transferFolder.listFiles() != null && Objects.requireNonNull(transferFolder.listFiles()).length > 0) {
                for(File file : Objects.requireNonNull(transferFolder.listFiles())) {
                    if(file.getName().endsWith(".zip")) {
                        ZipFile zipFile = new ZipFile(file);
                        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
                        InputStream dataStream;

                        ZipEntry fileHeader = zipInputStream.getNextEntry();
                        dataStream = zipFile.getInputStream(fileHeader);
                        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
                        IOUtils.copy(dataStream, byteOutStream);
                        EntityHeaderData headerData = new EntityHeaderData(byteOutStream.toByteArray());
                        //Todo: Do something with the header data
                        dataStream.close();
                        if(headerData.entityName.toLowerCase().contains(entityName.toLowerCase())) {
                            ZipEntry entityHeader = zipInputStream.getNextEntry();
                            dataStream = zipFile.getInputStream(entityHeader);
                            Tag entityTag = Tag.readFrom(dataStream, false, false);
                            dataStream.close();

                            ZipEntry entityData = zipInputStream.getNextEntry();
                            dataStream = zipFile.getInputStream(entityData);
                            File tempFile = new File(transferFolder.getPath() + "/entity_temp_" + headerData.saveDate + ".dat");
                            FileOutputStream outputStream = new FileOutputStream(tempFile);
                            IOUtils.copy(dataStream, outputStream);
                            outputStream.close();
                            File out = BlueprintEntry.importToFile(tempFile, BluePrintController.active);
                            String importName = out.getName();
                            dataStream.close();
                            zipInputStream.close();
                            List<BlueprintEntry> bpList = BluePrintController.active.readBluePrints();
                            for(BlueprintEntry entry : bpList) {
                                if(entry.getName().equals(importName)) {
                                    Transform transform = new Transform();
                                    transform.setIdentity();
                                    transform.origin.set(playerState.getFirstControlledTransformableWOExc().getWorldTransform().origin);
                                    Vector3f forward = GlUtil.getForwardVector(new Vector3f(), transform);
                                    Vector3f size = headerData.size;
                                    size.scale(0.5f);
                                    forward.scaleAdd(1.15f, size);
                                    transform.origin.set(forward);
                                    SegmentControllerOutline<?> outline = BluePrintController.active.loadBluePrint(
                                            GameServerState.instance,
                                            entry.getName(),
                                            entityName,
                                            transform,
                                            -1,
                                            playerState.getFactionId(),
                                            playerState.getCurrentSector(),
                                            playerState.getName(),
                                            PlayerState.buffer,
                                            null,
                                            false,
                                            new ChildStats(false));
                                    SegmentController entity = outline.spawn(playerState.getCurrentSector(), false, new ChildStats(false), new SegmentControllerSpawnCallbackDirect(GameServer.getServerState(), playerState.getCurrentSector()) {
                                        @Override
                                        public void onNoDocker() {

                                        }
                                    });
                                    entity.fromTagStructure(entityTag);
                                    tempFile.delete();
                                    out.delete();
                                    file.delete();
                                    entry.getBbController().removeBluePrint(entry);
                                    return;
                                }
                            }
                            throw new IllegalStateException("Failed to find blueprint \"" + entityName + "\" even though it was just imported!");
                        }
                    }
                }
                throw new NullPointerException("There is no entity by the name \"" + entityName + "\" saved in the world transfer folder.");
            }
        }
    }

    public static HashMap<String, File> getSavedEntities(PlayerState playerState) {
        HashMap<String, File> savedEntities = new HashMap<>();
        File transferFolder = getTransferFolder(playerState);
        if(transferFolder != null && transferFolder.isDirectory()) {
            for(File file : Objects.requireNonNull(transferFolder.listFiles())) {
                if(file.getName().endsWith(".zip")) {
                    try {
                        ZipFile zipFile = new ZipFile(file);
                        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
                        InputStream dataStream;

                        ZipEntry fileHeader = zipInputStream.getNextEntry();
                        dataStream = zipFile.getInputStream(fileHeader);
                        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
                        IOUtils.copy(dataStream, byteOutStream);
                        EntityHeaderData headerData = new EntityHeaderData(byteOutStream.toByteArray());
                        byteOutStream.close();
                        dataStream.close();
                        zipInputStream.close();
                        savedEntities.put(headerData.entityName, file);
                    } catch(IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
        return savedEntities;
    }

    public static String getSavedEntitiesList(PlayerState playerState) {
        HashMap<String, File> savedEntities = getSavedEntities(playerState);
        StringBuilder builder = new StringBuilder();
        builder.append("Saved Entities for ").append(playerState.getName()).append(":\n");
        for(String name : savedEntities.keySet()) builder.append(name).append("\n");
        return builder.toString().trim();
    }
}
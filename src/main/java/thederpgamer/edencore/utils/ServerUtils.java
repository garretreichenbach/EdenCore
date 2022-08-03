package thederpgamer.edencore.utils;

import api.common.GameCommon;
import api.common.GameServer;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.database.DatabaseIndex;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.physics.CubeRayCastResult;
import org.schema.game.common.data.physics.PhysicsExt;
import org.schema.game.common.data.player.AbstractCharacter;
import org.schema.game.common.data.player.PlayerControlledTransformableNotFound;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.world.Sector;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.game.server.data.GameServerState;
import org.schema.game.server.data.PlayerNotFountException;
import org.schema.game.server.data.ServerConfig;
import org.schema.schine.graphicsengine.core.GlUtil;
import org.schema.schine.resource.FileExt;
import org.schema.schine.resource.tag.Tag;
import thederpgamer.edencore.manager.LogManager;

import javax.vecmath.Vector3f;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/19/2021]
 */
public class ServerUtils {

    public enum EntryType {
        PLAYER_STATE("ENTITY_PLAYERSTATE");

        public String databasePrefix;

        EntryType(String databasePrefix) {
            this.databasePrefix = databasePrefix;
        }
    }

    public static SegmentController getEntityFromUID(String entityUID) {
        if(GameCommon.isOnSinglePlayer() || GameCommon.isDedicatedServer()) {
            return GameServer.getServerState().getSegmentControllersByName().get(entityUID);
        } else return null;
    }

    public static void clearInventoryFull(PlayerState playerState) {
        playerState.getPersonalFactoryInventoryMicro().clear();
        playerState.getPersonalFactoryInventoryMicro().sendAll();

        playerState.getPersonalFactoryInventoryCapsule().clear();
        playerState.getPersonalFactoryInventoryCapsule().sendAll();

        playerState.getPersonalFactoryInventoryMacroBlock().clear();
        playerState.getPersonalFactoryInventoryMacroBlock().sendAll();

        playerState.getPersonalInventory().clear();
        playerState.getPersonalInventory().sendAll();

        if(!playerState.getInventory().isInfinite()) {
            playerState.getInventory().clear();
            playerState.getInventory().sendAll();
        }
    }

    /**
     * Searches for a player by name on server. If the player isn't online, searches the server database for a matching entry.
     * @param playerName The player's name
     * @return The player state
     */
    public static PlayerState getPlayerByName(String playerName) {
        assert GameCommon.isOnSinglePlayer() || GameCommon.isDedicatedServer();
        PlayerState playerState = GameServer.getServerState().getPlayerStatesByName().get(playerName);
        if(playerState == null) {
            try {
                HashMap<String, Tag> entries = getEntriesOfType(EntryType.PLAYER_STATE);
                if(!entries.isEmpty() && entries.containsKey(playerName)) {
                    String[] fields = getPlayerDatabaseFields(playerName);
                    playerState = new PlayerState(GameServer.getServerState());
                    playerState.initialize();
                    playerState.fromTagStructure(entries.get(playerName));
                    playerState.setId(Integer.parseInt(fields[0].trim()));
                    playerState.setName(playerName);
                    playerState.setStarmadeName(fields[2]);
                    playerState.getFactionController().setFactionId(Integer.parseInt(fields[3].trim()));
                    playerState.offlinePermssion[0] = playerState.getFactionId();
                    playerState.offlinePermssion[1] = Long.parseLong(fields[4].trim());
                }
            } catch(Exception exception) {
                LogManager.logException("Encountered an exception while trying to fetch a player from database", exception);
            }
        }
        return playerState;
    }

    public static SegmentPiece getBlockLookingAt(GameServerState state, PlayerState player) throws IOException, PlayerNotFountException, PlayerControlledTransformableNotFound {
        Vector3f pos;
        Vector3f to;
        SimpleTransformableSendableObject firstControlledTransformable = player.getFirstControlledTransformable();
        if(player.getNetworkObject().isInBuildMode.getBoolean()) {
            pos = new Vector3f(player.getBuildModePosition().getWorldTransform().origin);
            Vector3f forw = GlUtil.getForwardVector(new Vector3f(), player.getBuildModePosition().getWorldTransform());
            forw.scale(5000);
            to = new Vector3f(pos);
            to.add(forw);
        } else {
            if(firstControlledTransformable instanceof AbstractCharacter<?>) pos = new Vector3f(((AbstractCharacter<?>) firstControlledTransformable).getHeadWorldTransform().origin);
            else pos = new Vector3f(firstControlledTransformable.getWorldTransform().origin);
            to = new Vector3f(pos);
            Vector3f forw = player.getForward(new Vector3f());
            forw.scale(5000);
            to.add(forw);

        }
        Sector sector = state.getUniverse().getSector(firstControlledTransformable.getSectorId());
        CollisionWorld.ClosestRayResultCallback testRayCollisionPoint = ((PhysicsExt) sector.getPhysics()).testRayCollisionPoint(pos, to, false, null, null, false, true, false);
        if(testRayCollisionPoint.hasHit() && testRayCollisionPoint instanceof CubeRayCastResult && ((CubeRayCastResult)testRayCollisionPoint).getSegment() != null) {
            CubeRayCastResult c = (CubeRayCastResult) testRayCollisionPoint;
            SegmentPiece p = new SegmentPiece(c.getSegment(), c.getCubePos());
            if(p.isValid() && p.getInfo().isRailDockable()) return p;
        }
        return null;
    }

    public static ArrayList<PlayerState> getAllPlayers() {
        assert GameCommon.isOnSinglePlayer() || GameCommon.isDedicatedServer();
        ArrayList<PlayerState> allPlayers = new ArrayList<>(GameServer.getServerState().getPlayerStatesByName().values());
        try {
            HashMap<String, Tag> entries = getEntriesOfType(EntryType.PLAYER_STATE);
            for(Map.Entry<String, Tag> entry : entries.entrySet()) {
                String[] fields = getPlayerDatabaseFields(entry.getKey());
                PlayerState playerState = new PlayerState(GameServer.getServerState());
                playerState.initialize();
                playerState.fromTagStructure(entry.getValue());
                playerState.setName(entry.getKey());
                playerState.setStarmadeName(fields[2]);
                playerState.getFactionController().setFactionId(Integer.parseInt(fields[3].trim()));
                playerState.offlinePermssion[0] = playerState.getFactionId();
                playerState.offlinePermssion[1] = Long.parseLong(fields[4].trim());
                if(!allPlayers.contains(playerState)) allPlayers.add(playerState);
            }
        } catch(Exception exception) {
            LogManager.logException("Encountered an exception while trying to fetch players from database", exception);
        }
        return allPlayers;
    }

    public static HashMap<String, Tag> getEntriesOfType(final EntryType entryType) {
        assert GameCommon.isOnSinglePlayer() || GameCommon.isDedicatedServer();
        File databaseFolder = new FileExt(GameServerState.ENTITY_DATABASE_PATH);
        if(databaseFolder.exists()) {
            try {
                File[] listFiles = databaseFolder.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File arg0, String name) {
                        return name.startsWith(entryType.databasePrefix);
                    }
                });

                if(listFiles != null) {
                    HashMap<String, Tag> entryMap = new HashMap<>();
                    for(File listFile : listFiles) {
                        String name = listFile.getName().substring(entryType.databasePrefix.length(), listFile.getName().lastIndexOf("."));
                        Tag tag = Tag.readFrom(new BufferedInputStream(new FileInputStream(listFile)), true, false);
                        entryMap.put(name, tag);
                    }
                    return entryMap;
                }
            } catch(Exception exception) {
                LogManager.logException("Encountered an exception while trying to fetch server database entries", exception);
            }
        }
        return new HashMap<>();
    }

    public static String[] getPlayerDatabaseFields(String playerName) {
        assert GameCommon.isOnSinglePlayer() || GameCommon.isDedicatedServer();
        String[] fields = new String[5];
        try {
            String maxNIOSize = ";hsqldb.nio_max_size=" + ServerConfig.SQL_NIO_FILE_SIZE.getCurrentState() + ";";
            Connection c = DriverManager.getConnection("jdbc:hsqldb:file:" + DatabaseIndex.getDbPath() + ";shutdown=true" + maxNIOSize, "SA", "");
            Statement s = c.createStatement();
            ResultSet q = s.executeQuery("SELECT * FROM PLAYERS;");
            while(q.next()) {
                String name = q.getString(1);
                if(name.equals(playerName)) {
                    fields = new String[] {
                            String.valueOf(q.getInt(0)),
                            name,
                            q.getString(2),
                            String.valueOf(q.getInt(3)),
                            String.valueOf(q.getLong(4))
                    };
                }
            }
        } catch(SQLException exception) {
            exception.printStackTrace();
        }
        return fields;
    }
}
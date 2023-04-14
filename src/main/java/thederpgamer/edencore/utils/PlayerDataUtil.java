package thederpgamer.edencore.utils;

import api.utils.game.PlayerUtils;
import api.utils.game.inventory.InventoryUtils;
import org.schema.game.common.controller.database.DatabaseIndex;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.updater.FileUtil;
import org.schema.game.server.controller.EntityNotFountException;
import org.schema.game.server.data.GameServerState;
import org.schema.game.server.data.PlayerNotFountException;
import org.schema.game.server.data.ServerConfig;
import org.schema.schine.resource.FileExt;
import org.schema.schine.resource.tag.Tag;

import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 20.09.2021
 * TIME: 20:02
 * helper class to access the server database and player .ent files
 */
public class PlayerDataUtil {
	/**
	 * will read all players from the servers database, return all names.
	 *
	 * @return
	 * @throws SQLException
	 */
	public static ArrayList<String> getAllPlayerNamesEver() throws SQLException {
		//open connection
		String maxNIOSize = ";hsqldb.nio_max_size=" + ServerConfig.SQL_NIO_FILE_SIZE.getCurrentState() + ";";
		Connection c = DriverManager.getConnection("jdbc:hsqldb:file:" + DatabaseIndex.getDbPath() + ";shutdown=true" + maxNIOSize, "SA", "");
		//make statement
		Statement s = c.createStatement();
		ResultSet q = s.executeQuery("SELECT NAME FROM PLAYERS;");
		ArrayList<String> names = new ArrayList<>();
		while(q.next()) { //iterate over all rows
			names.add(q.getString(1));
		}
		return names;
	}

	public static void addBars(String seller, short barType, int amount) {
		try {
			PlayerState playerState = GameServerState.instance.getPlayerFromName(seller);
			InventoryUtils.addItem(playerState.getInventory(), barType, amount);
			PlayerUtils.sendMessage(playerState, "[COMMUNITY MARKET]: Sold a blueprint for " + amount + " bars.");
		} catch(PlayerNotFountException ignored) {
			try { //Seller not online, use database instead
				PlayerState playerState = loadControlPlayer(seller);
				InventoryUtils.addItem(playerState.getInventory(), barType, amount);
				writeTagForPlayer(playerState.toTagStructure(), seller);
			} catch(Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	/**
	 * will load a playerstate from the names tag structure(savefile). only tag values will be set. pure debug.
	 *
	 * @param playername
	 *
	 * @return
	 */
	public static PlayerState loadControlPlayer(String playername) {
		PlayerState p = new PlayerState(GameServerState.instance);
		try {
			Tag t = getTagFromFile(playername);
			p.fromTagStructure(t);
			return p;
		} catch(IOException | EntityNotFountException exception) {
			exception.printStackTrace();
		}
		return null;
	}

	/**
	 * stolen from EntitiyFileTools.write, modified
	 * will write the tag of given player to the players.ent file (overwriting its persistent save data)
	 *
	 * @param tagStructure
	 *
	 * @throws IOException
	 */
	public static void writeTagForPlayer(final Tag tagStructure, final String playerName) throws IOException {
		final String playerUID = "ENTITY_PLAYERSTATE_" + playerName; //todo
		final String fileName = playerUID + ".ent";
		//unchanging params moved out of method params :
		final HashMap<String, Object> locks = GameServerState.fileLocks;
		final String pathRaw = GameServerState.ENTITY_DATABASE_PATH;
		//create .ent file path
		String path = pathRaw;
		if(!path.endsWith("/")) {
			path += "/";
		}
		//synchronized == atomic, multi thread safe
		Object lock;
		synchronized(locks) {
			lock = locks.get(fileName);
			if(lock == null) {
				lock = new Object();
				locks.put(fileName, lock);
			}
		}
		synchronized(lock) {
			//write to a temporary file
			File tmpFile = new FileExt(path + "tmp/" + fileName + ".tmp");
			if(tmpFile.exists()) {
				System.err.println("Exception: tmpFile of " + pathRaw + " still exists"); //TODO playername
				tmpFile.delete();
			}
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(tmpFile.toPath()), 4096);
			DataOutputStream os = new DataOutputStream(bufferedOutputStream);
			try {
				if(tagStructure.getType() != Tag.Type.STRUCT || tagStructure.getValue() == null || ((Tag[]) tagStructure.getValue()).length < 1) {
					os.close();
					throw new IllegalArgumentException("serialization of " + playerName + " failed, and will not be written because it could lead to corruption. Please send in a report!");
				}
				tagStructure.writeTo(os, true);
				os.close();
			} catch(RuntimeException e) {
				System.err.println("Exception during write of: " + playerName);
				try {
					os.close();
				} catch(Exception es) {
					es.printStackTrace();
				}
				throw e;
			}
			//overwrite actual savefile with temporary
			File over = new FileExt(path + fileName);
			File old = new FileExt(path + fileName + ".old");
			if(over.exists()) {
				if(old.exists()) {
					System.err.println("Exception: tried parallel write off OLD: " + old.getName()); //is this some legacy shit?
					old.delete();
				}
				FileUtil.copyFile(over, old);
				boolean deleted = over.delete();
				System.err.println("[SERVER] DELETING ORIGINALFILE TO REPLACE WITH NEW ONE: " + deleted + "; " + over.getAbsolutePath());
			}
			FileUtil.copyFile(tmpFile, over);
			tmpFile.delete();
			if(old.exists()) {
				old.delete();
			}
			System.err.println("[SERVER] CHECKING IF FILE EXISTS " + over.exists());
			if(!over.exists()) {
				throw new FileNotFoundException(over.getAbsolutePath());
			}
		}
		File check = new FileExt(path + fileName);
		if(!check.exists()) {
			throw new FileNotFoundException("ERROR WRITING FILE: " + check.getAbsolutePath());
		}
	}

	/**
	 * (attempts to) read the savefile for this player.
	 *
	 * @param playername
	 *
	 * @return Tag of that player. contains tag[] with save categories + values
	 * @throws IOException
	 * @throws EntityNotFountException
	 */
	public static Tag getTagFromFile(String playername) throws IOException, EntityNotFountException {
		String pUID = "ENTITY_PLAYERSTATE_" + playername;
		return GameServerState.instance.getController().readEntity(pUID); //reads the .ent file with that UID
	}
}

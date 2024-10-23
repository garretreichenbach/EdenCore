package thederpgamer.edencore.data.buildsectordata;

import api.common.GameCommon;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.controller.SpaceStation;
import org.schema.game.common.controller.ai.Types;
import org.schema.game.common.controller.rails.RailRelation;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.faction.FactionManager;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.game.server.data.blueprintnw.BlueprintEntry;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.SerializableData;
import thederpgamer.edencore.data.playerdata.PlayerData;
import thederpgamer.edencore.data.playerdata.PlayerDataManager;
import thederpgamer.edencore.utils.EntityUtils;
import thederpgamer.edencore.utils.PlayerUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class BuildSectorData extends SerializableData {

	public static final int OWNER = 0;
	public static final int FRIEND = 1;
	public static final int OTHER = 2;
	private static final byte VERSION = 0;

	protected String owner;
	protected Vector3i sector;
	protected Set<BuildSectorEntityData> entities = new HashSet<>();
	protected HashMap<String, HashMap<PermissionTypes, Boolean>> permissions = new HashMap<>();

	public BuildSectorData(String owner) {
		super(DataType.BUILD_SECTOR_DATA);
		this.owner = owner;
		sector = BuildSectorDataManager.calculateRandomSector();
		setDefaultPerms(owner, OWNER);
	}

	public BuildSectorData(PacketReadBuffer readBuffer) throws IOException {
		super(readBuffer);
	}

	public BuildSectorData(JSONObject data) {
		super(data);
	}

	@Override
	public JSONObject serialize() {
		JSONObject data = new JSONObject();
		data.put("version", VERSION);
		data.put("uuid", getUUID());
		data.put("owner", owner);
		data.put("sector", sector.toString());
		JSONArray entitiesArray = new JSONArray();
		for(BuildSectorEntityData entity : entities) entitiesArray.put(entity.serialize());
		data.put("entities", entitiesArray);
		JSONArray permissionsArray = new JSONArray();
		for(String name : permissions.keySet()) {
			for(Map.Entry<PermissionTypes, Boolean> permission : permissions.get(name).entrySet()) {
				JSONObject permissionData = new JSONObject();
				permissionData.put("name", name);
				permissionData.put(permission.getKey().getKey(), permission.getValue());
				permissionsArray.put(permissionData);
			}
		}
		data.put("permissions", permissionsArray);
		return data;
	}

	@Override
	public void deserialize(JSONObject data) {
		byte version = (byte) data.getInt("version");
		dataUUID = data.getString("uuid");
		owner = data.getString("owner");
		sector = new Vector3i(Vector3i.parseVector3i(data.getString("sector")));
		JSONArray entitiesArray = data.getJSONArray("entities");
		for(int i = 0; i < entitiesArray.length(); i++) entities.add(new BuildSectorEntityData(entitiesArray.getJSONObject(i)));
		JSONArray permissionsArray = data.getJSONArray("permissions");
		for(int i = 0; i < permissionsArray.length(); i++) {
			JSONObject permissionData = permissionsArray.getJSONObject(i);
			String name = permissionData.getString("name");
			for(PermissionTypes type : PermissionTypes.values()) {
				if(permissionData.has(type.getKey())) {
					boolean value = permissionData.getBoolean(type.getKey());
					HashMap<PermissionTypes, Boolean> permission = new HashMap<>();
					permission.put(type, value);
					permissions.put(name, permission);
				}
			}
		}
	}

	@Override
	public void serializeNetwork(PacketWriteBuffer writeBuffer) throws IOException {
		writeBuffer.writeByte(VERSION);
		writeBuffer.writeString(dataUUID);
		writeBuffer.writeString(owner);
		writeBuffer.writeString(sector.toStringPure());
		writeBuffer.writeInt(entities.size());
		for(BuildSectorEntityData entity : entities) entity.serializeNetwork(writeBuffer);
		writeBuffer.writeInt(permissions.size());
		for(String name : permissions.keySet()) {
			for(Map.Entry<PermissionTypes, Boolean> permission : permissions.get(name).entrySet()) {
				writeBuffer.writeString(name);
				writeBuffer.writeString(permission.getKey().getKey());
				writeBuffer.writeBoolean(permission.getValue());
			}
		}
	}

	@Override
	public void deserializeNetwork(PacketReadBuffer readBuffer) throws IOException {
		byte version = readBuffer.readByte();
		dataUUID = readBuffer.readString();
		owner = readBuffer.readString();
		sector = new Vector3i(Vector3i.parseVector3i(readBuffer.readString()));
		int entityCount = readBuffer.readInt();
		for(int i = 0; i < entityCount; i++) entities.add(new BuildSectorEntityData(readBuffer));
		int permissionCount = readBuffer.readInt();
		for(int i = 0; i < permissionCount; i++) {
			String name = readBuffer.readString();
			PermissionTypes type = PermissionTypes.valueOf(readBuffer.readString());
			boolean value = readBuffer.readBoolean();
			HashMap<PermissionTypes, Boolean> permission = new HashMap<>();
			permission.put(type, value);
			permissions.put(name, permission);
		}
	}

	public Vector3i getSector() {
		return sector;
	}

	public String getOwner() {
		return owner;
	}

	public PlayerData getOwnerData(boolean server) {
		return PlayerDataManager.getInstance().getFromName(owner, server);
	}

	public void addPlayer(String name, int type, boolean server) {
		setDefaultPerms(name, type);
		BuildSectorDataManager.getInstance().updateData(this, server);
	}

	public void removePlayer(String name, boolean server) {
		permissions.remove(name);
		BuildSectorDataManager.getInstance().updateData(this, server);
	}

	public boolean getPermission(String user, PermissionTypes type) {
		HashMap<PermissionTypes, Boolean> permissionMap = permissions.get(user);
		if(permissionMap != null) {
			Boolean value = permissionMap.get(type);
			if(value != null) return value;
			else return false;
		} else return false;
	}

	public void setPermission(String user, PermissionTypes type, boolean value, boolean server) {
		HashMap<PermissionTypes, Boolean> permissionMap = permissions.get(user);
		if(permissionMap != null) permissionMap.put(type, value);
		else {
			HashMap<PermissionTypes, Boolean> newPermissionMap = new HashMap<>();
			newPermissionMap.put(type, value);
			permissions.put(user, newPermissionMap);
		}
		BuildSectorDataManager.getInstance().updateData(this, server);
	}

	public boolean getPermissionForEntity(String user, int entityId, PermissionTypes... types) {
		BuildSectorEntityData entityData = getEntity((SegmentController) GameCommon.getGameObject(entityId));
		if(entityData != null) {
			for(PermissionTypes type : types) {
				if(entityData.getPermission(user, type)) return true;
			}
		}
		return false;
	}

	public boolean getPermissionForEntityOrGlobal(String user, int entityId, PermissionTypes type) {
		SegmentController entity = (SegmentController) GameCommon.getGameObject(entityId);
		switch(type) {
			case EDIT_SPECIFIC:
				return getPermissionForEntity(user, entityId, PermissionTypes.EDIT_SPECIFIC, PermissionTypes.EDIT_ANY) || (getPermission(user, PermissionTypes.EDIT_OWN) && entity.getSpawner().equals(user));
			case DELETE_SPECIFIC:
				return getPermissionForEntity(user, entityId, PermissionTypes.DELETE_SPECIFIC, PermissionTypes.DELETE_ANY) || (getPermission(user, PermissionTypes.DELETE_OWN) && entity.getSpawner().equals(user));
			case TOGGLE_AI_SPECIFIC:
				return getPermissionForEntity(user, entityId, PermissionTypes.TOGGLE_AI_SPECIFIC, PermissionTypes.TOGGLE_AI_ANY) || (getPermission(user, PermissionTypes.TOGGLE_AI_OWN) && entity.getSpawner().equals(user));
			case TOGGLE_DAMAGE_SPECIFIC:
				return getPermissionForEntity(user, entityId, PermissionTypes.TOGGLE_DAMAGE_SPECIFIC, PermissionTypes.TOGGLE_DAMAGE_ANY) || (getPermission(user, PermissionTypes.TOGGLE_DAMAGE_OWN) && entity.getSpawner().equals(user));
			case EDIT_ENTITY_PERMISSIONS:
				return getPermissionForEntity(user, entityId, PermissionTypes.EDIT_ENTITY_PERMISSIONS) || getPermission(user, PermissionTypes.EDIT_PERMISSIONS);
			default:
				return getPermissionForEntity(user, entityId, type);
		}
	}

	public Set<BuildSectorEntityData> getEntities() {
		return new HashSet<>(entities);
	}

	public BuildSectorEntityData getEntity(SegmentController entity) {
		for(BuildSectorEntityData entityData : entities) {
			if(entityData.getEntity().equals(entity)) return entityData;
		}
		return null;
	}

	public void addEntity(SegmentController entity, boolean server) {
		entities.add(new BuildSectorEntityData(entity));
		BuildSectorDataManager.getInstance().updateData(this, server);
	}

	public void removeEntity(SegmentController entity, boolean server) {
		entities.removeIf(entityData -> entityData.getEntity().equals(entity));
		BuildSectorDataManager.getInstance().updateData(this, server);
	}

	public void updateEntity(SegmentController entity, boolean server) {
		BuildSectorEntityData entityData = getEntity(entity);
		if(entityData == null) addEntity(entity, server);
		else {
			entityData.entityID = entity.getId();
			entityData.entityType = EntityType.fromEntity(entity);
			BuildSectorDataManager.getInstance().updateData(this, server);
		}
	}

	public void spawnEntity(BlueprintEntry blueprint, PlayerState spawner, boolean onDock, String name) {
		spawnEntity(blueprint, spawner, onDock, name, spawner.getFactionId());
	}

	public void spawnEntity(BlueprintEntry blueprint, PlayerState spawner, boolean onDock, String name, int factionId) {
		assert spawner.isOnServer() : "Cannot spawn entity on client";
		try {
			SegmentPiece dockPiece = onDock ? PlayerUtils.getBlockLookingAt(spawner) : null;
			SegmentController entity = EntityUtils.spawnEntryOnDock(spawner, blueprint, name, factionId, dockPiece);
			addEntity(entity, true);
			if(factionId == FactionManager.PIRATES_ID) toggleEntityAI(entity, false);
		} catch(Exception exception) {
			EdenCore.getInstance().logException("An error occurred while spawning entity", exception);
		}
	}

	public void toggleEntityAI(SegmentController entity, boolean value) {
		EntityUtils.toggleAI(entity, value);
	}

	private void setDefaultPerms(String user, int type) {
		switch(type) {
			case OWNER:
				permissions.put(user, new HashMap<PermissionTypes, Boolean>() {{
					put(PermissionTypes.EDIT_OWN, true);
					put(PermissionTypes.EDIT_ANY, true);
					put(PermissionTypes.SPAWN, true);
					put(PermissionTypes.SPAWN_ENEMIES, true);
					put(PermissionTypes.DELETE_OWN, true);
					put(PermissionTypes.DELETE_ANY, true);
					put(PermissionTypes.DELETE_ALL, true);
					put(PermissionTypes.TOGGLE_AI_OWN, true);
					put(PermissionTypes.TOGGLE_AI_ANY, true);
					put(PermissionTypes.TOGGLE_AI_ALL, true);
					put(PermissionTypes.TOGGLE_DAMAGE_OWN, true);
					put(PermissionTypes.TOGGLE_DAMAGE_ANY, true);
					put(PermissionTypes.TOGGLE_DAMAGE_ALL, true);
					put(PermissionTypes.INVITE, true);
					put(PermissionTypes.KICK, true);
					put(PermissionTypes.EDIT_PERMISSIONS, true);
				}});
				break;
			case FRIEND:
				permissions.put(user, new HashMap<PermissionTypes, Boolean>() {{
					put(PermissionTypes.EDIT_OWN, true);
					put(PermissionTypes.EDIT_ANY, true);
					put(PermissionTypes.SPAWN, true);
					put(PermissionTypes.SPAWN_ENEMIES, false);
					put(PermissionTypes.DELETE_OWN, true);
					put(PermissionTypes.DELETE_ANY, false);
					put(PermissionTypes.DELETE_ALL, false);
					put(PermissionTypes.TOGGLE_AI_OWN, true);
					put(PermissionTypes.TOGGLE_AI_ANY, false);
					put(PermissionTypes.TOGGLE_AI_ALL, false);
					put(PermissionTypes.TOGGLE_DAMAGE_OWN, true);
					put(PermissionTypes.TOGGLE_DAMAGE_ANY, false);
					put(PermissionTypes.TOGGLE_DAMAGE_ALL, false);
					put(PermissionTypes.INVITE, true);
					put(PermissionTypes.KICK, false);
					put(PermissionTypes.EDIT_PERMISSIONS, false);
				}});
				break;
			case OTHER:
				permissions.put(user, new HashMap<PermissionTypes, Boolean>() {{
					put(PermissionTypes.EDIT_OWN, true);
					put(PermissionTypes.EDIT_ANY, false);
					put(PermissionTypes.SPAWN, true);
					put(PermissionTypes.SPAWN_ENEMIES, false);
					put(PermissionTypes.DELETE_OWN, false);
					put(PermissionTypes.DELETE_ANY, false);
					put(PermissionTypes.DELETE_ALL, false);
					put(PermissionTypes.TOGGLE_AI_OWN, false);
					put(PermissionTypes.TOGGLE_AI_ANY, false);
					put(PermissionTypes.TOGGLE_AI_ALL, false);
					put(PermissionTypes.TOGGLE_DAMAGE_OWN, false);
					put(PermissionTypes.TOGGLE_DAMAGE_ANY, false);
					put(PermissionTypes.TOGGLE_DAMAGE_ALL, false);
					put(PermissionTypes.INVITE, false);
					put(PermissionTypes.KICK, false);
					put(PermissionTypes.EDIT_PERMISSIONS, false);
				}});
				break;
		}
	}

	public Set<String> getAllUsers() {
		return new HashSet<>(permissions.keySet());
	}

	public HashMap<PermissionTypes, Boolean> getPermissionsForEntity(int entityID, String username) {
		BuildSectorEntityData entityData = getEntity((SegmentController) GameCommon.getGameObject(entityID));
		if(entityData != null) return entityData.permissions.get(username);
		else return null;
	}

	public HashMap<PermissionTypes, Boolean> getPermissionsForUser(String username) {
		return permissions.get(username);
	}

	public void setPermissionForEntity(int entityID, String username, PermissionTypes type, boolean value, boolean server) {
		BuildSectorEntityData entityData = getEntity((SegmentController) GameCommon.getGameObject(entityID));
		if(entityData != null) entityData.setPermission(username, type, value, server);
	}

	public BuildSectorEntityData getEntityByID(int id) {
		for(BuildSectorEntityData entityData : entities) {
			if(entityData.getEntityID() == id) return entityData;
		}
		return null;
	}

	public enum EntityType {
		SHIP,
		STATION,
		DOCKED,
		TURRET;

		public static EntityType fromEntity(SegmentController entity) {
			if(entity.getType() == SimpleTransformableSendableObject.EntityType.SHIP) {
				if(entity.railController.isTurretDocked()) return TURRET;
				else if(entity.isDocked()) return DOCKED;
				else return SHIP;
			} else return STATION;
		}
	}

	public class BuildSectorEntityData extends SerializableData {

		private static final byte VERSION = 0;

		private int entityID;
		private EntityType entityType;
		protected final HashMap<String, HashMap<PermissionTypes, Boolean>> permissions = new HashMap<>();

		public BuildSectorEntityData(SegmentController entity) {
			super(DataType.BUILD_SECTOR_ENTITY_DATA);
			entityID = entity.getId();
			entityType = EntityType.fromEntity(entity);
			setDefaultEntityPerms(entity.getSpawner(), OWNER);
		}

		public BuildSectorEntityData(PacketReadBuffer readBuffer) throws IOException {
			super(readBuffer);
		}

		public BuildSectorEntityData(JSONObject data) {
			super(data);
		}

		@Override
		public JSONObject serialize() {
			JSONObject data = new JSONObject();
			data.put("version", VERSION);
			data.put("uuid", getUUID());
			data.put("entityID", entityID);
			data.put("entityType", entityType.name());
			JSONArray permissionsArray = new JSONArray();
			for(String name : permissions.keySet()) {
				for(Map.Entry<PermissionTypes, Boolean> permission : permissions.get(name).entrySet()) {
					JSONObject permissionData = new JSONObject();
					permissionData.put("name", name);
					permissionData.put(permission.getKey().getKey(), permission.getValue());
					permissionsArray.put(permissionData);
				}
			}
			data.put("permissions", permissionsArray);
			return data;
		}

		@Override
		public void deserialize(JSONObject data) {
			byte version = (byte) data.getInt("version");
			dataUUID = data.getString("uuid");
			entityID = data.getInt("entityID");
			entityType = EntityType.valueOf(data.getString("entityType"));
			JSONArray permissionsArray = data.getJSONArray("permissions");
			for(int i = 0; i < permissionsArray.length(); i++) {
				JSONObject permissionData = permissionsArray.getJSONObject(i);
				String name = permissionData.getString("name");
				for(PermissionTypes type : PermissionTypes.values()) {
					if(permissionData.has(type.getKey())) {
						boolean value = permissionData.getBoolean(type.getKey());
						HashMap<PermissionTypes, Boolean> permission = new HashMap<>();
						permission.put(type, value);
						permissions.put(name, permission);
					}
				}
			}
		}

		@Override
		public void serializeNetwork(PacketWriteBuffer writeBuffer) throws IOException {
			writeBuffer.writeByte(VERSION);
			writeBuffer.writeString(dataUUID);
			writeBuffer.writeInt(entityID);
			writeBuffer.writeString(entityType.name());
			writeBuffer.writeInt(permissions.size());
			for(String name : permissions.keySet()) {
				for(Map.Entry<PermissionTypes, Boolean> permission : permissions.get(name).entrySet()) {
					writeBuffer.writeString(name);
					writeBuffer.writeString(permission.getKey().getKey());
					writeBuffer.writeBoolean(permission.getValue());
				}
			}
		}

		@Override
		public void deserializeNetwork(PacketReadBuffer readBuffer) throws IOException {
			byte version = readBuffer.readByte();
			dataUUID = readBuffer.readString();
			entityID = readBuffer.readInt();
			entityType = EntityType.valueOf(readBuffer.readString());
			int permissionCount = readBuffer.readInt();
			for(int i = 0; i < permissionCount; i++) {
				String name = readBuffer.readString();
				PermissionTypes type = PermissionTypes.valueOf(readBuffer.readString());
				boolean value = readBuffer.readBoolean();
				HashMap<PermissionTypes, Boolean> permission = new HashMap<>();
				permission.put(type, value);
				permissions.put(name, permission);
			}
		}

		private void setDefaultEntityPerms(String user, int type) {
			//These permissions are specific to an entity and are stored in the entity object itself rather than the sector object
			//If set, these will override "global" build sector permissions
			switch(type) {
				case OWNER:
					permissions.put(user, new HashMap<PermissionTypes, Boolean>() {{
						put(PermissionTypes.EDIT_SPECIFIC, true);
						put(PermissionTypes.DELETE_SPECIFIC, true);
						put(PermissionTypes.TOGGLE_AI_SPECIFIC, true);
						put(PermissionTypes.TOGGLE_DAMAGE_SPECIFIC, true);
						put(PermissionTypes.EDIT_ENTITY_PERMISSIONS, true);
					}});
					break;
				case FRIEND:
					permissions.put(user, new HashMap<PermissionTypes, Boolean>() {{
						put(PermissionTypes.EDIT_SPECIFIC, true);
						put(PermissionTypes.DELETE_SPECIFIC, true);
						put(PermissionTypes.TOGGLE_AI_SPECIFIC, true);
						put(PermissionTypes.TOGGLE_DAMAGE_SPECIFIC, true);
						put(PermissionTypes.EDIT_ENTITY_PERMISSIONS, false);
					}});
					break;
				case OTHER:
					permissions.put(user, new HashMap<PermissionTypes, Boolean>() {{
						put(PermissionTypes.EDIT_SPECIFIC, false);
						put(PermissionTypes.DELETE_SPECIFIC, false);
						put(PermissionTypes.TOGGLE_AI_SPECIFIC, false);
						put(PermissionTypes.TOGGLE_DAMAGE_SPECIFIC, false);
						put(PermissionTypes.EDIT_ENTITY_PERMISSIONS, false);
					}});
					break;
			}
		}

		public int getEntityID() {
			return entityID;
		}

		public SegmentController getEntity() {
			return (SegmentController) GameCommon.getGameObject(entityID);
		}

		public EntityType getEntityType() {
			return entityType;
		}

		public String getSpawner() {
			return getEntity().getSpawner();
		}

		public boolean getPermission(String user, PermissionTypes type) {
			HashMap<PermissionTypes, Boolean> permissionMap = permissions.get(user);
			if(permissionMap != null) {
				Boolean value = permissionMap.get(type);
				if(value != null) return value;
				else return false;
			} else return false;
		}

		public void setPermission(String user, PermissionTypes type, boolean value, boolean server) {
			HashMap<PermissionTypes, Boolean> permissionMap = permissions.get(user);
			if(permissionMap != null) permissionMap.put(type, value);
			else {
				HashMap<PermissionTypes, Boolean> newPermissionMap = new HashMap<>();
				newPermissionMap.put(type, value);
				permissions.put(user, newPermissionMap);
			}
			BuildSectorDataManager.getInstance().updateData(BuildSectorData.this, server);
		}

		public boolean isAIActive() {
			if(getEntity() instanceof Ship) {
				Ship ship = (Ship) getEntity();
				return ship.getAiConfiguration().get(Types.ACTIVE).isOn();
			} else if(getEntity() instanceof SpaceStation) {
				SpaceStation station = (SpaceStation) getEntity();
				return station.getAiConfiguration().get(Types.ACTIVE).isOn();
			} else return false;
		}

		public void setAIActive(boolean value) {
			try {
				if(getEntity() instanceof Ship) {
					Ship ship = (Ship) getEntity();
					ship.getAiConfiguration().get(Types.ACTIVE).switchSetting(String.valueOf(value), true);
					for(RailRelation docked : ship.railController.next) {
						if(docked.docked.getSegmentController() instanceof Ship) setAiRecursive((Ship) docked.docked.getSegmentController(), value);
					}
				} else if(getEntity() instanceof SpaceStation) {
					SpaceStation station = (SpaceStation) getEntity();
					station.getAiConfiguration().get(Types.ACTIVE).switchSetting(String.valueOf(value), true);
					for(RailRelation docked : station.railController.next) {
						if(docked.docked.getSegmentController() instanceof Ship) setAiRecursive((Ship) docked.docked.getSegmentController(), value);
					}
				}
			} catch(Exception exception) {
				EdenCore.getInstance().logException("An error occurred while setting AI for entity", exception);
			}
		}

		private void setAiRecursive(Ship ship, boolean value) {
			try {
				ship.getAiConfiguration().get(Types.ACTIVE).switchSetting(String.valueOf(value), true);
				for(RailRelation docked : ship.railController.next) {
					if(docked.docked.getSegmentController() instanceof Ship) setAiRecursive((Ship) docked.docked.getSegmentController(), value);
				}
			} catch(Exception exception) {
				EdenCore.getInstance().logException("An error occurred while setting AI for entity", exception);
			}
		}

		public void delete() {
			EntityUtils.delete(getEntity());
		}

		public void deleteTurrets() {
			for(RailRelation docked : getEntity().railController.next) {
				if(docked.docked.getSegmentController() instanceof Ship) EntityUtils.delete(docked.docked.getSegmentController());
			}
		}
	}

	public enum PermissionTypes {
		EDIT_SPECIFIC("edit_specific", "Edit Specific Ship", "Whether the player can edit a specific ship."),
		EDIT_OWN("edit_own", "Edit Own Ships", "Whether the player can edit their own ships."),
		EDIT_ANY("edit_any", "Edit Other Ships", "Whether the player can edit ships owned by other players."),
		SPAWN("spawn", "Spawn Ships", "Whether the player can spawn ships from their catalog."),
		SPAWN_ENEMIES("spawn_enemies", "Spawn Enemies", "Whether the player can spawn enemy ships."),
		DELETE_SPECIFIC("delete_specific", "Delete Specific Ship", "Whether the player can delete a specific ship."),
		DELETE_OWN("delete_own", "Delete Own Ships", "Whether the player can delete their own ships."),
		DELETE_ANY("delete_any", "Delete Other Ships", "Whether the player can delete ships owned by other players."),
		DELETE_ALL("delete_all", "Delete All Ships", "Whether the player can delete all ships in the sector."),
		TOGGLE_AI_SPECIFIC("toggle_ai_specific", "Toggle Specific AI", "Whether the player can toggle AI on a specific ship."),
		TOGGLE_AI_OWN("toggle_ai_own", "Toggle Own AI", "Whether the player can toggle AI on their own ships."),
		TOGGLE_AI_ANY("toggle_ai_any", "Toggle Other AI", "Whether the player can toggle AI on ships owned by other players."),
		TOGGLE_AI_ALL("toggle_ai_all", "Toggle All AI", "Whether the player can toggle AI on all ships in the sector."),
		TOGGLE_DAMAGE_SPECIFIC("toggle_damage_specific", "Toggle Specific Damage", "Whether the player can toggle damage on a specific ship."),
		TOGGLE_DAMAGE_OWN("toggle_damage_own", "Toggle Own Damage", "Whether the player can toggle damage on their own ships."),
		TOGGLE_DAMAGE_ANY("toggle_damage_any", "Toggle Other Damage", "Whether the player can toggle damage on ships owned by other players."),
		TOGGLE_DAMAGE_ALL("toggle_damage_all", "Toggle All Damage", "Whether the player can toggle damage on all ships in the sector."),
		INVITE("invite", "Invite Players", "Whether the player can invite other players to the sector."),
		KICK("kick", "Kick Players", "Whether the player can kick other players from the sector."),
		EDIT_PERMISSIONS("edit_permissions", "Edit Permissions", "Whether the player can edit permissions for other players."),
		EDIT_ENTITY_PERMISSIONS("edit_entity_permissions", "Edit Entity Permissions", "Whether the player can edit permissions for specific entities.");

		private final String key;
		private final String name;
		private final String description;

		PermissionTypes(String key, String name, String description) {
			this.key = key;
			this.name = name;
			this.description = description;
		}

		public String getKey() {
			return key;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public static Set<PermissionTypes> getListValues() {
			Set<PermissionTypes> values = new HashSet<>();
			values.add(EDIT_OWN);
			values.add(EDIT_ANY);
			values.add(SPAWN);
			values.add(SPAWN_ENEMIES);
			values.add(DELETE_OWN);
			values.add(DELETE_ANY);
			values.add(DELETE_ALL);
			values.add(TOGGLE_AI_OWN);
			values.add(TOGGLE_AI_ANY);
			values.add(TOGGLE_AI_ALL);
			values.add(TOGGLE_DAMAGE_OWN);
			values.add(TOGGLE_DAMAGE_ANY);
			values.add(TOGGLE_DAMAGE_ALL);
			values.add(INVITE);
			values.add(KICK);
			values.add(EDIT_PERMISSIONS);
			return values;
		}

		public static Set<PermissionTypes> getEntitySpecificValues() {
			Set<PermissionTypes> values = new HashSet<>();
			values.add(EDIT_SPECIFIC);
			values.add(DELETE_SPECIFIC);
			values.add(TOGGLE_AI_SPECIFIC);
			values.add(TOGGLE_DAMAGE_SPECIFIC);
			values.add(EDIT_ENTITY_PERMISSIONS);
			return values;
		}
	}
}

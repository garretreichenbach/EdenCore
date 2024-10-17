package thederpgamer.edencore.data.buildsectordata;

import api.common.GameCommon;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.other.HashList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.catalog.CatalogPermission;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import thederpgamer.edencore.data.SerializableData;
import thederpgamer.edencore.data.playerdata.PlayerData;
import thederpgamer.edencore.data.playerdata.PlayerDataManager;

import java.io.IOException;
import java.util.HashSet;
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
	
	private String owner;
	private Vector3i sector;
	private final Set<BuildSectorEntityData> entities = new HashSet<>();
	private final HashList<String, BuildSectorPermissionData> permissions = new HashList<>();

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
			for(BuildSectorPermissionData permission : permissions.get(name)) {
				permissionsArray.put(permission.serialize());
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
		for(int i = 0; i < entitiesArray.length(); i ++) entities.add(new BuildSectorEntityData(entitiesArray.getJSONObject(i)));
		JSONArray permissionsArray = data.getJSONArray("permissions");
		for(int i = 0; i < permissionsArray.length(); i ++) permissions.add(owner, new BuildSectorPermissionData(permissionsArray.getJSONObject(i)));
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
			for(BuildSectorPermissionData permission : permissions.get(name)) permission.serializeNetwork(writeBuffer);
		}
	}

	@Override
	public void deserializeNetwork(PacketReadBuffer readBuffer) throws IOException {
		byte version = readBuffer.readByte();
		dataUUID = readBuffer.readString();
		owner = readBuffer.readString();
		sector = new Vector3i(Vector3i.parseVector3i(readBuffer.readString()));
		int entityCount = readBuffer.readInt();
		for(int i = 0; i < entityCount; i ++) entities.add(new BuildSectorEntityData(readBuffer));
		int permissionCount = readBuffer.readInt();
		for(int i = 0; i < permissionCount; i ++) permissions.add(owner, new BuildSectorPermissionData(readBuffer));
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

	public Set<BuildSectorPermissionData> getPermissionsFor(String name) {
		return new HashSet<>(permissions.get(name));
	}

	public void addPlayer(String name, int type, boolean server) {
		setDefaultPerms(name, type);
		BuildSectorDataManager.getInstance().updateData(this, server);
	}

	public void removePlayer(String name, boolean server) {
		permissions.remove(name);
		BuildSectorDataManager.getInstance().updateData(this, server);
	}

	public void setPermission(String name, PermissionTypes type, Object value, boolean server) {
		BuildSectorPermissionData permission = getPermission(name, type);
		permission.value = value;
		BuildSectorDataManager.getInstance().updateData(this, server);
	}

	public BuildSectorPermissionData getPermission(String name, PermissionTypes type) {
		for(BuildSectorPermissionData permission : permissions.get(name)) {
			if(permission.getType() == type) return permission;
		}
		return null;
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
			entityData.entityType = entity.getType();
			BuildSectorDataManager.getInstance().updateData(this, server);
		}
	}
	
	public void spawnEntity(CatalogPermission catalogPermission, PlayerState spawner, boolean onDock) {
		
	}
	
	public void setEntityPermission(SegmentController entity, PermissionTypes type, Object value, boolean server) {
		BuildSectorEntityData entityData = getEntity(entity);
		BuildSectorPermissionData permission = entityData.permissions.get(owner).stream().filter(p -> p.getType() == type).findFirst().orElse(null);
		if(permission != null) permission.value = value;
		BuildSectorDataManager.getInstance().updateData(this, server);
	}
	
	public BuildSectorPermissionData getEntityPermission(SegmentController entity, PermissionTypes type) {
		BuildSectorEntityData entityData = getEntity(entity);
		return entityData.permissions.get(owner).stream().filter(p -> p.getType() == type).findFirst().orElse(null);
	}

	private void setDefaultPerms(String name, int type) {
		switch(type) {
			case OWNER:
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.EDIT_OWN, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.EDIT_ANY, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.SPAWN, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.SPAWN_ENEMIES, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.DELETE_OWN, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.DELETE_ANY, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.DELETE_ALL, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.TOGGLE_AI_OWN, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.TOGGLE_AI_ANY, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.TOGGLE_AI_ALL, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.TOGGLE_DAMAGE_OWN, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.TOGGLE_DAMAGE_ANY, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.TOGGLE_DAMAGE_ALL, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.INVITE, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.KICK, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.EDIT_PERMISSIONS, true));
				break;
			case FRIEND:
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.EDIT_OWN, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.EDIT_ANY, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.SPAWN, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.SPAWN_ENEMIES, false));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.DELETE_OWN, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.DELETE_ANY, false));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.DELETE_ALL, false));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.TOGGLE_AI_OWN, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.TOGGLE_AI_ANY, false));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.TOGGLE_AI_ALL, false));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.TOGGLE_DAMAGE_OWN, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.TOGGLE_DAMAGE_ANY, false));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.TOGGLE_DAMAGE_ALL, false));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.INVITE, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.KICK, false));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.EDIT_PERMISSIONS, false));
				break;
			case OTHER:
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.EDIT_OWN, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.EDIT_ANY, false));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.SPAWN, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.SPAWN_ENEMIES, false));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.DELETE_OWN, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.DELETE_ANY, false));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.DELETE_ALL, false));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.TOGGLE_AI_OWN, true));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.TOGGLE_AI_ANY, false));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.TOGGLE_AI_ALL, false));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.TOGGLE_DAMAGE_OWN, false));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.TOGGLE_DAMAGE_ANY, false));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.TOGGLE_DAMAGE_ALL, false));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.INVITE, false));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.KICK, false));
				permissions.add(name, new BuildSectorPermissionData(PermissionTypes.EDIT_PERMISSIONS, false));
				break;
		}
	}

	public static class BuildSectorEntityData extends SerializableData {

		private static final byte VERSION = 0;
		
		private int entityID;
		private SimpleTransformableSendableObject.EntityType entityType;
		private final HashList<String, BuildSectorPermissionData> permissions = new HashList<>();
		
		public BuildSectorEntityData(SegmentController entity) {
			super(DataType.BUILD_SECTOR_ENTITY_DATA);
			entityID = entity.getId();
			entityType = entity.getType();
			setDefaultPerms(entity);
		}

		public BuildSectorEntityData(PacketReadBuffer readBuffer) throws IOException {
			super(readBuffer);
		}

		public BuildSectorEntityData(JSONObject data) {
			super(data);
		}
		
		private void setDefaultPerms(SegmentController entity) {
			String owner = entity.getSpawner();
			permissions.add(owner, new BuildSectorPermissionData(PermissionTypes.EDIT_SPECIFIC, new Object[] {true, entity.getId()}));
			permissions.add(owner, new BuildSectorPermissionData(PermissionTypes.DELETE_SPECIFIC, new Object[] {true, entity.getId()}));
			permissions.add(owner, new BuildSectorPermissionData(PermissionTypes.TOGGLE_AI_SPECIFIC, new Object[] {true, entity.getId()}));
			permissions.add(owner, new BuildSectorPermissionData(PermissionTypes.TOGGLE_DAMAGE_SPECIFIC, new Object[] {true, entity.getId()}));
			permissions.add(owner, new BuildSectorPermissionData(PermissionTypes.EDIT_ENTITY_PERMISSIONS, new Object[] {true, entity.getId()}));
		}

		@Override
		public JSONObject serialize() {
			JSONObject data = new JSONObject();
			data.put("version", VERSION);
			data.put("uuid", getUUID());
			data.put("entityID", entityID);
			data.put("entityType", entityType.name());
			return data;
		}

		@Override
		public void deserialize(JSONObject data) {
			byte version = (byte) data.getInt("version");
			dataUUID = data.getString("uuid");
			entityID = data.getInt("entityID");
			entityType = SimpleTransformableSendableObject.EntityType.valueOf(data.getString("entityType"));
		}

		@Override
		public void serializeNetwork(PacketWriteBuffer writeBuffer) throws IOException {
			writeBuffer.writeByte(VERSION);
			writeBuffer.writeString(dataUUID);
			writeBuffer.writeInt(entityID);
			writeBuffer.writeString(entityType.name());
		}

		@Override
		public void deserializeNetwork(PacketReadBuffer readBuffer) throws IOException {
			byte version = readBuffer.readByte();
			dataUUID = readBuffer.readString();
			entityID = readBuffer.readInt();
			entityType = SimpleTransformableSendableObject.EntityType.valueOf(readBuffer.readString());
		}
		
		public SegmentController getEntity() {
			return (SegmentController) GameCommon.getGameObject(entityID);
		}
		
		public SimpleTransformableSendableObject.EntityType getEntityType() {
			return entityType;
		}
	}

	public static class BuildSectorPermissionData extends SerializableData {

		private static final byte VERSION = 0;
		
		private PermissionTypes type;
		private Object value;

		public BuildSectorPermissionData(PermissionTypes type, Object value) {
			super(DataType.BUILD_SECTOR_PERMISSION_DATA);
			this.type = type;
			this.value = value;
		}

		public BuildSectorPermissionData(PacketReadBuffer readBuffer) throws IOException {
			super(readBuffer);
		}

		public BuildSectorPermissionData(JSONObject data) {
			super(data);
		}

		@Override
		public JSONObject serialize() {
			JSONObject data = new JSONObject();
			data.put("version", VERSION);
			data.put("uuid", getUUID());
			data.put("type", type.ordinal());
			data.put("value", value);
			return data;
		}

		@Override
		public void deserialize(JSONObject data) {
			byte version = (byte) data.getInt("version");
			dataUUID = data.getString("uuid");
			type = PermissionTypes.values()[data.getInt("type")];
			value = data.get("value");
		}

		@Override
		public void serializeNetwork(PacketWriteBuffer writeBuffer) throws IOException {
			writeBuffer.writeByte(VERSION);
			writeBuffer.writeString(dataUUID);
			writeBuffer.writeInt(type.ordinal());
			writeBuffer.writeString(value.toString());
		}

		@Override
		public void deserializeNetwork(PacketReadBuffer readBuffer) throws IOException {
			byte version = readBuffer.readByte();
			dataUUID = readBuffer.readString();
			type = PermissionTypes.values()[readBuffer.readInt()];
			value = readBuffer.readString();
		}

		public PermissionTypes getType() {
			return type;
		}

		public Object getValue() {
			return value;
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

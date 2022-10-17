package thederpgamer.edencore.data.event;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.server.controller.BluePrintController;
import org.schema.game.server.controller.EntityNotFountException;
import org.schema.game.server.data.blueprintnw.BlueprintEntry;
import thederpgamer.edencore.data.SerializableData;

import java.io.IOException;

/**
 * [Description]
 *
 * @author TheDerpGamer (MrGoose#0027)
 */
public class EventEnemyData implements SerializableData {

	public static final int CAPACITY_MODIFIER = 0;
	public static final int RECHARGE_MODIFIER = 1;
	public static final int ARMOR_MODIFIER = 2;
	public static final int DAMAGE_MODIFIER = 4;
	public static final int SPEED_MODIFIER = 8;

	private int spawnCount = 1;
	private String entryName;

	public EventEnemyData(PacketReadBuffer readBuffer) throws Exception {
		deserialize(readBuffer);
	}

	@Override
	public void deserialize(PacketReadBuffer readBuffer) throws IOException {

	}

	@Override
	public void serialize(PacketWriteBuffer writeBuffer) throws IOException {

	}

	public void setSpawnCount(int spawnCount) {
		this.spawnCount = spawnCount;
	}

	public int getSpawnCount() {
		return spawnCount;
	}

	public BlueprintEntry getCatalogEntry() {
		try {
			return BluePrintController.active.getBlueprint(entryName);
		} catch(EntityNotFountException exception) {
			throw new RuntimeException(exception);
		}
	}
}

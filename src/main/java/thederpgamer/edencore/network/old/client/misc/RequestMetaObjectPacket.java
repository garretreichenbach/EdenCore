package thederpgamer.edencore.network.old.client.misc;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.element.meta.MetaObject;
import org.schema.game.common.data.element.meta.MetaObjectManager;
import org.schema.game.common.data.element.meta.weapon.LaserWeapon;
import org.schema.game.common.data.element.meta.weapon.Weapon;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.inventory.NoSlotFreeException;

import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/24/2021]
 */
public class RequestMetaObjectPacket extends Packet {
	private short itemId;
	private short metaId;
	private short subType;

	public RequestMetaObjectPacket() {
	}

	public RequestMetaObjectPacket(short itemId, short metaId, short subType) {
		this.itemId = itemId;
		this.metaId = metaId;
		this.subType = subType;
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		itemId = packetReadBuffer.readShort();
		metaId = packetReadBuffer.readShort();
		subType = packetReadBuffer.readShort();
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		packetWriteBuffer.writeShort(itemId);
		packetWriteBuffer.writeShort(metaId);
		packetWriteBuffer.writeShort(subType);
	}

	@Override
	public void processPacketOnClient() {
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
		try {
			MetaObject weapon = MetaObjectManager.instantiate(MetaObjectManager.MetaObjectType.WEAPON.type, Weapon.WeaponSubType.getById(subType).type, true);
			if(weapon instanceof LaserWeapon) ((LaserWeapon) weapon).getColor().set(0, 1, 0, 1);
			int slot = playerState.getInventory().getFreeSlot();
			playerState.getInventory().put(slot, weapon);
			playerState.getInventory().sendInventoryModification(slot);
		} catch(NoSlotFreeException exception) {
			exception.printStackTrace();
		}
	}
}

package thederpgamer.edencore.network.client;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.game.PlayerUtils;
import org.schema.game.common.controller.ElementCountMap;
import org.schema.game.common.data.element.meta.BlueprintMetaItem;
import org.schema.game.common.data.element.meta.MetaObjectManager;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.server.data.blueprintnw.BlueprintEntry;
import thederpgamer.edencore.data.exchange.BlueprintExchangeItem;
import thederpgamer.edencore.manager.LogManager;

import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/28/2022]
 */
public class RequestExchangeBPItemGivePacket extends Packet {

	private BlueprintExchangeItem exchangeItem;

	public RequestExchangeBPItemGivePacket() {

	}

	public RequestExchangeBPItemGivePacket(BlueprintExchangeItem exchangeItem) {
		this.exchangeItem = exchangeItem;
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		exchangeItem = new BlueprintExchangeItem(packetReadBuffer);
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		exchangeItem.serialize(packetWriteBuffer);
	}

	@Override
	public void processPacketOnClient() {

	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
		try {
			BlueprintMetaItem metaItem = (BlueprintMetaItem) MetaObjectManager.instantiate(MetaObjectManager.MetaObjectType.BLUEPRINT, (short) -1, true);
			BlueprintEntry blueprintEntry = exchangeItem.getBlueprintEntry();
			metaItem.blueprintName = exchangeItem.name;
			metaItem.goal = new ElementCountMap(blueprintEntry.getElementCountMapWithChilds());
			metaItem.progress = new ElementCountMap(blueprintEntry.getElementCountMapWithChilds());
			int slot = playerState.getInventory().getFreeSlot();
			playerState.getInventory().put(slot, metaItem);
			playerState.getInventory().sendInventoryModification(slot);
		} catch(Exception exception) {
			LogManager.logException("An exception occurred while trying to spawn blueprint \"" + exchangeItem.name + "\" for player " + playerState.getName() + "!", exception);
			PlayerUtils.sendMessage(playerState, "Sorry, but an exception occurred while trying to spawn the blueprint. Contact an admin for help.");
		}
	}
}

package thederpgamer.edencore.network.old.client.exchange;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.data.exchange.BlueprintExchangeItemOld;
import thederpgamer.edencore.utils.PlayerDataUtil;

import java.io.IOException;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class PlayerBuyBPPacket extends Packet {
	private BlueprintExchangeItemOld item;

	public PlayerBuyBPPacket() {
	}

	public PlayerBuyBPPacket(BlueprintExchangeItemOld item) {
		this.item = item;
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		item = new BlueprintExchangeItemOld(packetReadBuffer);
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		item.serialize(packetWriteBuffer);
	}

	@Override
	public void processPacketOnClient() {
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
		PlayerDataUtil.addBars(item.seller, item.barType, item.price);
	}
}

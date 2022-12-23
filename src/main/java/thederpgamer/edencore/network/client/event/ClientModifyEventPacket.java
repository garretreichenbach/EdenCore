package thederpgamer.edencore.network.client.event;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.data.event.EventData;

import java.io.IOException;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class ClientModifyEventPacket extends Packet {

	public static final int JOIN_EVENT = 0;
	public static final int LEAVE_EVENT = 1;
	public static final int SET_READY = 2;
	public static final int CHANGE_SHIP = 3;

	private EventData eventData;
	private int action;
	private Object[] args;

	public ClientModifyEventPacket() {

	}

	public ClientModifyEventPacket(EventData eventData, int action) {
		this.eventData = eventData;
		this.action = action;
		this.args = new Object[0];
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		eventData = EventData.fromPacket(packetReadBuffer);
		action = packetReadBuffer.readInt();
		args = new Object[packetReadBuffer.readInt()];
		for(int i = 0; i < args.length; i ++) {
			try {
				args[i] = packetReadBuffer.readObject(Class.forName(packetReadBuffer.readString()));
			} catch(ClassNotFoundException exception) {
				exception.printStackTrace();
			}
		}
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		eventData.serialize(packetWriteBuffer);
		packetWriteBuffer.writeInt(action);
		packetWriteBuffer.writeInt(args.length);
		for(Object arg : args) {
			packetWriteBuffer.writeString(arg.getClass().getName());
			packetWriteBuffer.writeObject(arg);
		}
	}

	@Override
	public void processPacketOnClient() {

	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
		switch(action) {
			case JOIN_EVENT:
				eventData.joinEvent(playerState);
				break;
			case LEAVE_EVENT:
				eventData.leaveEvent(playerState);
				break;
			case SET_READY:
				eventData.getSquadMember(playerState).setReady((boolean) args[0]);
				break;
			case CHANGE_SHIP:
				eventData.getSquadMember(playerState).setShip((String) args[0]);
				break;
		}
	}
}

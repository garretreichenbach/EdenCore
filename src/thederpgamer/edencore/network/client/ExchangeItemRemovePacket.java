package thederpgamer.edencore.network.client;

import api.mod.config.PersistentObjectUtil;
import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.exchange.BlueprintExchangeItem;
import thederpgamer.edencore.data.exchange.ExchangeItem;
import thederpgamer.edencore.data.exchange.ResourceExchangeItem;

import java.io.IOException;

/**
 * Requests the removal of an exchange item from the server.
 * <p>[CLIENT -> SERVER]</p>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/18/2021]
 */
public class ExchangeItemRemovePacket extends Packet {

    private int type;
    private ExchangeItem item;

    public ExchangeItemRemovePacket() {

    }

    public ExchangeItemRemovePacket(int type, ExchangeItem item) {
        this.type = type;
        this.item = item;
    }

    @Override
    public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
        type = packetReadBuffer.readInt();
        if(type == 0) item = new BlueprintExchangeItem(packetReadBuffer);
        else item = new ResourceExchangeItem(packetReadBuffer);
    }

    @Override
    public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
        packetWriteBuffer.writeInt(type);
        item.serialize(packetWriteBuffer);
    }

    @Override
    public void processPacketOnClient() {

    }

    @Override
    public void processPacketOnServer(PlayerState playerState) {
        assert playerState.isAdmin();
        ExchangeItem toRemove = null;
        if(type == 0) {
            for(Object object : PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), BlueprintExchangeItem.class)) {
                BlueprintExchangeItem exchangeItem = (BlueprintExchangeItem) object;
                if(exchangeItem.name.equals(item.name)) {
                    toRemove = exchangeItem;
                    break;
                }
            }
        } else {
            for(Object object : PersistentObjectUtil.getObjects(EdenCore.getInstance().getSkeleton(), ResourceExchangeItem.class)) {
                ResourceExchangeItem exchangeItem = (ResourceExchangeItem) object;
                if(exchangeItem.name.equals(item.name)) {
                    toRemove = exchangeItem;
                    break;
                }
            }
        }

        if(toRemove != null) {
            PersistentObjectUtil.removeObject(EdenCore.getInstance().getSkeleton(), toRemove);
            PersistentObjectUtil.save(EdenCore.getInstance().getSkeleton());
        }
    }
}

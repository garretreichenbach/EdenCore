package thederpgamer.edencore.data.exchange;

import api.common.GameClient;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.server.data.blueprintnw.BlueprintEntry;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.forms.gui.GUIOverlay;

import javax.vecmath.Vector3f;
import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/17/2021]
 */
public class BlueprintExchangeItem extends ExchangeItem {

    //public static final transient Vector3i SECTOR = new Vector3i(100000000, 100000000, 100000000);
    public transient BlueprintEntry blueprint;
    public double mass;
    public Vector3f min;
    public Vector3f max;

    public BlueprintExchangeItem(BlueprintEntry blueprint, short barType, int price, String description) {
        super(barType, price, blueprint.getName(), description);
        this.blueprint = blueprint;
        this.barType = barType;
        this.price = price;
        this.name = blueprint.getName();
        this.description = description;
        this.mass = blueprint.getMass();
        this.min = blueprint.getBb().min;
        this.max = blueprint.getBb().max;
    }

    @Override
    public GUIOverlay getIcon() {
        GUIOverlay tempOverlay = new GUIOverlay(Controller.getResLoader().getSprite("map-sprites-8x2-c-gui-"), GameClient.getClientState());
        tempOverlay.setSpriteSubIndex(blueprint.getEntityType().type.mapSprite);
        return tempOverlay;
        //return new GUIOverlay(ResourceManager.getSpriteExternal(name + "-icon"), GameClient.getClientState());
    }

    @Override
    public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
        writeBuffer.writeShort(barType);
        writeBuffer.writeInt(price);
        writeBuffer.writeString(name);
        writeBuffer.writeString(description);
        writeBuffer.writeDouble(mass);
        writeBuffer.writeVector3f(min);
        writeBuffer.writeVector3f(max);
        /* Todo: Somehow generate a preview of the entity that can be used as it's icon
        try {
            writeBuffer.writeSendable(createEntity());
        } catch(EntityNotFountException | EntityAlreadyExistsException | StateParameterNotFoundException exception) {
            exception.printStackTrace();
        }
         */
    }

    @Override
    public void deserialize(PacketReadBuffer readBuffer) throws IOException {
        barType = readBuffer.readShort();
        price = readBuffer.readInt();
        name = readBuffer.readString();
        description = readBuffer.readString();
        mass = readBuffer.readDouble();
        min = readBuffer.readVector3f();
        max = readBuffer.readVector3f();
    }

    /*
    private SegmentController createEntity() throws IOException, StateParameterNotFoundException, EntityAlreadyExistsException, EntityNotFountException {
        SegmentControllerOutline<?> outline = BluePrintController.active.loadBluePrint(
                GameServerState.instance,
                blueprint.getName(),
                blueprint.getName() + "_TEMP",
                new Transform(),
                -1,
                0,
                SECTOR,
                "SERVER",
                PlayerState.buffer,
                null,
                false,
                new ChildStats(false));
        SegmentController entity = outline.spawn(SECTOR, false, new ChildStats(false), new SegmentControllerSpawnCallbackDirect(GameServer.getServerState(), SECTOR) {
            @Override
            public void onNoDocker() {

            }
        });

        new StarRunnable() {
            @Override
            public void run() {

            }
        }.runLater(EdenCore.getInstance(), 5000); //Should be enough time to send the entity and create an image of it's preview

        return entity;
    }
     */
}

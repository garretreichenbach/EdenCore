package thederpgamer.edencore.data.event.types;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import thederpgamer.edencore.data.SerializableData;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.data.event.EventModifier;
import thederpgamer.edencore.data.event.SquadData;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/05/2021]
 */
public abstract class EventRuleSet implements SerializableData, Serializable {

    protected float difficulty;
    protected boolean friendlyFire;
    protected boolean pvp;
    protected int maxPlayers;
    protected int minPlayers;
    protected int enemyCount;
    protected double totalEnemyTargetMass; //Total mass of all enemy targets, may not actually reach the exact value depending on what blueprints are provided
    protected final HashMap<String, Integer> enemyTargetBlueprints = new HashMap<>(); //Blueprints of enemy targets, key = blueprint name, value = count
    protected final HashMap<String, EventModifier> playerModifiers = new HashMap<>();
    protected final HashMap<String, EventModifier> enemyModifiers = new HashMap<>();

    public static EventRuleSet fromPacket(PacketReadBuffer readBuffer, EventData.EventType type) throws IOException {
        switch(type) {
            //case CAPTURE: return new CaptureRuleSet(readBuffer);
            //case DEFENSE: return new DefenseRuleSet(readBuffer);
            //case DESTROY: return new DestroyRuleSet(readBuffer);
            //case ESCORT: return new EscortRuleSet(readBuffer);
            //case PURSUIT: return new PursuitRuleSet(readBuffer);
        }
        return null;
    }

    public EventRuleSet(EventData event) {

    }

    public EventRuleSet(PacketReadBuffer readBuffer) throws IOException {
        deserialize(readBuffer);
    }

    @Override
    public void deserialize(PacketReadBuffer readBuffer) throws IOException {
        difficulty = readBuffer.readFloat();
        friendlyFire = readBuffer.readBoolean();
        pvp = readBuffer.readBoolean();
        maxPlayers = readBuffer.readInt();
        minPlayers = readBuffer.readInt();
        enemyCount = readBuffer.readInt();
        totalEnemyTargetMass = readBuffer.readDouble();

        int blueprintCount = readBuffer.readInt();
        for(int i = 0; i < blueprintCount; i ++) {
            String blueprintName = readBuffer.readString();
            int count = readBuffer.readInt();
            enemyTargetBlueprints.put(blueprintName, count);
        }

        int playerModifierCount = readBuffer.readInt();
        for(int i = 0; i < playerModifierCount; i ++) {
            String modifierName = readBuffer.readString();
            EventModifier modifier = EventModifier.fromPacket(readBuffer);
            playerModifiers.put(modifierName, modifier);
        }

        int enemyModifierCount = readBuffer.readInt();
        for(int i = 0; i < enemyModifierCount; i ++) {
            String modifierName = readBuffer.readString();
            EventModifier modifier = EventModifier.fromPacket(readBuffer);
            enemyModifiers.put(modifierName, modifier);
        }
    }

    @Override
    public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
        writeBuffer.writeFloat(difficulty);
        writeBuffer.writeBoolean(friendlyFire);
        writeBuffer.writeBoolean(pvp);
        writeBuffer.writeInt(maxPlayers);
        writeBuffer.writeInt(minPlayers);
        writeBuffer.writeInt(enemyCount);
        writeBuffer.writeDouble(totalEnemyTargetMass);

        writeBuffer.writeInt(enemyTargetBlueprints.size());
        for(String blueprintName : enemyTargetBlueprints.keySet()) {
            writeBuffer.writeString(blueprintName);
            writeBuffer.writeInt(enemyTargetBlueprints.get(blueprintName));
        }

        writeBuffer.writeInt(playerModifiers.size());
        for(String modifierName : playerModifiers.keySet()) {
            writeBuffer.writeString(modifierName);
            playerModifiers.get(modifierName).serialize(writeBuffer);
        }

        writeBuffer.writeInt(enemyModifiers.size());
        for(String modifierName : enemyModifiers.keySet()) {
            writeBuffer.writeString(modifierName);
            enemyModifiers.get(modifierName).serialize(writeBuffer);
        }
    }

    public abstract void calculateValues(EventData eventData, SquadData squadData);
}

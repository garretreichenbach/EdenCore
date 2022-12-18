package thederpgamer.edencore.data.event;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.blockeffects.config.StatusEffectType;
import thederpgamer.edencore.data.SerializableData;

import java.io.IOException;
import java.io.Serializable;

/**
 * [Description]
 *
 * @author TheDerpGamer (MrGoose#0027)
 */
public class EventModifier implements SerializableData, Serializable {

	public String modifierName;
	public String modifierDescription;
	public StatusEffectType effectTag;
	public float effectValue;
	public EventModifierEffect effect;

	public static EventModifier fromPacket(PacketReadBuffer readBuffer) throws IOException {
		String modifierName = readBuffer.readString();
		String modifierDescription = readBuffer.readString();
		StatusEffectType effectTag = StatusEffectType.valueOf(readBuffer.readString());
		float effectValue = readBuffer.readFloat();
		return new EventModifier(modifierName, modifierDescription, effectTag, effectValue);
	}

	public EventModifier(String modifierName, String modifierDescription, StatusEffectType effectTag, float effectValue) {
		this.modifierName = modifierName;
		this.modifierDescription = modifierDescription;
		this.effectTag = effectTag;
		this.effectValue = effectValue;
	}

	@Override
	public void deserialize(PacketReadBuffer readBuffer) throws IOException {
		modifierName = readBuffer.readString();
		modifierDescription = readBuffer.readString();
		effectTag = StatusEffectType.valueOf(readBuffer.readString());
		effectValue = readBuffer.readFloat();
	}

	@Override
	public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
		writeBuffer.writeString(modifierName);
		writeBuffer.writeString(modifierDescription);
		writeBuffer.writeString(effectTag.name());
		writeBuffer.writeFloat(effectValue);
	}

	public interface EventModifierEffect {
		boolean canApply(SegmentController entity);
		void applyEffect(SegmentController entity);
		void removeEffect(SegmentController entity);
	}
}
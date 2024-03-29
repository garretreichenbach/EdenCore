package org.schema.game.common.data.element.meta.weapon;

import org.schema.game.client.data.GameClientState;
import org.schema.game.common.controller.damage.effects.InterEffectHandler;
import org.schema.game.common.controller.damage.effects.InterEffectSet;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.beam.BeamReloadCallback;
import org.schema.game.common.data.element.meta.MetaObject;
import org.schema.game.common.data.player.AbstractCharacter;
import org.schema.game.common.data.player.AbstractOwnerState;
import org.schema.game.common.data.player.PlayerCharacter;
import org.schema.game.common.data.player.PlayerState;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.schine.resource.tag.FinishTag;
import org.schema.schine.resource.tag.Tag;
import thederpgamer.edencore.utils.SegmentPieceUtils;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TorchBeam extends Weapon {
	public int damage = 4;
	public float distance = 1.3f;
	public float reload = 0.5f;
	public BeamReloadCallback reloadCallback = new BeamReloadCallback() {
		private long nextShot;

		@Override
		public void setShotReloading(long reload) {
			nextShot = System.currentTimeMillis() + reload;
		}

		@Override
		public boolean canUse(long curTime, boolean popupText) {
			return nextShot < curTime;
		}

		@Override
		public boolean isInitializing(long curTime) {
			return false;
		}

		@Override
		public long getNextShoot() {
			return nextShot;
		}

		@Override
		public long getCurrentReloadTime() {
			return (long) (reload * 1000.0f);
		}

		@Override
		public boolean consumePower(float powerConsumtionDelta) {
			return true;
		}

		@Override
		public boolean canConsumePower(float powerConsumtionDelta) {
			return true;
		}

		@Override
		public double getPower() {
			return 1;
		}

		@Override
		public boolean isUsingPowerReactors() {
			return true;
		}

		@Override
		public void flagBeamFiredWithoutTimeout() {
		}
	};
	private Vector4f color = new Vector4f(Math.min(1.0f, (float) Math.random() + (float) Math.random()), Math.min(1.0f, (float) Math.random() + (float) Math.random()), Math.min(1.0f, (float) Math.random() + (float) Math.random()), 1.0f);

	public TorchBeam(int id) {
		super(id, WeaponSubType.TORCH.type);
	}

	@Override
	public void deserialize(DataInputStream stream) throws IOException {
		damage = stream.readInt();
		color.set(stream.readFloat(), stream.readFloat(), stream.readFloat(), stream.readFloat());
		distance = stream.readFloat();
		reload = stream.readFloat();
	}

	@Override
	public void fromTag(Tag tag) {
		Tag[] v = (Tag[]) tag.getValue();
		damage = (Integer) v[0].getValue();
		reload = (Float) v[1].getValue();
		color = (Vector4f) v[2].getValue();
		distance = (Float) v[3].getValue();
	}

	@Override
	public Tag getBytesTag() {
		return new Tag(Tag.Type.STRUCT, null, new Tag[] {new Tag(Tag.Type.INT, null, damage), new Tag(Tag.Type.FLOAT, null, reload), new Tag(Tag.Type.VECTOR4f, null, color), new Tag(Tag.Type.FLOAT, null, distance), FinishTag.INST});
	}

	@Override
	public void serialize(DataOutputStream stream) throws IOException {
		stream.writeInt(damage);
		stream.writeFloat(color.x);
		stream.writeFloat(color.y);
		stream.writeFloat(color.z);
		stream.writeFloat(color.w);
		stream.writeFloat(distance);
		stream.writeFloat(reload);
	}

	@Override
	public String getName() {
		return Lng.str("Torch");
	}

	@Override
	public boolean equalsObject(MetaObject other) {
		return equalsTypeAndSubId(other) && damage == ((TorchBeam) other).damage && color.equals(((TorchBeam) other).color) && distance == (((TorchBeam) other).distance);
	}

	/**
	 * @return the color
	 */
	public Vector4f getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Vector4f color) {
		this.color = color;
	}

	@Override
	protected void setupEffectSet(InterEffectSet s) {
		s.setStrength(InterEffectHandler.InterEffectType.KIN, 0.1f);
		s.setStrength(InterEffectHandler.InterEffectType.HEAT, 0.8f);
		s.setStrength(InterEffectHandler.InterEffectType.EM, 0.1f);
	}

	@Override
	public void fire(AbstractCharacter<?> playerCharacter, AbstractOwnerState state, boolean addButton, boolean removeButton, Timer timer) {
		if(addButton) {
			Vector3f dir = state.getForward(new Vector3f());
			SegmentPiece segmentPiece = SegmentPieceUtils.getLookingAt(playerCharacter);
			if(segmentPiece != null && segmentPiece.getInfo().getName().contains("Aegis")) return;
			fire(playerCharacter, state, dir, addButton, removeButton, timer);
		} else {
			//zoom done in clientController to revert if weapon changes etc
		}
	}

	@Override
	public void fire(AbstractCharacter<?> playerCharacter, AbstractOwnerState state, Vector3f dir, boolean addButton, boolean removeButton, Timer timer) {
		if(!state.isOnServer() && ((GameClientState) state.getState()).getCurrentSectorId() != playerCharacter.getSectorId()) {
			return;
		}
		if(playerCharacter instanceof PlayerCharacter) {
			((PlayerCharacter) playerCharacter).shootTorchBeam(((PlayerState) playerCharacter.getOwnerState()).getControllerState().getUnits().iterator().next(), damage, reload, distance, this, addButton, removeButton);
		}
	}

	@Override
	protected String toDetailedString() {
		return Lng.str("Torch Beam\nDamage: %s\nColor(RGBA): %s\nDistance: %s", damage, color, distance);
	}

	public boolean isIgnoringShields() {
		return true;
	}

	public boolean isIgnoringArmor() {
		return true;
	}
}
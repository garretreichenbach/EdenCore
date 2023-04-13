package thederpgamer.edencore.data.event.target;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.server.controller.BluePrintController;
import org.schema.game.server.data.blueprintnw.BlueprintEntry;
import thederpgamer.edencore.data.event.EventTarget;
import thederpgamer.edencore.gui.eventeditor.EventEditorFrame;

import java.io.IOException;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class DestroyTarget extends EventTarget {

	private int count;

	public DestroyTarget(String name, Object target, int count, int selectedIndex) {
		super(name, target);
		this.count = count;
		String[] bpNames = EventEditorFrame.getBPNames();
		String bpName = bpNames[selectedIndex];
		for(BlueprintEntry entry : BluePrintController.active.readBluePrints()) {
			if(entry.getName().equalsIgnoreCase(bpName)) {
				this.target = entry;
				break;
			}
		}
	}

	public DestroyTarget(PacketReadBuffer readBuffer) throws IOException {
		super(readBuffer);
	}

	@Override
	public void deserialize(PacketReadBuffer readBuffer) throws IOException {
	}

	@Override
	public void serialize(PacketWriteBuffer writeBuffer) throws IOException {
	}

	@Override
	public void updateClients() {
	}

	@Override
	public float getProgress() {
		return 0;
	}
}

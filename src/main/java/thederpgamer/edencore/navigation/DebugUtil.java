package thederpgamer.edencore.navigation;

import org.schema.common.util.linAlg.Vector3i;

import javax.vecmath.Vector4f;
import java.util.ArrayList;
import java.util.Random;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 26.09.2021
 * TIME: 15:52
 */
public class DebugUtil {
	public static Vector4f gateColor = new Vector4f(0, 0.667f, 1, 1);
	public static Vector4f lineColor = new Vector4f(1, 0.333f, 0, 1);

	public static ArrayList<MapMarker> mockGateNetwork(Vector3i start, int amountGates, long seed) {
		Random rand = new Random(seed);
		ArrayList<MapMarker> gates = new ArrayList<>(amountGates);
		int[] icons = new int[] {4, 5, 6, 7, 8, 9};
		Vector3i sector = new Vector3i(start);
		for(int i = 0; i < amountGates; i++) {
			//create a new gate randomly at range 100 sectors, add line to previous gate
			sector.set(nextIntRange(100, sector.x, rand), nextIntRange(100, sector.y, rand), nextIntRange(100, sector.z, rand));
			int iconIdx = icons[rand.nextInt(icons.length)]; //random gate icon
			GateMarker gate = new GateMarker(new Vector3i(sector), "Gate " + i, MapIcon.values()[iconIdx], new Vector4f(gateColor));
			gates.add(gate);
			if(i > 0) { //connect to previous gate
				gate.addLine(gates.get(i - 1).sector);
			}
		}
		return gates;
	}

	private static int nextIntRange(int range, int center, Random rand) {
		return rand.nextInt() % range + center;
	}
}

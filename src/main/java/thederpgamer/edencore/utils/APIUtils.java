package thederpgamer.edencore.utils;

import api.mod.StarLoader;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/04/2021]
 */
public class APIUtils {
	public static boolean isRRSInstalled() {
		return StarLoader.getModFromName("Resources ReSourced") != null;
	}
}

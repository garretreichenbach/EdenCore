package thederpgamer.edencore;

import api.config.BlockConfig;
import api.listener.events.controller.ClientInitializeEvent;
import api.listener.events.controller.ServerInitializeEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.network.packets.PacketUtil;
import glossar.GlossarCategory;
import glossar.GlossarEntry;
import glossar.GlossarInit;
import org.apache.commons.io.IOUtils;
import org.schema.schine.resource.ResourceLoader;
import thederpgamer.edencore.commands.*;
import thederpgamer.edencore.data.DataManager;
import thederpgamer.edencore.element.ElementManager;
import thederpgamer.edencore.element.items.PrizeBars;
import thederpgamer.edencore.manager.ConfigManager;
import thederpgamer.edencore.manager.EventManager;
import thederpgamer.edencore.manager.ResourceManager;
import thederpgamer.edencore.network.ServerPlayerActionCommandPacket;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Main class for EdenCore mod.
 *
 * @author TheDerpGamer
 * @version 1.0 - [06/27/2021]
 */
public class EdenCore extends StarMod {
	private static EdenCore instance;
	private final String[] overwriteClasses = {};
	public EdenCore() {instance = this;}
	public static void main(String[] args) {}
	public static EdenCore getInstance() {
		return instance;
	}

	@Override
	public byte[] onClassTransform(String className, byte[] byteCode) {
		for(String name : overwriteClasses) {
			if(className.endsWith(name)) return overwriteClass(className, byteCode);
		}
		return super.onClassTransform(className, byteCode);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		instance = this;
		ConfigManager.initialize(this);
		EventManager.initialize(this);
		registerPackets();
		registerCommands();
	}

	@Override
	public void onServerCreated(ServerInitializeEvent serverInitializeEvent) {
		super.onServerCreated(serverInitializeEvent);
		DataManager.initialize(false);
	}

	@Override
	public void onClientCreated(ClientInitializeEvent clientInitializeEvent) {
		super.onClientCreated(clientInitializeEvent);
		DataManager.initialize(true);
		initGlossary();
	}

	@Override
	public void onBlockConfigLoad(BlockConfig blockConfig) {
		ElementManager.addItemGroup(new PrizeBars());
		ElementManager.initialize();
		logInfo("Initialized Blocks");
	}

	@Override
	public void onResourceLoad(ResourceLoader resourceLoader) {
		ResourceManager.loadResources(resourceLoader);
	}

	@Override
	public void logInfo(String message) {
		System.out.println("[INFO][EdenCore]: " + message);
		super.logInfo(message);
	}

	@Override
	public void logWarning(String message) {
		System.out.println("[WARNING][EdenCore]: " + message);
		super.logWarning(message);
	}

	@Override
	public void logException(String message, Exception exception) {
		System.err.println("[EXCEPTION][EdenCore]: " + message + "\n" + exception.getMessage() + "\n" + Arrays.toString(exception.getStackTrace()));
		exception.printStackTrace();
		super.logException(message, exception);
	}

	@Override
	public void logFatal(String message, Exception exception) {
		System.err.println("[FATAL][EdenCore]: " + message + "\n" + exception.getMessage() + "\n" + Arrays.toString(exception.getStackTrace()));
		exception.printStackTrace();
		super.logFatal(message, exception);
	}

	private void initGlossary() {
		GlossarInit.initGlossar(this);
		GlossarCategory rules = new GlossarCategory("Server Info and Rules");
		rules.addEntry(new GlossarEntry("Server Info", "Skies of Eden is a modded survival StarMade server run by the SOE staff team and hosted on CBS hardware.\n" + "We work hard to bring new features and content to the server, and we hope you enjoy your time here.\n" + "Note that not all features are complete, and some may be buggy. If you find any bugs, please report them to a staff member.\n" + "Please read the rules section before playing on the server, and be sure to join our discord at https://discord.gg/qxzvBxT."));
		rules.addEntry(new GlossarEntry("Rules", "1) Be polite and respectful in chat.\n" + "2) Do not spam chat or advertise links to other servers.\n" + "3) Do not use any cheats, glitches, exploits, etc. that give you an unfair advantage over other players. If you find a bug, please report it to a staff member.\n" + "4) Keep politics at an absolute minimum. This is a starmade server, not a political forum.\n" + "5) Hate speech and hate symbols are not tolerated. This includes racism, sexism, homophobia, etc.\n" + "6) Do not intentionally create server lag. If your entity is lagging the server, it may be deleted by staff without compensation.\n" + "7) Do not create home-bases on planets.\n" + "8) Do not attempt to attack or capture public infrastructure such as warpgates.\n" + "9) Use common sense. If you are unsure about something, ask a staff member.\n" + "10) Repeated or serious violations of any of the server rules can result in bans of the offenders, deletion of ships/stations, and penalties to anyone involved or associated."));
		GlossarInit.addCategory(rules);
		GlossarCategory edenCore = new GlossarCategory("Eden Core");
		edenCore.addEntry(new GlossarEntry("Build Sectors", "Build Sectors are special sectors unique to each player where you can build freely in creative mode. They are protected from other players and hostiles.\n" + "You can invite other players to your build sector, set permissions, spawn entities, and more using the build sector menu.\nTo access the build sector menu, use the - key on your keypad or look in the top right menu bar under PLAYER.\n" + "If you prefer to use commands, you can use /help build_sector to view usable commands."));
		edenCore.addEntry(new GlossarEntry("Banking", "Banking is a feature that allows you to send money to other players.\n" + "To send money, use /bank_send <player_name> <amount> [optional_message].\n" + "To view the last 10 transactions, use /bank_list."));
		edenCore.addEntry(new GlossarEntry("Server Exchange", "Every day you log in, you will receive 2 Bronze Bars. You can only receive this reward once per day.\n" + "You can use these bars in the Server Exchange menu, which can be opened with the * key on your keypad, or by looking in the top right menu bar under PLAYER.\n" + "You can use the Server Exchange to buy items from the server shop, such as resources, items, and blueprints. Please note that some of these features are work in progress and may not always be available.\n" + "Some items in the Server Exchange require silver or gold bars instead of bronze. To upgrade your bronze bars into silver or gold, see the EXCHANGE tab in the Server Exchange menu."));
		edenCore.addEntry(new GlossarEntry("Listing Blueprints in the Exchange", "Players are able to sell blueprints in the COMMUNITY tab of the blueprint exchange."));
		edenCore.addEntry(new GlossarEntry("Donator Perks", "Donators can use chat colors and get free bars each day.\n" + "Chat colors:\n" + " - &0 = Transparent\n" + " - &1 = White\n" + " - &2 = Light Grey\n" + " - &3 = Grey\n" + " - &4 = Dark Grey\n" + " - &5 = Black\n" + " - &y = Yellow\n" + " - &o = Orange\n" + " - &r = Red\n" + " - &m = Magenta\n" + " - &p = Pink\n" + " - &b = Blue\n" + " - &c = Cyan\n" + " - &g = Green\n"));
		GlossarInit.addCategory(edenCore);
		logInfo("Initialized Glossary");
	}

	private void registerPackets() {
		PacketUtil.registerPacket(ServerPlayerActionCommandPacket.class);
		logInfo("Registered Packets");
	}

	private void registerCommands() {
		StarLoader.registerCommand(new GuideCommand());
		logInfo("Registered Commands");
	}

	private byte[] overwriteClass(String className, byte[] byteCode) {
		byte[] bytes = null;
		try {
			ZipInputStream file = new ZipInputStream(Files.newInputStream(getSkeleton().getJarFile().toPath()));
			while(true) {
				ZipEntry nextEntry = file.getNextEntry();
				if(nextEntry == null) break;
				if(nextEntry.getName().endsWith(className + ".class")) bytes = IOUtils.toByteArray(file);
			}
			file.close();
		} catch(IOException exception) {
			exception.printStackTrace();
		}
		if(bytes != null) return bytes;
		else return byteCode;
	}
}

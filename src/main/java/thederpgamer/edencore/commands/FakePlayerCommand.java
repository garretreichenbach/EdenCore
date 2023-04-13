package thederpgamer.edencore.commands;

import api.common.GameServer;
import api.mod.StarMod;
import api.network.packets.PacketUtil;
import api.utils.StarRunnable;
import api.utils.game.chat.CommandInterface;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;
import org.schema.game.common.data.chat.ChannelRouter;
import org.schema.game.common.data.chat.ChatChannel;
import org.schema.game.common.data.player.PlayerState;
import org.schema.schine.common.language.Lng;
import org.schema.schine.network.server.ServerMessage;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.FakeChannel;
import thederpgamer.edencore.data.FakePlayerLoginRequest;
import thederpgamer.edencore.network.server.ChatPacket;
import thederpgamer.edencore.network.server.ChatRefreshPacket;

import java.lang.reflect.Field;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class FakePlayerCommand implements CommandInterface {

	@Override
	public String getCommand() {
		return "fsp";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"fsp"};
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public boolean isAdminOnly() {
		return true;
	}

	@Override
	public boolean onCommand(PlayerState playerState, final String[] strings) {
		try {
			String type = strings[0].toLowerCase().trim();
			switch(type) {
				case "add":
					FakePlayerLoginRequest fakePlayerLoginRequest = new FakePlayerLoginRequest(strings[1]);
					fakePlayerLoginRequest.run();
					EdenCore.getFakePlayers().add(fakePlayerLoginRequest.serverProcessor.getClient());
					(new StarRunnable() {
						@Override
						public void run() {
							for(PlayerState ps : GameServer.getServerState().getPlayerStatesByName().values()) PacketUtil.sendPacket(ps, new ChatRefreshPacket());
						}
					}).runLater(EdenCore.getInstance(), 3000);
					break;
				case "remove":
					PlayerState playerState1 = GameServer.getServerState().getPlayerFromNameIgnoreCase(strings[1]);
					if(playerState1 != null) {
						if(!GameServer.getServerState().filterJoinMessages()) GameServer.getServerState().getController().broadcastMessage(Lng.astr("%s left the game.", playerState1.getName()), ServerMessage.MESSAGE_TYPE_SIMPLE);
						EdenCore.getFakePlayers().remove(GameServer.getServerClient(playerState1));
						GameServer.getServerState().getController().unregister(playerState1.getClientId());
					}
					break;
				case "set_faction":
					PlayerState playerState2 = GameServer.getServerState().getPlayerFromNameIgnoreCase(strings[1]);
					if(playerState2 != null) playerState2.getNetworkObject().factionJoinBuffer.add(Integer.parseInt(strings[2]));
					break;
				case "chat":
					PlayerState playerState3 = GameServer.getServerState().getPlayerFromNameIgnoreCase(strings[1]);
					if(playerState3 != null) {
						FakeChannel fakeChannel = new FakeChannel(GameServer.getServerState(), ++ ChannelRouter.idGen, strings[1], GameServer.getServerState().getChannelRouter().getAllChannel().getClientChannelsInChannel());
						try {
							Field field = ChannelRouter.class.getDeclaredField("channels");
							field.setAccessible(true);
							Object2ObjectOpenHashMap<String, ChatChannel> channels = (Object2ObjectOpenHashMap<String, ChatChannel>) field.get(GameServer.getServerState().getChannelRouter());
							channels.put(strings[0], fakeChannel);
							field.set(GameServer.getServerState().getChannelRouter(), channels);
						} catch(Exception exception) {
							exception.printStackTrace();
						}
						PacketUtil.sendPacket(playerState, new ChatPacket(playerState3.getName()));
					}
					break;
			}
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		return true;
	}

	/**
	 * @param playerState
	 * @param strings
	 *
	 * @deprecated
	 */
	@Override
	public void serverAction(@Nullable PlayerState playerState, String[] strings) {

	}

	@Override
	public StarMod getMod() {
		return EdenCore.getInstance();
	}
}

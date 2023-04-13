package thederpgamer.edencore.data;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.schema.game.client.controller.ClientChannel;
import org.schema.game.common.data.chat.AllChannel;
import org.schema.game.network.objects.ChatMessage;
import org.schema.schine.network.StateInterface;

import java.util.Collection;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class FakeChannel extends AllChannel {

	private final String name;
	private final ObjectArrayList<ClientChannel> clientChannels = new ObjectArrayList<>();

	public FakeChannel(StateInterface stateInterface, int i, String name, Collection<ClientChannel> clientChannelsInChannel) {
		super(stateInterface, i);
		this.name = name;
		for(ClientChannel clientChannel : clientChannelsInChannel) onLoginServer(clientChannel);
	}

	@Override
	public String getUniqueChannelName() {
		return name;
	}

	@Override
	public void chat(String text) {
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.text = text;
		chatMessage.sender = name;
		chatMessage.receiver = getUniqueChannelName();
		chatMessage.receiverType = ChatMessage.ChatMessageType.CHANNEL;
		send(chatMessage);
	}
}

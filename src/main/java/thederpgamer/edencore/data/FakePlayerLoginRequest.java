package thederpgamer.edencore.data;

import api.StarLoaderHooks;
import api.common.GameServer;
import api.listener.events.network.ClientLoginEvent;
import api.mod.StarLoader;
import org.schema.common.LogUtil;
import org.schema.schine.common.language.Lng;
import org.schema.schine.network.IdGen;
import org.schema.schine.network.RegisteredClientOnServer;
import org.schema.schine.network.commands.Login;
import org.schema.schine.network.commands.LoginRequest;
import org.schema.schine.network.server.ServerMessage;

import java.io.IOException;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class FakePlayerLoginRequest extends LoginRequest {

	private RegisteredClientOnServer client;

	public FakePlayerLoginRequest(String playerName) {
		this.playerName = playerName;
	}

	@Override
	public void run() {
		try {
			this.login();
		} catch(IOException exception) {
			exception.printStackTrace();
		}
	}

	private void login() throws IOException {
		id = IdGen.getFreeStateId();
		state = GameServer.getServerState();
		serverProcessor = new FakeClientProcessor(null, state);
		System.err.println("[SERVER] PROCESSING LOGIN (" + playerName + ")");
		client = new RegisteredClientOnServer(id, playerName, state);
		client.setProcessor(serverProcessor);
		serverProcessor.setClient(client);
		synchronized(state) {
			state.setSynched();
			try {
				login = new Login();
				System.out.println("[SERVER][LOGIN] login received. returning login info for " + serverProcessor.getClient() + ": returnCode: " + 0);
				ClientLoginEvent event = new ClientLoginEvent(this, 0, false, version, serverProcessor, client, playerName);
				StarLoaderHooks.onClientLoginEvent(event);
				StarLoader.fireEvent(event, true);
				LogUtil.log().fine("[LOGIN] logged in " + serverProcessor.getClient() + " (" + serverProcessor.getIp() + ")");
				login.createReturnToClient(state, serverProcessor, (short) -1, id, System.currentTimeMillis(), "");
				if(!state.filterJoinMessages()) state.getController().broadcastMessage(Lng.astr("%s has joined the game.", playerName), ServerMessage.MESSAGE_TYPE_SIMPLE);
				Thread serverThread = new Thread(serverProcessor);
				serverThread.setDaemon(true);
				serverProcessor.setThread(serverThread);
				serverThread.setName("SERVER-Listener Thread (unknownId)");
				serverThread.start();
				GameServer.getServerState().getController().onLoggedIn(client);
				GameServer.getServerState().getController().registerClient(client, GameServer.getServerState().getVersion().trim() , new StringBuffer());
			} catch(Exception exception) {
				exception.printStackTrace();
			} finally {
				state.setUnsynched();
			}
		}
	}
}

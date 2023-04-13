package thederpgamer.edencore.data;

import org.schema.schine.network.server.ServerProcessor;
import org.schema.schine.network.server.ServerStateInterface;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class FakeClientProcessor extends ServerProcessor {

	public FakeClientProcessor(Socket socket, ServerStateInterface state) throws SocketException {
		super(socket, state);
	}

	@Override
	public boolean isConnectionAlive() {
		return true;
	}

	@Override
	public boolean isAlive() {
		return true;
	}

	@Override
	public String getIp() {
		Random random = new Random();
		return random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255) + ":4242";
	}

	@Override
	public void run() {
		//Do nothing
	}

	@Override
	public void closeSocket() throws IOException {
		//Do nothing
	}
}

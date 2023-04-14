package thederpgamer.edencore.data.event;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [11/05/2021]
 */
public interface EventUpdater {
	int getStatus();

	void start();

	void update(float deltaTime);

	void end();
}

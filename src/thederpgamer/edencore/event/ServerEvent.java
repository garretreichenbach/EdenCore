package thederpgamer.edencore.event;

import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.data.event.SquadData;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/26/2021]
 */
public abstract class ServerEvent {

  protected EventData eventData;
  protected SquadData[] squadData;

  public abstract boolean canStart();

  public abstract void start();

  public abstract void update();
}

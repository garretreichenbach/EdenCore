package thederpgamer.edencore.event.pvp;

import thederpgamer.edencore.data.event.SquadData;
import thederpgamer.edencore.event.ServerEvent;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/26/2021]
 */
public class TeamDeathMatch extends ServerEvent implements PvPEvent {

  @Override
  public boolean canStart() {
    for (SquadData squad : squadData) if (!squad.ready()) return false;
    return true;
  }

  @Override
  public void start() {}

  @Override
  public void update() {}
}

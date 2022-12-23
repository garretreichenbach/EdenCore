package thederpgamer.edencore.data.player;

/**
 * [Description]
 *
 * @author TheDerpGamer (TheDerpGamer#0027)
 */
public class DonatorData {

	public String name;
	public long discordId;
	public String tier;

	public DonatorData(String name, long discordId, String tier) {
		this.name = name;
		this.discordId = discordId;
		this.tier = tier;
	}
}

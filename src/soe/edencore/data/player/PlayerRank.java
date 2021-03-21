package soe.edencore.data.player;

/**
 * PlayerRank.java
 * <Description>
 *
 * @author TheDerpGamer
 * @since 03/20/2021
 */
public class PlayerRank implements Comparable<PlayerRank> {

    public String rankName;
    public String chatPrefix;
    public int rankLevel;

    public PlayerRank(String rankName, String chatPrefix, int rankLevel) {
        this.rankName = rankName;
        this.chatPrefix = chatPrefix;
        this.rankLevel = rankLevel;
    }

    public PlayerRank(String rankName, String chatPrefix) {
        this(rankName, chatPrefix, 0);
    }

    @Override
    public int compareTo(PlayerRank compare) {
        return Integer.compare(rankLevel, compare.rankLevel);
    }
}

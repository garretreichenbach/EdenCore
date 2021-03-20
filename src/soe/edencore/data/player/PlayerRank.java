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
    public String chatSuffix;
    public int rankLevel;

    public PlayerRank(String rankName, String chatPrefix, String chatSuffix, int rankLevel) {
        this.rankName = rankName;
        this.chatPrefix = chatPrefix;
        this.chatSuffix = chatSuffix;
        this.rankLevel = rankLevel;
    }

    public PlayerRank(String rankName, String chatPrefix, String chatSuffix) {
        this(rankName, chatPrefix, chatSuffix, 0);
    }

    public PlayerRank(String rankName, String chatPrefix) {
        this(rankName, chatPrefix, "", 0);
    }

    @Override
    public int compareTo(PlayerRank compare) {
        return Integer.compare(rankLevel, compare.rankLevel);
    }
}

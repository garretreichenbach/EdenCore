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
    public RankType rankType;

    public PlayerRank(String rankName, String chatPrefix, RankType rankType, int rankLevel) {
        this.rankName = rankName;
        this.chatPrefix = chatPrefix;
        this.rankType = rankType;
        this.rankLevel = rankLevel;
    }

    public PlayerRank(String rankName, String chatPrefix, RankType rankType) {
        this(rankName, chatPrefix, rankType, 0);
    }

    public PlayerRank(String rankName, String chatPrefix) {
        this(rankName, chatPrefix, RankType.PLAYER);
    }

    @Override
    public int compareTo(PlayerRank compare) {
        return Integer.compare(rankLevel, compare.rankLevel);
    }

    public enum RankType {ALL, PLAYER, DONATOR, STAFF}
}

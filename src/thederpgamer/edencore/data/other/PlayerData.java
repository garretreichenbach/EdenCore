package thederpgamer.edencore.data.other;

import api.common.GameCommon;
import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.edencore.data.other.BankingTransactionLog;
import thederpgamer.edencore.utils.DataUtils;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Stores player information as persistent data.
 *
 * @version 2.0 - [09/08/2021]
 * @author TheDerpGamer
 */
public class PlayerData {

    public String playerName;
    public int factionId;
    public Vector3i lastRealSector;
    public Vector3f lastRealSectorPos;
    public Vector3f lastBuildSectorPos;

    public PlayerData(PlayerState playerState) {
        playerName = playerState.getName();
        factionId = playerState.getFactionId();
        lastRealSector = (playerState.getCurrentSector().length() > 100000 || DataUtils.isPlayerInAnyBuildSector(playerState)) ? getDefaultSector(playerState) : playerState.getCurrentSector();
        Transform tempTransform = new Transform();
        playerState.getWordTransform(tempTransform);
        lastRealSectorPos = tempTransform.origin;
        lastBuildSectorPos = new Vector3f();
    }

    public PlayerData(String playerName, int factionId, Vector3i lastRealSector, Vector3f lastRealSectorPos, Vector3f lastBuildSectorPos) {
        this.playerName = playerName;
        this.factionId = factionId;
        this.lastRealSector = lastRealSector;
        this.lastRealSectorPos = lastRealSectorPos;
        this.lastBuildSectorPos = lastBuildSectorPos;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof PlayerData && ((PlayerData) object).playerName.equals(playerName);
    }

    public String getFactionName() {
        return GameCommon.getGameState().getFactionManager().getFactionName(factionId);
    }

    public static Vector3i getDefaultSector(PlayerState playerState) {
        return (playerState.getFactionId() != 0) ? GameCommon.getGameState().getFactionManager().getFaction(playerState.getFactionId()).getHomeSector() : DataUtils.getSpawnSector();
    }

    //collection of banking transactions that player has sent or received.
    private List<BankingTransactionLog> transactions = new ArrayList<>();

    public void addTransaction(BankingTransactionLog transaction) {
        //neither sender nor receiver?
        String from = transaction.from.toLowerCase(Locale.ENGLISH);
        String to = transaction.to.toLowerCase(Locale.ENGLISH);
        String name = playerName.toLowerCase(Locale.ENGLISH);
        if (!from.equals(name) && !to.equals(name))
            return; //dont add.

        transactions.add(transaction);

        //remove oldest ones if list >10
        while (transactions.size()>10) {
            transactions.remove(0);
        }
    }

    public List<BankingTransactionLog> getTransactions() {
        return transactions;
    }
}
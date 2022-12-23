package thederpgamer.edencore.manager;

import api.common.GameCommon;

/**
 * Manages game state and data access.
 *
 * @author TheDerpGamer
 * @version 1.0 - [09/10/2021]
 */
public class DataManager {

    //Local Access Modifiers
    public static final int NONE = 0; //No data access
    public static final int READ = 1; //Can read data from a local source
    public static final int WRITE = 2; //Can write data to a local source

    //Client Access Modifiers
    public static final int VIEW = 4; //Can read data sent from a source that has READ access
    public static final int SEND = 8; //Can send data to a destination that has WRITE access
    public static final int UPDATE = 16; //Can updateClients data from a source that has REPLY access

    //Server Access Modifiers
    public static final int RECEIVE = 32; //Can receive data sent from a source that has SEND access
    public static final int REQUEST = 64; //Can request data from a source that has SEND access
    public static final int REPLY = 128; //Can reply to a source that has UPDATE access

    public enum GameStateType {
        NO_STATE(NONE),
        LOCAL(READ | WRITE),
        CLIENT(VIEW | SEND | UPDATE),
        SERVER(READ | WRITE | RECEIVE | REQUEST | REPLY);

        public int accessType;

        GameStateType(int accessType) {
            this.accessType = accessType;
        }
    }

    public static GameStateType getGameStateType() {
        if(GameCommon.isOnSinglePlayer()) return GameStateType.LOCAL;
        else if(GameCommon.isClientConnectedToServer()) return GameStateType.CLIENT;
        else if(GameCommon.isDedicatedServer()) return GameStateType.SERVER;
        else return GameStateType.NO_STATE;
    }

    public static boolean canRead() {
        return (getGameStateType().accessType & READ) == READ;
    }

    public static boolean canWrite() {
        return (getGameStateType().accessType & WRITE) == WRITE;
    }

    public static boolean canView() {
        return (getGameStateType().accessType & VIEW) == VIEW;
    }

    public static boolean canSend() {
        return (getGameStateType().accessType & SEND) == SEND;
    }

    public static boolean canUpdate() {
        return (getGameStateType().accessType & UPDATE) == UPDATE;
    }

    public static boolean canReceive() {
        return (getGameStateType().accessType & RECEIVE) == RECEIVE;
    }

    public static boolean canRequest() {
        return (getGameStateType().accessType & REQUEST) == REQUEST;
    }

    public static boolean canReply() {
        return (getGameStateType().accessType & REPLY) == REPLY;
    }
}

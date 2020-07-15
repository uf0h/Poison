package me.ufo.poison.common;

import java.io.Serializable;

public final class ServerInfo implements Serializable {

    private static final long serialVersionUID = -864897153203655658L;

    private final String serverID;
    private int players;

    public ServerInfo(String serverID) {
        this.serverID = serverID;
    }

    public String getServerID() {
        return serverID;
    }

    public int getPlayers() {
        return this.players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

}

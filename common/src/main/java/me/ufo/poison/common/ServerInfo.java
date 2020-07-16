package me.ufo.poison.common;

import java.io.Serializable;

public final class ServerInfo implements Serializable {

    private static final long serialVersionUID = -864897153203655658L;

    private final String serverID;
    private final int onlinePlayers;
    private final ServerStatus serverStatus;

    public ServerInfo(String serverID, int onlinePlayers, ServerStatus serverStatus) {
        this.serverID = serverID;
        this.onlinePlayers = onlinePlayers;
        this.serverStatus = serverStatus;
    }

    public String getServerID() {
        return serverID;
    }

    public int getOnlinePlayers() {
        return this.onlinePlayers;
    }

}

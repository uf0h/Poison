package me.ufo.poison.shared;

import java.util.HashSet;
import java.util.Set;

public final class ServerData {

    private static final Set<ServerData> SERVERS = new HashSet<>(8);

    private final String serverID;
    private final ServerType serverType;

    private int onlinePlayers;
    private ServerStatus serverStatus = ServerStatus.OFFLINE;
    private long lastPinged;

    public ServerData(String serverID, ServerType serverType) {
        this.serverID = serverID;
        this.serverType = serverType;

        SERVERS.add(this);
    }

    public static ServerData getByName(String name) {
        for (ServerData data : SERVERS) {
            if (name.equalsIgnoreCase(data.getServerID())) {
                return data;
            }
        }
        return null;
    }

    public static Set<ServerData> getServers() {
        return SERVERS;
    }

    public String getServerID() {
        return this.serverID;
    }

    public int getOnlinePlayers() {
        return this.onlinePlayers;
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public ServerStatus getServerStatus() {
        return this.serverStatus;
    }

    public void setServerStatus(ServerStatus serverStatus) {
        this.serverStatus = serverStatus;
    }

    public long getPinged() {
        return this.lastPinged;
    }

    public void setLastPinged(long lastPinged) {
        this.lastPinged = lastPinged;
    }

    public boolean isDestinationServer() {
        return this.serverType == ServerType.DESTINATION;
    }

    public boolean isLobbyServer() {
        return this.serverType == ServerType.LOBBY;
    }

}

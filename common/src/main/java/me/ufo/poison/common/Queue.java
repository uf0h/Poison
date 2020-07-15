package me.ufo.poison.common;

public final class Queue {

    private final ServerInfo serverInfo;

    public Queue(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public ServerInfo getServerInfo() {
        return this.serverInfo;
    }

}

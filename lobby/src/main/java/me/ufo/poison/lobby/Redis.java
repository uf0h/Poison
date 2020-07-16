package me.ufo.poison.lobby;

import me.ufo.poison.shared.RedisAction;
import me.ufo.poison.shared.RedisProvider;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;

public final class Redis implements RedisProvider {

    private final RedissonClient redissonClient;

    public Redis(String address, int port) {
        this.redissonClient = Redisson.create(this.getRedissonConfig(address, port));
    }

    @Override
    public <T> void publish(RedisAction action, T object) {
        this.redissonClient.getTopic("POISON:" + action.toString()).publish(object);
    }

    @Override
    public RedissonClient getRedisson() {
        return this.redissonClient;
    }

    @Override
    public void close() {
        this.redissonClient.shutdown();
    }

}

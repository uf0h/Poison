package me.ufo.poison.bungee;

import me.ufo.poison.common.RedisAction;
import me.ufo.poison.common.RedisProvider;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

public final class Redis implements RedisProvider {

    private final RedissonClient redissonClient;

    public Redis(String address, int port) {
        this.redissonClient = Redisson.create(this.getRedissonConfig(address, port));
    }

    @Override
    public <T> void action(RedisAction action, T object) {
        final RTopic topic = this.redissonClient.getTopic(action.toString());
        topic.publishAsync(object);
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

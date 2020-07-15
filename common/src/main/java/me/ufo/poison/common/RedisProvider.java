package me.ufo.poison.common;

import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public interface RedisProvider extends AutoCloseable {

    <T> void action(RedisAction action, T object);

    RedissonClient getRedisson();

    default Config getRedissonConfig(String address, int port) {
        final Config config = new Config();
        config.useSingleServer().setAddress("redis://" + address + ":" + port);
        return config;
    }

    @Override
    void close() throws Exception;

}

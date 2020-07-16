package me.ufo.poison.shared;

import org.redisson.config.Config;

public interface RedisProvider extends AutoCloseable {

    <T> void publish(RedisAction action, T object);

    <T> T getRedisson();

    default Config getRedissonConfig(String address, int port) {
        final Config config = new Config();
        config.useSingleServer().setAddress("redis://" + address + ":" + port);
        return config;
    }

    @Override
    void close() throws Exception;

}

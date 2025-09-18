package com.example.booking.service;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonMultiLock;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RedisLockService {
    private final RedissonClient redissonClient;

    public RedisLockService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public RLock acquireMultiLock(List<String> keys, long waitSec, long leaseSec) throws InterruptedException {
        RLock[] locks = keys.stream().map(redissonClient::getLock).toArray(RLock[]::new);
        RedissonMultiLock multiLock = new RedissonMultiLock(locks);
        boolean ok = multiLock.tryLock(waitSec, leaseSec, TimeUnit.SECONDS);
        return ok ? multiLock : null;
    }

    public void release(org.redisson.api.RLock lock) {
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}

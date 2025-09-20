package com.example.catalog.component;

import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Profile({"PRODUCTION", "TEST"})
public class RedisLockManager implements LockManager {

    @Autowired
    private RedissonClient redissonClient;

    @Value("${lock.waitSec:10}")
    private long defaultWaitSec;

    @Value("${lock.ttlSec:60}")
    private long defaultTtlSec;


    @Override
    public LockHandle acquireLocks(List<String> keys) {
        return acquireLocks(keys, defaultWaitSec, defaultTtlSec);
    }

    @Override
    public LockHandle acquireLocks(List<String> keys, long waitSec, long ttlSec) {
        RLock[] locks = keys.stream().map(redissonClient::getLock).toArray(RLock[]::new);
        RedissonMultiLock multiLock = new RedissonMultiLock(locks);

        boolean ok = false;
        try {
            ok = multiLock.tryLock(waitSec, ttlSec, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return ok ? new RedisLockHandle(multiLock) : null;
    }

    @Override
    public void releaseLock(LockHandle lockHandle) {
        if (lockHandle instanceof RedisLockHandle redisLock) {
            RLock rLock = redisLock.getRLock();
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    private static class RedisLockHandle implements LockHandle {
        private final RLock rLock;

        private RedisLockHandle(RLock rLock) {
            this.rLock = rLock;
        }

        public RLock getRLock() {
            return rLock;
        }
    }
}

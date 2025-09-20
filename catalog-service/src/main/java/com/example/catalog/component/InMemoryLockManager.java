package com.example.catalog.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A simple in-memory lock manager implementation.
 * <p>
 * - Works only inside a single JVM instance (not distributed).
 * - Useful for local development/testing (dev/local profile).
 * - For production use, prefer a distributed lock manager (e.g., Redis/Redisson).
 */
@Component
@Profile({"!PRODUCTION", "!TEST"}) // active only for dev/local, not production/test
public class InMemoryLockManager implements LockManager {

    /**
     * Default time (in seconds) to wait for acquiring a lock.
     * Configurable via property: lock.waitSec
     */
    @Value("${lock.waitSec:10}")
    private long defaultWaitSec;

    /**
     * Default TTL (time to live in seconds) for acquired locks.
     * After TTL, the lock will expire automatically.
     * Configurable via property: lock.ttlSec
     */
    @Value("${lock.ttlSec:60}")
    private long defaultTtlSec;

    /**
     * Map of lock key → expiry timestamp (in millis).
     * ConcurrentHashMap ensures thread-safe access without synchronized blocks.
     */
    private final ConcurrentMap<String, Long> locks = new ConcurrentHashMap<>();

    @Override
    public LockHandle acquireLocks(List<String> keys) {
        return acquireLocks(keys, defaultWaitSec, defaultTtlSec);
    }

    @Override
    public LockHandle acquireLocks(List<String> keys, long waitSec, long ttlSec) {
        long deadline = System.nanoTime() + waitSec * 1_000_000_000L;

        while (System.nanoTime() < deadline) {
            long now = System.currentTimeMillis();

            synchronized (locks) {
                // Clean expired locks
                locks.entrySet().removeIf(e -> e.getValue() < now);

                // Check if all requested keys are free
                boolean allFree = keys.stream().noneMatch(locks::containsKey);
                if (allFree) {
                    // Acquire all keys with expiry
                    long expiry = now + ttlSec * 1000;
                    keys.forEach(k -> locks.put(k, expiry));
                    return new InMemoryLockHandle(keys, this);
                }
            }

            // Calculate remaining time in millis
            long remainingMillis = (deadline - System.nanoTime()) / 1_000_000L;
            if (remainingMillis <= 0) {
                break;
            }

            // Sleep for the smaller of remaining time or a fixed retry interval (e.g., 50ms)
            try {
                Thread.sleep(Math.min(remainingMillis, 50));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for lock", e);
            }
        }

        throw new RuntimeException("Could not acquire lock within waitSec=" + waitSec + " for keys=" + keys);
    }

    @Override
    public void releaseLock(LockHandle lockHandle) {
        if (lockHandle instanceof InMemoryLockHandle handle) {
            // Remove all keys held by this lock
            handle.keys.forEach(locks::remove);
        }
    }

    /**
     * Represents a handle to the acquired locks.
     * Calling release() will release the locks.
     */
    private record InMemoryLockHandle(List<String> keys, InMemoryLockManager manager) implements LockHandle {

    }
}

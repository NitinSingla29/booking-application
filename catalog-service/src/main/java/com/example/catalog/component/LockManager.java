package com.example.catalog.component;

import java.util.List;

public interface LockManager {

    /**
     * Acquire a distributed lock (or multi-lock) on given keys.
     * Waits indefinitely until the lock is acquired.
     *
     * @param keys keys to lock
     * @return lock handle
     */
    LockHandle acquireLocks(List<String> keys);

    /**
     * Acquire a distributed lock (or multi-lock) on given keys.
     *
     * @param keys    keys to lock
     * @param waitSec maximum time to wait for acquiring the lock
     * @param ttlSec  lock time-to-live in seconds
     * @return lock handle if acquired, or null if not acquired
     */
    LockHandle acquireLocks(List<String> keys, long waitSec, long ttlSec);

    /**
     * Release the lock
     *
     * @param lockHandle lock handle
     */
    void releaseLock(LockHandle lockHandle);
}


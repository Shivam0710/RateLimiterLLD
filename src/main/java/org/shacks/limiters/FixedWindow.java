package org.shacks.limiters;

import java.util.concurrent.locks.ReentrantLock;

public class FixedWindow extends RateLimit {
    private int maxRequests;
    private int windowIntervalSize;
    private long lastRequestTimeInMillis;
    private int availableRequests;

    private final ReentrantLock locker = new ReentrantLock();

    public FixedWindow(int maxRequests, int windowIntervalSize) {
        this.maxRequests = maxRequests;
        this.windowIntervalSize = windowIntervalSize;
        this.availableRequests = maxRequests;
        this.lastRequestTimeInMillis = System.currentTimeMillis();
    }

    @Override
    public boolean TryConsume(int token) {
        locker.lock();
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastRequestTimeInMillis >= windowIntervalSize) {
            availableRequests = maxRequests;
            lastRequestTimeInMillis = currentTime;
        }

        try {
            if(availableRequests >= token) {
                availableRequests -= token;
                return true;
            }
        } finally {
            locker.unlock();
        }

        return false;
    }
}

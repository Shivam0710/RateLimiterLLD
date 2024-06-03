package org.shacks.limiters;

import java.util.concurrent.locks.ReentrantLock;

public class LeakyBucket extends RateLimit {

    private int capacity;
    private int leakRate;
    private long intervalInMs;
    private double currentTokens;
    private long lastRequestTime;

    ReentrantLock locker = new ReentrantLock();

    public LeakyBucket(int capacity, int leakRate, long intervalInMs) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.intervalInMs = intervalInMs;
        this.currentTokens = 0;
        this.lastRequestTime = System.currentTimeMillis();
    }

    public boolean TryConsume(int token) {
        long currentTime = System.currentTimeMillis();

        locker.lock();
        try {
            long elapsedTime = currentTime - lastRequestTime;

            double leakedTokens = (elapsedTime / (double) intervalInMs) * leakRate;
            currentTokens = Math.max(0, currentTokens - leakedTokens);
            lastRequestTime = currentTime;

            if(currentTokens + token <= capacity) {
                currentTokens += token;
                return true;
            } else {
                return false;
            }
        } finally {
            locker.unlock();
        }
    }
}

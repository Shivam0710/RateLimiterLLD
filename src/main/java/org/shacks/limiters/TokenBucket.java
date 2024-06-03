package org.shacks.limiters;

import java.util.concurrent.locks.ReentrantLock;

public class TokenBucket extends RateLimit {
    private int maxTokens;
    private int availableTokens;
    private int refillInterval;
    private int tokensPerInterval;
    private long lastRefillTime;

    private final ReentrantLock locker = new ReentrantLock();

    public TokenBucket(int maxTokens, int refillInterval, int tokensPerInterval) {
        this.maxTokens = maxTokens;
        this.refillInterval = refillInterval;
        this.tokensPerInterval = tokensPerInterval;
        this.availableTokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }

    public boolean TryConsume(int tokens) {
        locker.lock();
        try {
            refillToken();
            if(availableTokens >= tokens) {
                availableTokens -= tokens;
                return true;
            }

            return false;
        } finally {
            locker.unlock();
        }
    }

    private void refillToken() {
        long now = System.currentTimeMillis();
        long timeLeftSinceLastInterval = now - lastRefillTime;
        if(timeLeftSinceLastInterval > refillInterval) {
            int numberOfIntervalPassed = (int) (timeLeftSinceLastInterval / refillInterval);
            int newTokensToBeAdded = numberOfIntervalPassed * tokensPerInterval;
            availableTokens = Math.min(maxTokens, newTokensToBeAdded + availableTokens);
            lastRefillTime = now;
        }
    }
}

package org.shacks.limiters;

import java.util.concurrent.locks.ReentrantLock;

public class SlidingWindowCounter extends RateLimit {

    private int maxRequests;
    private long windowIntervalInMs;
    private int bucketSize;
    private int[] buckets;
    private long startTime;
    private int currentBucket;
    private int numberOfBuckets;

    private final ReentrantLock locker = new ReentrantLock();

    public void SlidingWindow(int maxRequests, long windowIntervalInMs, int bucketSize) {
        this.maxRequests = maxRequests;
        this.windowIntervalInMs = windowIntervalInMs;
        this.bucketSize = bucketSize;
        this.startTime = System.currentTimeMillis();
        this.currentBucket = 0;
        this.numberOfBuckets = (int) Math.ceil((double) windowIntervalInMs/bucketSize);
        this.buckets = new int[numberOfBuckets];
    }

    public boolean TryConsume(int token) {
        long currentTime = System.currentTimeMillis();
        locker.lock();
        try {
            int elapsedBuckets = (int) ((currentTime - startTime) / bucketSize);

            if(elapsedBuckets > 0) {
                startTime += (long) elapsedBuckets * bucketSize;

                for(int i=0; i<Math.min(elapsedBuckets, numberOfBuckets); i++) {
                    buckets[(currentBucket + i)%numberOfBuckets] = 0;
                }

                currentBucket = (currentBucket + elapsedBuckets)%numberOfBuckets;
            }

            int totalCount = 0;
            for(int count: buckets) {
                totalCount += count;
            }

            if(totalCount + token <= maxRequests) {
                buckets[currentBucket] += token;
                return true;
            } else {
                return false;
            }
        } finally {
            locker.unlock();
        }
    }
}

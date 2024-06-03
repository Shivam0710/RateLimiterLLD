package org.shacks.limiters;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class SlidingWindow extends RateLimit {
    private int maxRequest;
    private long windowIntervalSizeInMs;
    private Queue<Long> timestamps;

    private final ReentrantLock locker = new ReentrantLock();

    public SlidingWindow(int maxRequest, long windowIntervalSizeInMs, int i) {
        this.maxRequest = maxRequest;
        this.windowIntervalSizeInMs = windowIntervalSizeInMs;
        this.timestamps = new LinkedList<>();
    }

    public boolean TryConsume(int token) {
        long currentTime = System.currentTimeMillis();
        locker.lock();
        try {
            while(!timestamps.isEmpty() && timestamps.peek() <= currentTime - windowIntervalSizeInMs) {
                timestamps.poll();
            }

            if(timestamps.size() + token <= maxRequest) {
                for(int i=0; i<token; i++) {
                    timestamps.add(currentTime);
                }
                return true;
            } else {
                return false;
            }
        } finally {
            locker.unlock();
        }


    }
}

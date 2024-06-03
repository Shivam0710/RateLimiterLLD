package org.shacks;

import org.shacks.limiters.*;

import java.util.Random;

public class Main {
    public static void main(String[] args) throws InterruptedException {
//        TokenBucket rateLimiter = new TokenBucket(2, 1000, 2);
        RateLimit rateLimiter = new LeakyBucket(2, 2, 1000);

        for(int i=0; i<15; i++) {
            if(rateLimiter.TryConsume(1)) {
                System.out.println("$Request " + (i+1) + " allowed");
            } else {
                System.out.println("$Request " + (i+1) + " blocked");
            }

//            int delay  = new Random().nextInt(100, 1000);
//            System.out.println("Adding delay of: " + delay);
            if(i == 4) Thread.sleep(1000);
        }
    }
}
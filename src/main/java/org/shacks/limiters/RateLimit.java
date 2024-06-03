package org.shacks.limiters;

public abstract class RateLimit {
    public boolean TryConsume(int token) {return false;};
}

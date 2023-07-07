package com.example.pet_platform.util;

public interface ILock {
    boolean tryLock(long timeoutSec);
    void unlock();
}

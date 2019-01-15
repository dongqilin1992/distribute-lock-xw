package com.dongqilin.provider.impl;

import com.dongqilin.api.IUserProvider;
import com.dongqilin.curator.CuratorLock;
import com.dongqilin.redisson.RedissonLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: dongql
 * @date: 2017/10/24 13:36
 */

public class UserProviderImpl implements IUserProvider {

    @Override
    public void findRedisLock() {
        RLock lock = RedissonLock.getRedisson().getLock("TEST_KEY");

        try {
            boolean res = lock.tryLock(10, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void findZkLock() {
        InterProcessMutex locks = CuratorLock.getLock();
        try {
            locks.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                locks.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

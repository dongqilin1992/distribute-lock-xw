package com.dongqilin.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @description:
 * @author: dongql
 * @date: 2018/3/15 14:38
 */
public class CuratorLock {
    private static InterProcessMutex lock;

    public static void main(String[] args) throws Exception {
        final CuratorLock curatorLock = new CuratorLock();
        for (int i = 0; i < 60; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    curatorLock.seckill();
                }
            }).start();

        }
    }

    static {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory
                .newClient("127.0.0.1:2181", retryPolicy);
        client.start();
        System.out.println(client.toString());
        lock = new InterProcessMutex(client,
                "/mylock");

    }

    public static InterProcessMutex getLock() {
        return lock;
    }


    public void seckill() {
        InterProcessMutex locks = getLock();
        try {
            // 返回锁的value值，供释放锁时候进行判断
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



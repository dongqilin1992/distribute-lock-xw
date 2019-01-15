package com.dongqilin.service;

import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

import static com.dongqilin.service.RedissonLock.getRedisson;


/**
 * @description:
 * @author: dongql
 * @date: 2018/3/15 16:42
 */
public class RedissonLock {
    private static RedissonClient redissonClient;


    public static void main(String[] args) throws InterruptedException {
        RedissonLock service = new RedissonLock();
        RAtomicLong atomicLong = getRedisson().getAtomicLong("myAtomicLong");
        atomicLong.set(50);
        for (int i = 0; i < 60; i++) {
            //启动10个线程模拟多个客户端
            Jvmlock jl = new Jvmlock(i, service, atomicLong);
            new Thread(jl).start();
            //这里加上300毫秒是为了让线程按顺序启动，不然有可能4号线程比3号线程先启动了，这样测试就不准了。
            //Thread.sleep(300);
        }
        Thread.sleep(2000);
        getRedisson().shutdown();

    }

    static {
        Config config = new Config();
        config.useSingleServer().setAddress("localhost:6379");

        redissonClient = Redisson.create(config);

    }

    public static RedissonClient getRedisson() {
        return redissonClient;
    }


    public void secondKill(RAtomicLong atomicLong) {
        if (atomicLong.get() > 0) {
            System.out.println("秒杀剩余商品：" + atomicLong.decrementAndGet());
        } else {
            System.out.println("商品已经卖完");
        }
    }
}


class Jvmlock implements Runnable {
    private int num;
    private RedissonLock service;
    RAtomicLong atomicLong;

    public Jvmlock(int num, RedissonLock service, RAtomicLong atomicLong) {
        this.num = num;
        this.service = service;
        this.atomicLong = atomicLong;
    }

    public void run() {
        RLock lock = getRedisson().getLock("TEST_KEY");
        //lock（）方法会一直阻塞申请锁资源，直到有可用的锁释放
        //      fairLock.lock();
        // 尝试加锁，最多等待10秒，上锁以后10秒自动解锁
        //tryLock(long waitTime, long leaseTime, TimeUnit unit)//有leaseTime参数的申请锁方法是会按照leaseTime时间来自动释放锁的
        try {
            //trylock方法提供了锁重入的实现，并且客户端一旦持有锁，就会在能正常运行期间一直持有锁，直到主动unlock或者节点故障，主动失效（超过默认的过期时间）释放锁
            boolean res = lock.tryLock(10, 10, TimeUnit.SECONDS);
            if (res) {    //成功
                System.out.println("我是第" + num + "号线程，我已经获取锁");
                service.secondKill(atomicLong);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            System.out.println("我是第" + num + "号线程，我已经释放锁");
        }
    }
}

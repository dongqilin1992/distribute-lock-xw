package com.dongqilin.test;


import com.dongqilin.service.CuratorLock;
import com.dongqilin.service.RedissonLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: dongql
 * @date: 2018/3/21 10:43
 */
public class JMHTest {
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHTest.class.getSimpleName()).warmupIterations(1)
                .measurementIterations(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)//计算一个时间单位内操作数量
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Threads(1)
    public void zk() {
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

    @Threads(1)
    @Benchmark
    @BenchmarkMode(Mode.Throughput)//计算一个时间单位内操作数量
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void redis() {
        RLock lock = RedissonLock.getRedisson().getLock("TEST_KEY");
        try {
            boolean res = lock.tryLock(100, 100, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

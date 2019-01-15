package com.dongqilin.test;

import com.dongqilin.api.IUserProvider;
import com.dongqilin.service.CuratorLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: dongql
 * @date: 2018/3/21 10:20
 */
@State(Scope.Thread)
public class JMHZk {
    private IUserProvider provider;
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHZk.class.getSimpleName()).warmupIterations(1)
                .measurementIterations(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
    @Setup
    public void startbubbo() throws InterruptedException {
        System.out.println("init dubbo");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:consumer.xml");
        context.start();
        provider = context.getBean(IUserProvider.class);
        System.out.println(provider);
        Thread.sleep(5000);
    }
    @Threads(1)
    @Benchmark
    @BenchmarkMode(Mode.Throughput)//计算一个时间单位内操作数量
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void restWithDubbo() {
        provider.findZkLock();
    }
    @Threads(1)
    @Benchmark
    @BenchmarkMode(Mode.Throughput)//计算一个时间单位内操作数量
    @OutputTimeUnit(TimeUnit.SECONDS)
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
    @TearDown
    public void stop() {

    }
}

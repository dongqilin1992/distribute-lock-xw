package com.dongqilin.test;

import com.dongqilin.api.IUserProvider;
import com.dongqilin.service.RedissonLock;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.redisson.api.RLock;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: dongql
 * @date: 2018/3/20 12:04
 */
@State(Scope.Thread)
public class JMHRedis {
    private IUserProvider provider;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHRedis.class.getSimpleName()).warmupIterations(1)
                .measurementIterations(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void startbubbo() throws InterruptedException {
        System.out.println("init dubbo");
        /*AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConsumerApplication.class);*/
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
        provider.findRedisLock();
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

    @TearDown
    public void stop() {

    }
}

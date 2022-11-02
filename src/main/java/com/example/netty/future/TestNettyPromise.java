package com.example.netty.future;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * @author huangzhenyu
 * @date 2022/11/2
 */
@Slf4j
public class TestNettyPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        EventLoop group = new NioEventLoopGroup(2).next();

        DefaultPromise<Integer> promise = new DefaultPromise<>(group);

        new Thread(new Runnable() {
            @Override
            public void run() {
                log.debug("开始计算...");
                try {
                    Thread.sleep(1000);

                    throw new RuntimeException("1111");

                } catch (Exception e) {
                    e.printStackTrace();
                    promise.setFailure(e);
                }
                promise.setSuccess(80);
            }
        }).start();


        log.debug("等待同步结果");
        log.debug("promise.get() = {}", promise.get());
//        log.debug("{}", promise.await());
    }
}

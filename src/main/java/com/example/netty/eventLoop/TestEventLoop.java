package com.example.netty.eventLoop;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author huangzhenyu
 * @date 2022/10/28
 */
@Slf4j
public class TestEventLoop {
    public static void main(String[] args) {
        // 创建事件循环组
        NioEventLoopGroup group = new NioEventLoopGroup(1);//io 事件、普通任务、定时任务
//        DefaultEventLoop eventExecutors = new DefaultEventLoop(); // 普通任务、定时任务

        log.info("core count:{}", NettyRuntime.availableProcessors());

//        for (int i = 0; i < 5; i++) {
//            System.out.println(group.next());
//        }

        // 执行普通任务
        group.next().execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("执行普通任务 ok");
        });
        // 启动一个定时任务
        group.next().scheduleAtFixedRate(() -> {
            log.info("启动一个定时任务 ok");
        }, 0, 1, TimeUnit.SECONDS);
    }
}

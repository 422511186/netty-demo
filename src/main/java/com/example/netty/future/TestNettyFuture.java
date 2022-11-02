package com.example.netty.future;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * @author huangzhenyu
 * @date 2022/11/2
 */
@Slf4j
public class TestNettyFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup(2);
        EventLoop loop = group.next();
        Future<Integer> future = loop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                try {
                    log.debug("计算...");
                    Thread.sleep(1000);
                    log.debug("计算完成...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 70;
            }
        });
       /* log.debug("等待同步结果");
        //阻塞方法，同步等待完成
        log.debug("future.get() = {}", future.get());*/

        //异步处理结果 回调
        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                log.debug("future.getNow() = {}", future.getNow());
            }
        });


        log.debug("关闭NioEventLoopGroup:{}", group);
        group.shutdownGracefully();
    }
}

package com.example.netty.future;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author huangzhenyu
 * @date 2022/11/2
 */
@Slf4j
public class TestJDKFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<Integer> future = service.submit(() -> {
            try {
                log.debug("计算...");
                Thread.sleep(1000);
                log.debug("计算完成...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 50;
        });
        log.debug("等待同步结果");
        //阻塞方法，同步等待完成
        log.debug("future.get() = {}", future.get());
        service.shutdown();
    }
}

package com.example.netty.eventLoop;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * netty 客户端
 *
 * @author huangzhenyu
 * @date 2022/10/27
 */
@Slf4j
public class CloseFutureClient {
    public static void main(String[] args) throws InterruptedException {
        Bootstrap client = new Bootstrap();

        NioEventLoopGroup group = new NioEventLoopGroup();
        client.group(group);
        client.channel(NioSocketChannel.class);
        client.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override //在连接建立后被调用
            protected void initChannel(NioSocketChannel ch) {
                //添加具体handler
                ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                ch.pipeline().addLast(new StringEncoder());   //编码器
            }
        });
        //连接服务器 异步非堵塞
        ChannelFuture connect = client.connect("localhost", 8080);

        /*主线程执行*/
        //阻塞方法，直到连接建立 返回值代表连接对象
        connect.sync();
        Channel channel = connect.channel();

        //发送消息线程
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                if ("q".equals(line)) {
                    log.info("close channel:{}", channel);
                    channel.close();
                }
                channel.writeAndFlush(line);
            }
        }, "Thread-Input").start();

        ChannelFuture closeFuture = channel.closeFuture();

        /*主线程处理*/
//        closeFuture.sync();
//        log.debug("channel has closed...");

        /*nio线程处理*/
        closeFuture.addListener((ChannelFutureListener) channelFuture -> {
            log.debug("channel has closed...");

            group.shutdownGracefully();
        });
    }
}

package com.example.netty.eventLoop;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author huangzhenyu
 * @date 2022/10/28
 */
@Slf4j
public class EventLoopServer {
    public static void main(String[] args) {

        // 创建一个独立的eventLoop处理耗时操作
        DefaultEventLoop eventLoop = new DefaultEventLoop();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 细分:EventLoopGroup boss和worker
        // boss只负责accept事件  worker负责socketChannel上的读写
        serverBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup(2));
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast("eventLoop-1", new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        ByteBuf buf = (ByteBuf) msg;
                        log.info(buf.toString(StandardCharsets.UTF_8));
                        /*让消息传递给下一个handler*/
                        ctx.fireChannelRead(msg);
                    }
                });
                ch.pipeline().addLast(eventLoop, "eventLoop-2", new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        ByteBuf buf = (ByteBuf) msg;
                        log.info(buf.toString(StandardCharsets.UTF_8));
                    }
                });

            }
        }).bind(8080);
    }
}

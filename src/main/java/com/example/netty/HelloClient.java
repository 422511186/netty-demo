package com.example.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * netty 客户端
 *
 * @author huangzhenyu
 * @date 2022/10/27
 */
@Slf4j
public class HelloClient {
    public static void main(String[] args) throws InterruptedException {
        Bootstrap client = new Bootstrap();
        client.group(new NioEventLoopGroup());
        client.channel(NioSocketChannel.class);
        client.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override //在连接建立后被调用
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                //添加具体handler
                nioSocketChannel.pipeline().addLast(new StringEncoder());   //编码器
            }
        });
        //连接服务器 异步非堵塞
        ChannelFuture connect = client.connect("localhost", 8080);
        /*阻塞方法，直到连接建立 返回值代表连接对象*/
        Channel channel = connect.sync().channel();
        //发送消息
        channel.writeAndFlush("hello word");
    }
}

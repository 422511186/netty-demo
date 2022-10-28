package com.example.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * netty 服务器
 *
 * @author huangzhenyu
 * @date 2022/10/27
 */
@Slf4j
public class HelloServer {
    public static void main(String[] args) {
        /*服务器端启动器， 负责组装netty组件，启动服务器*/
        ServerBootstrap server = new ServerBootstrap();
        server.group(new NioEventLoopGroup());
        /*选择 服务器的ServerSocketChannel实现*/
        server.channel(NioServerSocketChannel.class);
        /*boss 负责连接 worker（child）负责读写,决定 worker 能执行哪些操作 handler*/
        server.childHandler(
                /*channel 代表和客户端进行数据读写的通道 Initializer 初始化 负责添加别的handler*/
                new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        /*添加具体handler*/
                        nioSocketChannel.pipeline().addLast(new StringDecoder());   /*将传输ByteBuf 转换为字符串*/
                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {    /*自定义handler*/
                            /* 读事件 */
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                /*打印上一步转换好的字符串*/
                                log.info("msg:{} ", msg);
                            }
                        });
                    }
                });
        /*绑定端口号*/
        server.bind(8080);
    }
}

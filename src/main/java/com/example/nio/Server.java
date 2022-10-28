package com.example.nio;

import com.example.utils.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author huangzhenyu
 * @date 2022/10/26
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {
        serverSelector();
    }

    private static void serverSelector() throws IOException {
        /*创建selector， 管理多个channel*/
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();

        /*设置非阻塞*/
        ssc.configureBlocking(false);

        /*建立 selector 和 channel 的联系 （注册） */
        /* SelectionKey 就是将来事件发生后，通过它可以知道事件和哪个channel的事件*/
        SelectionKey sscKey = ssc.register(selector, 0, null);

        /*key只关注 accept 事件*/
        sscKey.interestOps(SelectionKey.OP_ACCEPT);

        log.info("register key:{}", sscKey);

        /*监听8080端口*/
        ssc.bind(new InetSocketAddress(8080));

        while (true) {
            /* select 方法，没有事件发生，线程阻塞，有事件，线程才会回复运行*/
            /* select 在事件未处理时，不会堵塞*/
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                /*移除集合中的key*/
                iterator.remove();

                log.info("key:{}", key);

                /*不处理事件*/
//                key.cancel();

                /*需要对不同的事件做分路处理*/
                if (key.isAcceptable()) {   /*如果是accept事件*/
                    /*处理事件*/
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);

                    ByteBuffer buffer = ByteBuffer.allocate(16);

                    /*注册客户端到selector*/
                    /*将一个buffer作为附件关联到 SelectionKey */
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    /*设置监听 read 事件*/
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.info("{}", sc);
                } else if (key.isReadable()) {   /*如果是read事件*/
                    /*捕获异常，防止服务端程序因为异常终止*/
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        /*获取SelectionKey的附件*/
                        ByteBuffer buffer = (ByteBuffer) key.attachment();

                        int read = channel.read(buffer);
                        log.info("read len:{}", read);
                        /*如果是正常断开*/
                        if (read == -1) {
                            log.info("断开连接:{}", key);
                            key.cancel();
                        } else {
                            split(buffer);
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                /*切换至读模式*/
                                buffer.flip();
                                newBuffer.put(buffer);
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        /*出现异常，断开客户端连接，取消key*/
                        key.cancel();
                    }
                }
            }
        }
    }

    private static void server() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(16);
        /*创建一个服务器*/
        ServerSocketChannel ssc = ServerSocketChannel.open();
        /*绑定监听端口*/
        ssc.bind(new InetSocketAddress(8080));
        /*设置非阻塞*/
        ssc.configureBlocking(false);
        ArrayList<SocketChannel> channels = new ArrayList<>();
        /*多线程打印消息*/
       /* new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (true) {
                    for (SocketChannel channel : channels) {
                        log.info("read...");
                        *//*接受客户端发送的数据*//*
                        channel.read(buffer);
                        buffer.flip();
                        ByteBufferUtil.debugRead(buffer);
                        buffer.clear();
                        log.info("after read...");
                    }
                }
            }
        }).start();*/

        while (true) {
//            log.info("connecting...");
            /*accept 建立与客户端之间的连接 SocketChannel 用来和客户端继进行通信 */
            SocketChannel sc = ssc.accept();

            if (sc != null) {
                /*设置客户端为非阻塞*/
                sc.configureBlocking(false);
                channels.add(sc);
                log.info("connected... {}", sc);
            }
            for (SocketChannel channel : channels) {
//                log.info("read...");
                //*接受客户端发送的数据*//
                int read = channel.read(buffer);
                if (read > 0) {
                    buffer.flip();
                    ByteBufferUtil.debugRead(buffer);
                    buffer.clear();
                    log.info("after read...");
                }
            }
        }
    }

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            // 找到一条完整消息
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                // 把这条完整消息存入新的 ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                // 从 source 读，向 target 写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                target.flip();
                System.out.print(Charset.defaultCharset().decode(target));
            }
        }
        source.compact();
    }
}

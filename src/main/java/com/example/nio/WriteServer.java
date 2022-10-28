package com.example.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author huangzhenyu
 * @date 2022/10/27
 */
public class WriteServer {
    public static void main(String[] args) throws IOException {

        /*创建selector， 管理多个channel*/
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();

        /*设置非阻塞*/
        ssc.configureBlocking(false);

        /*建立 selector 和 channel 的联系 （注册） */
        ssc.register(selector, SelectionKey.OP_ACCEPT, null);

        ssc.bind(new InetSocketAddress(8080));
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);

                    SelectionKey scKey = sc.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);

                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < 300; i++) {
                        sb.append(0);
                    }
                    ByteBuffer buffer = ByteBuffer.wrap(sb.toString().getBytes());
                    int write = sc.write(buffer);
                    System.out.println("write = " + write);
                    /*未写完*/
                    if (buffer.hasRemaining()) {
                        /*关注可写事件*/
                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                        /*附件*/
                        scKey.attach(buffer);
                    }
                } else if (key.isWritable()) {
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel sc = (SocketChannel) key.channel();
                    int write = sc.write(buffer);
                    System.out.println("write = " + write);
                    /*写完*/
                    if (!buffer.hasRemaining()) {
                        key.attach(null);
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                    }
                }
            }
        }

    }
}

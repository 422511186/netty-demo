package com.example.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author huangzhenyu
 * @date 2022/10/27
 */
public class WriteClient {
    public static void main(String[] args) throws IOException {
        /*创建一个服务器*/
        SocketChannel sc = SocketChannel.open();
        /*绑定监听端口*/
        sc.connect(new InetSocketAddress("127.0.0.1", 8080));
        int count = 0;
        while (true) {
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            int read = sc.read(buffer);
            count += read;
            System.out.println("count = " + count);
            buffer.clear();
        }
    }
}

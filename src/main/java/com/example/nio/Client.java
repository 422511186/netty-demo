package com.example.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author huangzhenyu
 * @date 2022/10/26
 */
@Slf4j
public class Client {
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        /*创建一个服务器*/
        SocketChannel sc = SocketChannel.open();

        /*绑定监听端口*/
        sc.connect(new InetSocketAddress("127.0.0.1", 8080));

        while (true) {
            System.out.println("输入 'EXIT' 断开连接");
            String in = scanner.next();
            /*输入为空*/
            if (in.length() == 0) {
                continue;
            }
            /*断开连接*/
            if (in.startsWith("EXIT")) {
                break;
            }
            /* 发送消息 */
            sc.write(ByteBuffer.wrap((in + "\n").getBytes(StandardCharsets.UTF_8)));
        }

        /* 断开连接 */
        sc.close();
    }
}

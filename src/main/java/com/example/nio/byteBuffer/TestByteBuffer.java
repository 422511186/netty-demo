package com.example.nio.byteBuffer;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author huangzhenyu
 * @date 2022/10/26
 */
@Slf4j
public class TestByteBuffer {

    public static void main(String[] args) {
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            /*准备缓存区*/
            ByteBuffer byteBuffer = ByteBuffer.allocate(10);
            while (true) {
                /*从channel读取数据到缓存区buffer*/
                int len = channel.read(byteBuffer);
                log.info("len:{}", len);
                if (len == -1) {
                    break;
                }
                /*打印buffer内容*/
                byteBuffer.flip();/*切换至读模式*/
                while (byteBuffer.hasRemaining()) {/*判断缓存区中是否还有未读数据*/
                    byte b = byteBuffer.get();
                    log.info("实际字节:{}", (char) b);
                }
                byteBuffer.clear();/*切换至写模式*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

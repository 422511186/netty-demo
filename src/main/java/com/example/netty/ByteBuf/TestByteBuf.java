package com.example.netty.ByteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.charset.StandardCharsets;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * byteBuf  可以开启池化功能    window下，4.1后: 默认是池化实现
 *
 * @author huangzhenyu
 * @date 2022/11/2
 */
public class TestByteBuf {
    public static void main(String[] args) {
        /*直接内存*/
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();

        /*堆内存*/
//        ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer();

        /*直接内存*/
//        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer();

        System.out.println("buf.getClass() = " + buf.getClass());

        log(buf);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 32; i++) {
            sb.append("a");
        }
        buf.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8));
        log(buf);
    }

    private static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2).append("read index:").append(buffer.readerIndex()).append(" write index:").append(buffer.writerIndex()).append(" capacity:").append(buffer.capacity()).append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf.toString());
    }
}

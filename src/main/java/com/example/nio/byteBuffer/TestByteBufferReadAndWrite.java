package com.example.nio.byteBuffer;

import com.example.utils.ByteBufferUtil;

import java.nio.ByteBuffer;

/**
 * @author huangzhenyu
 * @date 2022/10/26
 */
public class TestByteBufferReadAndWrite {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{0x61, 0x62, 0x63, 0x64});
        ByteBufferUtil.debugAll(buffer);

    }
}

package com.example.demo.bencode;

import com.example.demo.common.Constants;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BDecoder {

    public static Object decodeFromByte(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        return decodeFromBuffer(buffer);
    }

    public static Object decodeFromBuffer(ByteBuffer buffer) {
        while (buffer.hasRemaining()) {
            byte ch = buffer.get();
            switch (ch){
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    int length = 0;
                    while (ch != ':') {
                        length = length * 10 + (ch - '0');
                        ch = buffer.get();
                    }
                    buffer.position(buffer.position() + length);
                    return new String(buffer.array(), buffer.position() - length, length, Constants.BYTE_ENCODING);
                case 'i':
                    length = 0;
                    int offset = buffer.position();
                    while (buffer.get() != 'e') {
                        length++;
                    }
                    return Long.valueOf(new String(buffer.array(), offset, length, Constants.BYTE_ENCODING));
                case 'l':
                    List<Object> list = new LinkedList<>();
                    while (buffer.get() != 'e') {
                        buffer.position(buffer.position() - 1);
                        list.add(decodeFromBuffer(buffer));
                    }
                    return list;
                case 'd':
                    Map<String, Object> map = new LinkedHashMap<>();
                    while (buffer.get() != 'e') {
                        buffer.position(buffer.position() - 1);
                        map.put((String) decodeFromBuffer(buffer), decodeFromBuffer(buffer));
                    }
                    return map;
                default:
                    throw new RuntimeException("格式错误!");
            }
        }
        throw new RuntimeException("数据为空!");
    }
}

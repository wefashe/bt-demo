package com.example.demo.bencode;


import com.example.demo.common.Constants;

import java.util.List;
import java.util.Map;

public class BEncoder {

    public static byte[] encodeToByte(Object obj) {
        return encodeToString(obj).getBytes(Constants.BYTE_ENCODING);
    }

    public static String encodeToString(Object obj) {
        if (obj instanceof String) {
            StringBuffer buffer = new StringBuffer();
            String value = (String) obj;
            buffer.append(value.length()).append(':').append(value);
            return buffer.toString();
        } else if (obj instanceof Long) {
            StringBuffer buffer = new StringBuffer();
            Long value = (Long) obj;
            buffer.append("i").append(value).append("e");
            return buffer.toString();
        } else if (obj instanceof List) {
            StringBuffer buffer = new StringBuffer();
            List<Object> list = (List<Object>) obj;
            buffer.append("l");
            for (Object value : list) {
                buffer.append(encodeToString(value));
            }
            return buffer.append("e").toString();
        } else if (obj instanceof Map) {
            StringBuffer buffer = new StringBuffer();
            Map<String, Object> map = (Map<String, Object>) obj;
            buffer.append("d");
            for (Map.Entry<String, Object> value : map.entrySet()) {
                buffer.append(encodeToString(value.getKey()));
                buffer.append(encodeToString(value.getValue()));
            }
            return buffer.append("e").toString();
        } else {
            throw new RuntimeException("格式错误");
        }
    }
}

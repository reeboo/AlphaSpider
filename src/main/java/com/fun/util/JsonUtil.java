package com.fun.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 
 * 实现描述：Json处理方法，封装fastJson
 *
 * @author: reeboo
 * @since: 2016-08-16 19:25
 */
public class JsonUtil {

    public static byte[] marshalToByte(Object obj) {
        return JSON.toJSONBytes(obj); // 默认为UTF-8
    }

    public static byte[] marshalToByte(Object obj, SerializerFeature... features) {
        return JSON.toJSONBytes(obj, features); // 默认为UTF-8
    }

    public static String marshalToString(Object obj) {
        return JSON.toJSONString(obj); // 默认为UTF-8
    }

    public static String marshalToString(Object obj, SerializerFeature... features) {
        return JSON.toJSONString(obj, features); // 默认为UTF-8
    }

    /**
     * 可以允许指定一些过滤字段进行生成json对象
     */
    public static String marshalToString(Object obj, String... fliterFields) {
        final List<String> propertyFliters = Arrays.asList(fliterFields);
        SerializeWriter out = new SerializeWriter();
        try {
            JSONSerializer serializer = new JSONSerializer(out);
            serializer.getPropertyFilters().add(new PropertyFilter() {

                @Override
                public boolean apply(Object source, String name, Object value) {
                    return !propertyFliters.contains(name);
                }

            });
            serializer.write(obj);
            return out.toString();
        } finally {
            out.close();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T unmarshalFromByte(byte[] bytes, Class<T> targetClass) {
        return (T) JSON.parseObject(bytes, targetClass);// 默认为UTF-8
    }

    @SuppressWarnings("unchecked")
    public static <T> T unmarshalFromByte(byte[] bytes, TypeReference<T> type) {
        return (T) JSON.parseObject(bytes, type.getType());
    }

    public static <T> T unmarshalFromString(String json, Class<T> targetClass) {
        return JSON.parseObject(json, targetClass);// 默认为UTF-8
    }

    public static <T> T unmarshalFromString(String json, TypeReference<T> type) {
        return JSON.parseObject(json, type);// 默认为UTF-8
    }

    public static <T> List<T> unmarshalFromString2List(String json, Class<T> targetClass) {
        return JSON.parseArray(json, targetClass); // 默认为UTF-8
    }

    public static String descriptor(Object obj) {
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            String elem = Object.class.getName();
            if (list.size() > 0) {
                elem = list.get(0).getClass().getName();
            }
            return String.format("List(%s)", elem);
        }
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            String elem = Object.class.getName();
            if (map.size() > 0) {
                elem = map.values().iterator().next().getClass().getName();
            }
            return String.format("Map(%s)", elem);
        }
        return obj.getClass().getName();
    }

    public static Object parse(String json, String descriptor) {
        try {
            if (descriptor.startsWith("List")) {
                Class<?> clazz = Class.forName(descriptor.substring("List".length() + 1, descriptor.length() - 1));
                return JSON.parseArray(json, clazz);
            }
            if (descriptor.startsWith("Map")) {
                Class<?> clazz = Class.forName(descriptor.substring("Map".length() + 1, descriptor.length() - 1));
                JSONObject obj = JSON.parseObject(json);
                Map<String, Object> map = Maps.newLinkedHashMap();
                for (String key : obj.keySet()) {
                    map.put(key, obj.getObject(key, clazz));
                }
                return map;
            }
            Class<?> clazz = Class.forName(descriptor);
            return JSON.parseObject(json, clazz);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

}

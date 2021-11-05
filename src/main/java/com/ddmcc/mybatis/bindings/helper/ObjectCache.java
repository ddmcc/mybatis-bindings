package com.ddmcc.mybatis.bindings.helper;


import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.scripting.xmltags.OgnlCache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.SneakyThrows;

/**
 * 对象缓存
 *
 * @author jiangrz
 * @date 2021-10-28 19:34
 */
public final class ObjectCache {



    public static final Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();


    public static final String TEST_NAME = "aaaa";

    /**
     * Method Description
     * 
     * 
     * @return  Object 获取缓存对象
     * @author jiangrz
     * @date 2021-11-01 19:33
     */
    @SneakyThrows
    public static Object getInstance(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        if (OBJECT_MAP.containsKey(name)) {
            return OBJECT_MAP.get(name);
        }
        synchronized (name.intern()) {
            if (OBJECT_MAP.containsKey(name)) {
                return OBJECT_MAP.get(name);
            }

            return OBJECT_MAP.computeIfAbsent(name, (k) -> {
                String newObjectExp = String.format("new %s", StringUtils.join(name, StringPool.LEFT_BRACKET, StringPool.RIGHT_BRACKET));
                return OgnlCache.getValue(newObjectExp, ObjectCache.class);
            });
        }
    }
}

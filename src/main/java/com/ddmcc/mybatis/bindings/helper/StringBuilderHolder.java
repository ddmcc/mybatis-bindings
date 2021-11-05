package com.ddmcc.mybatis.bindings.helper;


/**
 * @author jiangrz
 */
public class StringBuilderHolder {


    private static final ThreadLocal<StringBuilder> THREAD_BUILDER = new ThreadLocal<>();



    public static StringBuilder get() {
        StringBuilder builder = THREAD_BUILDER.get();
        if (builder == null) {
            builder = new StringBuilder();
            THREAD_BUILDER.set(builder);
            return builder;
        }
        builder.setLength(0);
        return builder;
    }

    public static void remove() {
        THREAD_BUILDER.remove();
    }
}

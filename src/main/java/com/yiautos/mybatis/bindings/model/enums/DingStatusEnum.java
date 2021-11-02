package com.yiautos.mybatis.bindings.model.enums;

import java.util.HashMap;
import java.util.Map;



/**
 *
 *
 * 钉钉审批状态-Enum
 * @return
 * @author xuhb
 * @date 2021-09-18 17:44
 */
public enum DingStatusEnum {
    /**
     * APPROVALING-1-审核中
     */
    APPROVALING(1, "审核中"),
    /**
     * PASS-2-已通过
     */
    PASS(2, "已通过"),
    /**
     * REFUSE-3-已拒绝
     */
    REFUSE(3, "已拒绝"),
    /**
     * REVOKE-4-已撤销
     */
    REVOKE(4, "已撤销"),
    ;

    /**
     * 编码
     */
    private final int code;

    /**
     * 名称
     */
    private final String name;

    DingStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }


    /**
     * 通过code获取到对应的枚举值
     *
     * @param code 编码
     * @return Map<String, String>
     * @author hello
     * @date 2021-09-16 09:36
     */
    public static DingStatusEnum getEnumByCode(int code) {
        for (DingStatusEnum value : DingStatusEnum.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

    /**
     * 枚举转为key-value对象，使用code与name属性
     */
    private static final Map<String, String> KV_MAP = new HashMap<>(4);

    static {
        for (DingStatusEnum value : DingStatusEnum.values()) {
            KV_MAP.put(String.valueOf(value.getCode()), value.getName());
        }
    }

    /**
     * 获取Map对象
     *
     * @return Map<String, String>
     * @author hello
     * @date 2021-09-16 09:36
     */
    public static Map<String, String> getMap() {
        return KV_MAP;
    }


    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}

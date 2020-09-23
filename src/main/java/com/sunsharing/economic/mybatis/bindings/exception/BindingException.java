package com.sunsharing.economic.mybatis.bindings.exception;

/**
 * @author jiangrz
 */
public class BindingException extends RuntimeException {

    public BindingException() {
        super();
    }

    public BindingException(String message) {
        super(message);
    }

    public BindingException(String message, Throwable cause) {
        super(message, cause);
    }

}

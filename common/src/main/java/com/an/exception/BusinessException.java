package com.an.exception;

/**
 * @author: Mystery
 * @url: www.an.com
 * @date: 2025/11/14 20:29
 * @description:
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

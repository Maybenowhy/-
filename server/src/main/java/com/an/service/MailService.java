package com.an.service;

/**
 * @author: Mystery
 * @url: www.an.com
 * @date: 2025/11/14 21:29
 * @description:
 */
public interface MailService {
    void sendOrderDeliveredEmail(Long userId, String orderNumber);
}

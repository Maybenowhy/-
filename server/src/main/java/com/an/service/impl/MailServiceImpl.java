package com.an.service.impl;

import com.an.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: Mystery
 * @url: www.an.com
 * @date: 2025/11/14 21:29
 * @description:
 */
@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Override
    public void sendOrderDeliveredEmail(Long userId, String orderNumber) {
        // 你可以这里集成 SMTP 或第三方邮件服务
        log.info("发送邮件给用户 {}, 订单 {} 已确认收货", userId, orderNumber);
    }
}

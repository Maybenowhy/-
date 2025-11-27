package com.an.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DailySalesVO {
    private String date;
    private BigDecimal totalAmount;
}

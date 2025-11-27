package com.an.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserStatsVO {
    private Long total;      // 总用户数
    private Long active;     // 活跃用户数
    private Long newToday;   // 今日新增用户数
}

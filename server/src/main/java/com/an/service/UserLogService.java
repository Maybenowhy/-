package com.an.service;

import com.an.entity.UserLog;

import java.util.List;

public interface UserLogService {

    /** 查询日志 */
    List<UserLog> list(Long userId, String actionType, String startTime, String endTime);

    /** 查询某用户最近 N 条日志 */
    List<UserLog> recent(Long userId, Integer limit);

    void addLog(UserLog userLog);
}

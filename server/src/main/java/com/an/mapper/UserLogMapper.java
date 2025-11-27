package com.an.mapper;

import com.an.entity.UserLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: Mystery
 * @url: www.an.com
 * @date: 2025/11/16 23:43
 * @description:
 */
@Mapper
public interface UserLogMapper {

    /** 插入日志 */
    void insert(UserLog log);

    /** 查询日志列表，可按用户、操作类型、时间范围筛选 */
    List<UserLog> list(@Param("userId") Long userId,
                       @Param("actionType") String actionType,
                       @Param("startTime") String startTime,
                       @Param("endTime") String endTime);

    /** 查询某用户最近 N 条日志 */
    List<UserLog> recent(@Param("userId") Long userId,
                         @Param("limit") Integer limit);
}

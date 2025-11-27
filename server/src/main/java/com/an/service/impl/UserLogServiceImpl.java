package com.an.service.impl;

import com.an.entity.UserLog;
import com.an.mapper.UserLogMapper;
import com.an.service.UserLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserLogServiceImpl implements UserLogService {

    @Autowired
    private UserLogMapper userLogMapper;

    @Override
    public List<UserLog> list(Long userId, String actionType, String startTime, String endTime) {
        return userLogMapper.list(userId, actionType, startTime, endTime);
    }

    @Override
    public List<UserLog> recent(Long userId, Integer limit) {
        return userLogMapper.recent(userId, limit);
    }

    @Override
    public void addLog(UserLog userLog) {
        userLog.setCreatedAt(LocalDateTime.now());
        try{
            userLogMapper.insert(userLog);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

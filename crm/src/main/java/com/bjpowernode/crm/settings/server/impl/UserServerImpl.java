package com.bjpowernode.crm.settings.server.impl;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.mapper.UserMapper;
import com.bjpowernode.crm.settings.server.UserServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service("userserver")
public class UserServerImpl implements UserServer {
    @Autowired
    private UserMapper userMapper;
    public User queryUserByLoginActAndPwd(Map<String, Object> map) {
        return userMapper.selectUserByLoginActAndPwd(map);
    }
    public List<User> queryAllUsers(){
        return userMapper.selectAllUsers();
    }
}

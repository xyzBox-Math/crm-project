package com.bjpowernode.crm.settings.server;

import com.bjpowernode.crm.settings.domain.User;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;


public interface UserServer {
    User queryUserByLoginActAndPwd(Map<String,Object> map);
    List<User> queryAllUsers();
}

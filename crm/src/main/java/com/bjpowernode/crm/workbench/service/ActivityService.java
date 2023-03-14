package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    public int saveCreateActivity(Activity activity);
    public List<Activity> queryActivityByConditionForPage(Map<String,Object>map);

    public int queryCountByCondition(Map<String,Object>map);
    int deleteActivityByIds(String[] ids);

    Activity queryActivityById(String id);

    int saveUpDateActivity(Activity activity);

    List<Activity> queryAllActivities();

    List<Activity> querySomeActivities(String[] ids);

    int saveCreateActivityByList(List<Activity> list);
}

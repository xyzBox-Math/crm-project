package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.mapper.ActivityMapper;
import com.bjpowernode.crm.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("activityservice")
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private ActivityMapper activityMapper;

    @Override
    public List<Activity> queryActivityByConditionForPage(Map<String, Object> map) {
        return activityMapper.selectActivityByConditionForPage(map);
    }

    @Override
    public int queryCountByCondition(Map<String, Object> map) {
        return activityMapper.selectCountByCondition(map);
    }

    @Override
    public int saveCreateActivity(Activity activity){
        return activityMapper.insertActivity(activity);
    }

    @Override
    public int deleteActivityByIds(String[] ids) {
        return activityMapper.deleteActivityByIds(ids);
    }

    @Override
    public Activity queryActivityById(String id) {
        return activityMapper.selectActivityById(id);
    }

    @Override
    public int saveUpDateActivity(Activity activity) {
        return activityMapper.updateActivity(activity);
    }

    @Override
    public List<Activity> queryAllActivities() {
        return activityMapper.selectAllActivities();
    }

    @Override
    public List<Activity> querySomeActivities(String[] ids) {
        return activityMapper.selectSomeActivities(ids);
    }

    @Override
    public int saveCreateActivityByList(List<Activity> list) {
        return activityMapper.insertActivityByList(list);
    }
}

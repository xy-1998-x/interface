package com.appium.example.bean;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllTask
{
    // // 一个静态的字符串列表，用于存储应用名称
    public static final List<String> appList = new ArrayList<>();

    //   // 一个静态的映射，键为应用名称（字符串），值为任务列表
    private static final Map<String, List<Task>> app2Task = HashMap.newHashMap(16);

    //    // 向 appList 中添加应用名称的方法
    public void setAppList(String appName) {
        appList.add(appName);
    }
    //    返回一个包含字符串的列表 appList
    public List<String> getAppList() {
        return appList;
    }
    //    // 根据应用名称从 app2Task 映射中获取对应的任务列表
    public List<Task> getTask(String appName) {
        return app2Task.get(appName);
    }

    // 向指定应用名称对应的任务列表中添加任务，如果该应用的任务列表不存在则创建
    public void setTask(String appName, Task task) {
        List<Task> tasks;
        if (app2Task.get(appName) == null) {
            tasks = new ArrayList<>();
            app2Task.put(appName, tasks);
        } else {
            tasks = app2Task.get(appName);
        }
        tasks.add(task);
    }
}

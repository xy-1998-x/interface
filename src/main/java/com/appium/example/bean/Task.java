package com.appium.example.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Task {
    private String appName;
    private String taskName;

    //用于存储一系列的 Step 对象。使用 ArrayList 来实现动态存储，方便添加和管理多个 Step 对象。
    private List<Step> steps = new ArrayList<>();

    //这是一个公共方法，用于向 steps 列表中添加一个 Step 对象。
    public void setSteps(Step step) {
        steps.add(step);
    }
}

package com.appium.example.bean;

import com.appium.example.constant.FindType;
import com.appium.example.constant.OperateType;
import lombok.Data;
//在step这个类中定义了一系列的步骤
@Data
public class Step {
    private String appName;
    private String activityName;
    private String elementInfo;
    private FindType findType;
    private OperateType operateType;
    private String inputText;
    private String numsinfo;
    private String taskslist;
    private int index;
}

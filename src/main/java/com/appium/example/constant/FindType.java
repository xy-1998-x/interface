package com.appium.example.constant;
//定义了一个类包含了查找类型的方式
public enum FindType {
    ID("id"),
    CLASSNAME("classname"),
    DESC("desc"),
    XPATH("xpath");

    private String name;

    FindType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
//定义了四种查找元素的类型：ID、CLASSNAME、DESC 和 XPATH 。
//每个枚举值都关联了一个字符串 name ，通过构造函数进行初始化。
//getName 方法用于获取与枚举值关联的名称。
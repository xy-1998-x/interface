package com.appium.example.constant;

//在枚举中添加进来自己需要的函数
public enum OperateType {
    CLICK("click"),
    INPUT("input"),
    SCROLLDOWN("scrolldown"),
    SCROLLRIGHT("scrollright"),
    SCROLLTOTEXT("scrolltotext"),
    DOUBLECLICK("doubleclick"),
    FIND("find");
    //定义一下c操作

    private String name;    // 声明了一个私有字符串类型的成员变量 name ，用于存储每个枚举值对应的特定名称。

    OperateType(String name) {
        this.name = name;
    }   //枚举值的构造函数

    public String getName() {
        return name;
    }   //用于获取枚举值所关联的名称

}
//在自定义枚举中定义了两种操作类型：CLICK（点击）和 INPUT（输入）。
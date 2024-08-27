package com.appium.example.bean;

import lombok.Builder;
import lombok.Data;

@Data
//@Data 注解会为类自动生成诸如 getter 、 setter 、 equals 、 hashCode 、 toString 等方法，使类的属性更易于访问和操作。
@Builder
//@Builder 注解允许使用建造者模式来创建这个类的对象，使得对象的创建更加灵活和可读，能够按需设置属性的值。@Builder 注解允许使用建造者模式来创建这个类的对象，使得对象的创建更加灵活和可读，能够按需设置属性的值。
public class AppInfo {
    private String androidAppFilePath;
    private String androidAppPackage;
    private String androidAppActivity;
}

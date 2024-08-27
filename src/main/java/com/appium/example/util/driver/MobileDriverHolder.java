package com.appium.example.util.driver;

import io.appium.java_client.AppiumDriver;

public class MobileDriverHolder
{
    // 定义一个静态且最终的ThreadLocal对象driver，用于存储AppiumDriver类型的实例
    private static final ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();

    //// 静态方法getDriver，用于获取当前线程关联的AppiumDriver对象
    public static AppiumDriver getDriver()
    {
        return driver.get();
    }

    //// 静态方法setDriver，用于设置当前线程关联的AppiumDriver对象
    public static void setDriver(AppiumDriver driver) {
        MobileDriverHolder.driver.set(driver);
    }
}

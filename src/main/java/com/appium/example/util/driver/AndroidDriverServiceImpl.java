package com.appium.example.util.driver;

import com.appium.example.bean.AppInfo;
import com.appium.example.constant.DriverConstants;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;

public class AndroidDriverServiceImpl implements MobileDriverService {
    private AndroidDriver androidDriver;
    private AppInfo appInfo;

    @Override
    public void initAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    @Override
    //按照这些配置来启动 Android 驱动，这样可以确保在执行后续的操作时，有足够的时间等待页面元素加载和准备就绪。
    public void spinUpDriver(AppiumDriverLocalService appiumService, String deviceName) {
        //创建一个 UiAutomator2Options 对象，并为其设置了一系列的属性。
        UiAutomator2Options options = new UiAutomator2Options()
                .setUdid(deviceName)   //设置了设备的唯一标识符
                .setAppPackage(appInfo.getAndroidAppPackage())  //应用的包名
                .setAppActivity(appInfo.getAndroidAppActivity())    //应用的活动
                .setNoReset(Boolean.parseBoolean(DriverConstants.ANDROID_NO_RESET))
                .setFullReset(Boolean.parseBoolean(DriverConstants.ANDROID_FULL_RESET))
                .autoGrantPermissions();    //  权限
        options.setCapability("appium:forceAppLaunch", true);

        androidDriver = new AndroidDriver(appiumService.getUrl(), options);
        androidDriver.manage().timeouts().implicitlyWait(DriverConstants.APPIUM_DRIVER_TIMEOUT);
    }

    @Override
    public void closeDriver() {
        androidDriver.terminateApp(appInfo.getAndroidAppPackage());
        androidDriver.quit();
    }

    @Override
    public AppiumDriver getDriver() {
        return androidDriver;
    }
}

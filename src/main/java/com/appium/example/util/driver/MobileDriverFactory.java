package com.appium.example.util.driver;

import io.appium.java_client.service.local.AppiumDriverLocalService;

import java.security.InvalidParameterException;

import static com.appium.example.constant.CommonConstants.MOBILE_PLATFORM_NAME;
import static com.appium.example.constant.DriverConstants.ANDROID;
import static com.appium.example.constant.DriverConstants.IOS;

//根据不同的平台获取相应的驱动服务
public class MobileDriverFactory
{
    public MobileDriverService getDriverService() {
        MobileDriverService driver = switch (MOBILE_PLATFORM_NAME) {
            case ANDROID -> new AndroidDriverServiceImpl();
            case IOS -> new IosDriverServiceImpl();
            default -> throw new InvalidParameterException("Please use platform as '" + ANDROID + "' or '" + IOS + "'");
        };
        return driver;
    }
}

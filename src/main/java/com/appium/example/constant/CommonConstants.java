package com.appium.example.constant;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import static com.appium.example.constant.DriverConstants.ANDROID;

public class CommonConstants {
//    public final static Logger logger = LogManager.getLogger();

    private final static String DEV = "dev";
    public static final String MOBILE_PLATFORM_NAME = getPlatformName();
    public static final String EXECUTION_ENV_NAME = getEnvironmentName();


    //用于获取环境名称，在没有配置时使用默认值，并进行了相应的日志记录和处理。
    private static String getEnvironmentName() {
        String environmentNameFromPomXml = System.getProperty("environment");
        String envName;

        if (environmentNameFromPomXml != null)
            envName = environmentNameFromPomXml;
        else {
//            logger.warn("The Maven Profile is missing the environment configuration.");
//            logger.warn("The default environment '{}' will be enabled for this run.", DEV);
            envName = DEV;
        }

        return envName.toLowerCase();
    }

    //获取平台名称：
    private static String getPlatformName() {
        String platformNameFromPomXml = System.getProperty("platform");
        String platformName;

        if (platformNameFromPomXml != null)
            platformName = platformNameFromPomXml;
        else {
            platformName = ANDROID;
        }

        return platformName.toLowerCase();
    }
}

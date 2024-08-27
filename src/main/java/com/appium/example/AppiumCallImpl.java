package com.appium.example;

import com.appium.example.bean.AllTask;
import com.appium.example.bean.Step;
import com.appium.example.bean.Task;
import com.appium.example.constant.DriverConstants;
import com.appium.example.util.LogUtils;
import com.appium.example.util.driver.MobileDriverFactory;
import com.appium.example.util.driver.MobileDriverHolder;
import com.appium.example.util.driver.MobileDriverService;
import com.google.common.collect.ImmutableMap;
import com.main.interfaces.CallbackInfo;
import com.main.interfaces.ControlInfo;
import com.main.interfaces.IControlCallback;
import com.main.task.ICall;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebElement;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AppiumCallImpl implements ICall {

    private static final String TAG = "AppiumCallImpl";

    //定义为private static的类型可以直接通过类名来访问这个变量
    private static AllTask allTask;
    private static List<String> appList;
    private static boolean isExecuted = false; //通过标志位来使得once方法只执行一次

    public static void main(String[] args) throws MalformedURLException, URISyntaxException {
//        execCmdByAppium("am start", List.of(" -n com.test.tools/com.test.tools.MainActivity"));
        swipe();
    }

    private static void swipe() {
        AppiumDriver androidDriver = Main.getDriver();

//        boolean canScrollMore = (Boolean) ((JavascriptExecutor) androidDriver).executeScript("mobile: scrollGesture", ImmutableMap.of(
//                "left", 100, "top", 100, "width", 200, "height", 200,
//                "direction", "down",
//                "percent", 1.0
//        ));
//        WebElement element = androidDriver.findElement(By.xpath("//android.widget.ImageView[@resource-id=\"com.sup.android.superb:id/aue\"]"));
//        ((JavascriptExecutor) androidDriver).executeScript("mobile: dragGesture", ImmutableMap.of(
//                "elementId", ((RemoteWebElement) element).getId(),
//                "endX", 100,
//                "endY", 100
//        ));


        //
//        int startX = androidDriver.manage().window().getSize().getWidth() / 2;
//        int startY = androidDriver.manage().window().getSize().getHeight() / 2;
//        int endY = (int) (androidDriver.manage().window().getSize().getHeight() * 0.2);
////
//        PointerInput finger = new PointerInput(PointerInput.Kind.PEN, "finger");
//        Sequence scroll = new Sequence(finger, 0);
//
//        scroll.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
//        scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
//        scroll.addAction(finger.createPointerMove(Duration.ofMillis(6000), PointerInput.Origin.viewport(), startX, endY));
//        scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
//        androidDriver.perform(List.of(scroll));


        //滑动
        WebElement element = androidDriver.findElement(By.xpath("//android.widget.RelativeLayout[@resource-id=\"com.sup.android.superb:id/att\"]"));
        Point sourceLocation = element.getLocation();
        Dimension sourceSize = element.getSize();
        int centerX = sourceLocation.getX() + sourceSize.getWidth() / 2;
        int centerY = sourceLocation.getY() + sourceSize.getHeight() / 2;

        PointerInput finger = new PointerInput(PointerInput.Kind.PEN, "finger");
        Sequence dragNDrop = new Sequence(finger, 1);
        dragNDrop.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), centerX, centerY));
        dragNDrop.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        dragNDrop.addAction(finger.createPointerMove(Duration.ofMillis(700), PointerInput.Origin.viewport(), centerX, centerY - 5050));
        dragNDrop.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        androidDriver.perform(List.of(dragNDrop));

        //点击
//        PointerInput finger = new PointerInput(PointerInput.Kind.PEN, "finger");
//        Sequence tap = new Sequence(finger, 1);
//        tap.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), centerX, centerY));
//        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
//        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
//        androidDriver.perform(List.of(tap));

    }

    public static String execCmdByAppium(String cmd, List<String> args) throws URISyntaxException, MalformedURLException {
        MobileDriverService driverService = new MobileDriverFactory().getDriverService();
        AppiumDriverLocalService appiumService = driverService.startAppiumService();

//        UiAutomator2Options options = new UiAutomator2Options();
//        options.setCapability("appium:forceAppLaunch", true);
//        options.setCapability("automationName", "uiautomator2");
//        AndroidDriver androidDriver = new AndroidDriver(new URL("http://127.0.0.1:1234/"), options);

        AppiumDriver androidDriver = Main.getDriver();
        Map<String, Object> status = androidDriver.getStatus();
        LogUtils.debug(TAG, "appium status: " + status);

        String cmdRes = "";
        try {
            Map cmdMap = ImmutableMap.of("command", cmd, "args", args);
            cmdRes = (String) androidDriver.executeScript("mobile: shell", cmdMap);
        } catch (Exception e) {
            throw e;
        } finally {
//            androidDriver.quit();
        }
        return cmdRes;
    }

    private void once(ControlInfo controlInfo) {
        if (!isExecuted) {
            try {
                allTask = Main.generateTask(controlInfo.getTaskPath()); //
                appList = allTask.getAppList();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            isExecuted = true;
        }
    }

    @Override
    public void start(ControlInfo controlInfo, IControlCallback iControlCallback) {

        once(controlInfo);//用一个标志位 使其只执行一次 第一次!FALSE 执行一次后都是！Ture 所以不会在执行

        appList.forEach(app -> {
            if (app.equals(controlInfo.getAppName())) {
                List<Task> taskByApp = allTask.getTask(app);
                if (!taskByApp.isEmpty()) {
                    MobileDriverService mobileDriverService = null;
                    AndroidDriver androidDriver = null;
                    String packageName = "";
                    try {
                        Task task = taskByApp.get(0);  // 获取第一个任务
                        Step step = task.getSteps().get(0);

//                        UiAutomator2Options options = new UiAutomator2Options()
//                                .setUdid(controlInfo.getDeviceName())   //设置了设备的唯一标识符
//                                .setAppPackage(step.getAppName())  //应用的包名
//                                .setAppActivity(step.getActivityName())    //应用的活动
//                                .setNoReset(Boolean.parseBoolean(DriverConstants.ANDROID_NO_RESET))
//                                .setFullReset(Boolean.parseBoolean(DriverConstants.ANDROID_FULL_RESET))
//                                .autoGrantPermissions();    //  权限
//                        options.setCapability("appium:forceAppLaunch", true);
//                        options.setCapability("sessionOverride", true);
//
//                        MobileDriverService driverService = new MobileDriverFactory().getDriverService();
//                        AppiumDriverLocalService appiumService = driverService.startAppiumService();
//                        androidDriver = new AndroidDriver(appiumService.getUrl(), options);
//                        androidDriver.manage().timeouts().implicitlyWait(DriverConstants.APPIUM_DRIVER_TIMEOUT);
//                        packageName = step.getAppName();
//                        MobileDriverHolder.setDriver(androidDriver);

                        mobileDriverService = Main.beforeExec(step.getAppName(), step.getActivityName(), controlInfo.getDeviceName());
                        Main.execTask(task);
                        taskByApp.remove(task);  // 删除已执行的任务
                    } catch (IndexOutOfBoundsException e) {
                        //   throw new RuntimeException("获取任务列表中的第一个任务时出错，任务列表可能为空", e);
                        //logger.info("获取任务列表中的第一个任务时出错，任务列表可能为空");
                    } catch (NullPointerException e) {
                        //  throw new RuntimeException("获取任务步骤或执行相关操作时出现空指针异常", e);
                        //logger.info("获取任务步骤或执行相关操作时出现空指针异常");
                    } catch (Exception e) {
                        //  throw new RuntimeException("执行任务过程中出现未知异常", e);
                        //logger.info("执行任务过程中出现未知异常");
                    } finally {
//                        androidDriver.terminateApp(packageName);
//                        androidDriver.quit();

                        if (mobileDriverService != null) {
                            Main.afterExec(mobileDriverService);
                        }

                        CallbackInfo callbackInfo;
                        if (!taskByApp.isEmpty()) {
                            callbackInfo = new CallbackInfo.Builder().appName(controlInfo.getAppName()).nextApp(false).build();
                        } else {
                            //logger.info("{}任务执行完毕", controlInfo.getAppName());  //打印出这个任务至完毕
                            callbackInfo = new CallbackInfo.Builder().appName(controlInfo.getAppName()).nextApp(true).build();
                        }
                        iControlCallback.doCallback(callbackInfo);
                    }
                }
            }
        });
    }
}

package com.appium.example;

import com.appium.example.bean.AllTask;
import com.appium.example.bean.Step;
import com.appium.example.bean.Task;
import com.appium.example.constant.DriverConstants;
import com.appium.example.screen.BaseScreen;
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
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class AppiumCallImpl implements ICall {

    private static final String TAG = "AppiumCallImpl";

    //定义为private static的类型可以直接通过类名来访问这个变量
    private static AllTask allTask;
    private static List<String> appList;
    private static boolean isExecuted = false; //通过标志位来使得once方法只执行一次


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

        int index = 3;

        Path taskpath = Path.of("C:\\Users\\86158\\Desktop\\test\\测试.yaml"); //传入的需要被解析的yaml文件路径
        String yourpath = "C:\\Users\\86158\\Desktop\\"; //要保存截图的位置
        once(controlInfo);//用一个标志位 使其只执行一次 第一次!FALSE 执行一次后都是！Ture 所以不会在执行

        Task taskyaml = new Task();
        /////解析yaml文件中的内容
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new FileInputStream(taskpath.toFile());
            ArrayList<HashMap<String, String>> arrayList = yaml.loadAs(inputStream, ArrayList.class);
            arrayList.forEach(ele -> {
                Step steplist = new Step();
                if (ele.get("app_name") != null) {
                    steplist.setAppName(ele.get("app_name"));
                }

                //解析yaml文件并保存为对应的字符串数组
                if (ele.get("taskslist") != null) {
                    steplist.setTaskslist(ele.get("taskslist"));
                }
                taskyaml.setSteps(steplist);
            });
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Step temp =  taskyaml.getSteps().get(index);

        String appstr = temp.getAppName();
        String taskstr = temp.getTaskslist();
        String[] parts = taskstr.split(","); //将不同的任务队列通过,分隔开

        appList.forEach(app -> {
            if (app.equals(appstr)) {

                //不同的任务队列tasklist进行for循环
                for (int i = 0; i < parts.length; i++) {
                    List<Task> taskByApp = allTask.getTask(app);
                    MobileDriverService mobileDriverService = Main.beforeExec(taskByApp.get(0).getSteps().get(0).getAppName(), taskByApp.get(0).getSteps().get(0).getActivityName(), controlInfo.getDeviceName());

                    String part = parts[i];
                    String[] partlist = part.split("\\+");
                    //把他变成一个链表  .map操作后的 Stream（其中的字符串都加上了.yaml后缀）收集到一个新的List<String>中。
                    List<String> newPartListAsList = Arrays.stream(partlist)
                            .map(str -> str + ".yaml")
                            .collect(Collectors.toList());

                    //这个 for 循环是轮询执行解析出来的listtask中的task
                    for (int j = 0; j < newPartListAsList.size(); j++) {
                        //不同的去和tasks比
                        for (int k = 0; k < taskByApp.size(); k++) {
                            Task task = taskByApp.get(k);

                            //将在tasklist中取得的task与解析的进行比较
                            if (task.getTaskName().equals(newPartListAsList.get(j))) {
                                try {
                                    Main.execTask(task); //执行任务不管如何都会截图
                                } finally {
                                    BaseScreen baseScreen = new BaseScreen(MobileDriverHolder.getDriver());
                                    try {

                                        baseScreen.screenshot(yourpath, task.getAppName(), task.getTaskName());

                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }//if的

                        } // 内嵌比较的for
                    }   //for的

                    try {
                        Main.afterExec(mobileDriverService);
                    } finally {
                        CallbackInfo callbackInfo;
                        if (i != parts.length - 1) {
                            callbackInfo = new CallbackInfo.Builder().appName(controlInfo.getAppName()).nextApp(false).build();
                        } else {
                            //logger.info("{}任务执行完毕", controlInfo.getAppName());  //打印出这个任务至完毕
                            callbackInfo = new CallbackInfo.Builder().appName(controlInfo.getAppName()).nextApp(true).build();
                        }
                        iControlCallback.doCallback(callbackInfo);
                    }

                }  //第一个for

            }//if (app.equals(appstr)){
        });

    }


}



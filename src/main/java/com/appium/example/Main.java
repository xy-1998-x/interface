/**
 * @authors zhuyin
 */

package com.appium.example;

import com.appium.example.bean.AllTask;
import com.appium.example.bean.AppInfo;
import com.appium.example.bean.Step;
import com.appium.example.bean.Task;

import com.appium.example.constant.DriverConstants;
import com.appium.example.constant.FindType;
import com.appium.example.constant.OperateType;
import com.appium.example.screen.BaseScreen;
import com.appium.example.util.driver.MobileDriverFactory;
import com.appium.example.util.driver.MobileDriverHolder;
import com.appium.example.util.driver.MobileDriverService;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.stream.Stream;

import static com.appium.example.util.driver.MobileDriverHolder.setDriver;

public class Main {
//    private static final Logger logger = LogManager.getLogger();
    // 这行代码用于从日志管理器（LogManager）中获取一个日志记录器（Logger）对象。


    //将appname传入进来转化为appname
    private static void generateTask(Path appNamePath, AllTask allTask) {
        //path类型表示为系统中的路劲
        //输入的文件系统路径path类型 然后getFileName 用于获取 Path 对象所表示的路径中的文件名部分。
        // 这段代码debug出了appname：“pipixia”

        //对Path类型的 appName执行方法PathgetFileName()将此路径表示的文件或目录的名称作为path对象返回 toString()方法将其转化为字符串 赋值给appnname
        String appName = appNamePath.getFileName().toString();

        // 将 appName 添加到applist
        allTask.setAppList(appName);

        //appname该流包含了指定路径 appNamePath 下的所有文件和子目录的路径。
        try (Stream<Path> tasksPath = Files.list(appNamePath))  //appNamePath是皮皮虾等appname的路径 taskPath是yaml文件
        {

            //forEach方法会依次对 tasksPath 流中的每个 taskPath 元素执行后面指定的操作
            //这段debug出了taskPath:\tasks\pipixia\关注.yaml

/////////////////这段代码是轮询app目录下的所有yaml文件并生成对应的task
            tasksPath.forEach(taskPath -> {
                //taskName = "关注.yaml"之类
                String taskName = taskPath.getFileName().toString();    //将taskpath变为string赋值给taskname
                System.out.println("文件: " + taskName);

                //创建了一个名为 task 的 Task 类的对象，并为其设置了两个属性：appName 和 taskName 。
                //通过 task.setAppName(appName) 和 task.setTaskName(taskName) 这两行代码，将传递进来的
                Task task = new Task();//new为该对象分配内存空间  新的 Task 对象分配了空间
                task.setAppName(appName);   //传递进来的appname值赋给task对象的appname成员变量
                task.setTaskName(taskName); //同上

                //allTask 对象中设置与特定的应用名称 appName 相关联的任务 task
                allTask.setTask(appName, task);

                /////////////对yaml进行操作 读取yaml文件中的内容
                try {
                    Yaml yaml = new Yaml();

                    //taskPath 是一个 Path 对象，通过 toFile() 方法将其转换为 File 对象，然后以此创建 FileInputStream 来读取文件的内容。
                    //FileInputStream 是用于从文件中读取字节数据的输入流 通过 toFile() 方法可以将其转换为 File 对象，从而满足 FileInputStream 构造函数的参数要求。
                    //FileInputStream以字节形式读取文件里的内容
                    InputStream inputStream = new FileInputStream(taskPath.toFile());

                    //从输入流 inputStream 中加载数据，并将其解析为一个 ArrayList<HashMap<String, String>> 类型的对象，然后将结果存储在 arrayList 变量中
                    //用于从 YAML 格式的输入中加载数据
                    ArrayList<HashMap<String, String>> arrayList = yaml.loadAs(inputStream, ArrayList.class);

                    //这一段的执行操作是将执行yaml文件的元素内容
                    arrayList.forEach(ele -> {
                        //在step这个类中定义了一系列的步骤
                        Step step = new Step();//step执行
                        //在inputStream输入流yaml中读取到的app_name非空
                        if (ele.get("app_name") != null) {
                            //则将其传入到step中
                            step.setAppName(ele.get("app_name"));
                        }

                        if (ele.get("activity_name") != null) {
                            step.setActivityName(ele.get("activity_name"));
                        }

                        step.setElementInfo(ele.get("element_info"));

                        step.setFindType(FindType.valueOf(ele.get("find_type")));
                        //在step对象调用setFindType方法

                        //过 ele.get("operate_type") 获取一个值，然后尝试将这个值转换为 OperateType 枚举类型的实例，并将其赋值给变量 operateType
                        // 最后，再将 operateType 的值设置给 step 对象的 operateType 属性。
                        OperateType operateType = OperateType.valueOf(ele.get("operate_type"));
                        step.setOperateType(operateType);

                        if (operateType.equals(OperateType.INPUT)) {
                            step.setInputText(ele.get("key"));
                        }
                        //将 step 对象设置到 task 对象的 steps 属性中。
                        task.setSteps(step);
                    });

                }
                ///////////////
                catch (FileNotFoundException e) {
                    // 将文件未找到异常转换为运行时异常抛出
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            // 将输入输出异常转换为运行时异常抛出
            throw new RuntimeException(e);
        }
    }

    //用于生成与项目中 tasks 文件夹相关的 AllTask 对象。
    public static AllTask generateTask(String taskpath) throws Exception {
        AllTask allTask = new AllTask();

        Path path = Paths.get(taskpath);
        // 使用 try-with-resources 语句获取任务目录下的子路径流 这里获取的是tasks下的所有子路径流
        try (Stream<Path> paths = Files.list(path)) {
            //路径流 paths 进行过滤，只保留其中的子目录路径
            //如果筛选出的目录路径分别是 D:\tasks\app1 、 D:\tasks\app2 ，那么会为这两个目录分别执行 generateTask 方法，
            paths.filter(Files::isDirectory).forEach(appNamePath ->
            {

                // 根据对应的appname生成task
                generateTask(appNamePath, allTask);

                // 获取当前目录路径的文件名，并将其存储在 appName 变量中。
                String appName = appNamePath.getFileName().toString();
                System.out.println("目录: " + appName);

            });
        }
        return allTask;
    }

    public static void afterExec(MobileDriverService driverService) {
        //这通常意味着在执行完某些操作之后，需要通过调用这个方法来关闭与移动驱动相关的服务或资源，以释放相关的系统资源或完成必要的清理工作
        driverService.closeDriver();
    }

    private static final AndroidDriver execCmdAndroidDriver;

    static {
        MobileDriverService driverService = new MobileDriverFactory().getDriverService();
        AppiumDriverLocalService appiumService = driverService.startAppiumService();

        UiAutomator2Options options = new UiAutomator2Options()
                .setNoReset(Boolean.parseBoolean(DriverConstants.ANDROID_NO_RESET))
                .setFullReset(Boolean.parseBoolean(DriverConstants.ANDROID_FULL_RESET))
//                .setAppPackage("com.sup.android.superb")
//                .setAppActivity("com.sup.android.base.MainActivity")
                .autoGrantPermissions();    //  权限
        options.setCapability("sessionOverride", true);
//        options.setCapability("appWaitActivity", "com.sup.android.base.MainActivity");
        options.setCapability("appium:forceAppLaunch", true);
        options.setCapability("adbExecTimeout", 50000);

        execCmdAndroidDriver = new AndroidDriver(appiumService.getUrl(), options);
        execCmdAndroidDriver.manage().timeouts().implicitlyWait(DriverConstants.APPIUM_DRIVER_TIMEOUT);
    }

    public static AppiumDriver getDriver() {
        return execCmdAndroidDriver;
    }

    //插入设备名 String devicename
    public static MobileDriverService beforeExec(String appName, String activityName, String deviceName) {
        // //根据不同的平台获取相应的驱动服务
        MobileDriverService driverService = new MobileDriverFactory().getDriverService();

        // 启动 Appium 服务，获取ip 监听端口
        AppiumDriverLocalService appiumService = driverService.startAppiumService();

        // 构建一个 AppInfo 对象，并设置相关属性
        AppInfo appInfo = AppInfo.builder()
                .androidAppActivity(activityName)
                .androidAppPackage(appName)
                .build();

        driverService.initAppInfo(appInfo);  // 初始化 driverService 的 AppInfo

        driverService.spinUpDriver(appiumService, deviceName);// 照这些配置包括设备名 应用名 行为名 来启动 Android 驱动 启动驱动  s

        setDriver(driverService.getDriver());  // 设置驱动

        return driverService;
    }


    //执行task
    public static void execTask(Task task) {
        //BaseScreen是初始化 driver 并创建一个等待时间为 30 秒的 appiumdriver对象。
        BaseScreen baseScreen = new BaseScreen(MobileDriverHolder.getDriver());
        // appium定位元素的几种方式：https://blog.csdn.net/lovedingd/article/details/111058898
        // https://cloud.tencent.com/developer/article/1816977

        // 获取任务中的步骤列表 yaml文件中列首选项
        List<Step> steps = task.getSteps();

        // 遍历 1.获取特定元素 2.定位元素 3.执行操作
        steps.forEach(step -> {
            By elementInfo = null;
            String info = step.getElementInfo(); //取特定元素的信息

            //// 根据步骤的查找类型设置元素定位方式 (xpath)
            switch (step.getFindType()) {
                case ID -> elementInfo = AppiumBy.id(info);
                case CLASSNAME -> elementInfo = AppiumBy.className(info);
                case DESC -> elementInfo = AppiumBy.accessibilityId(info);
                case XPATH -> {
                    elementInfo = AppiumBy.xpath(info);
                    if (elementInfo != null) {  // 判断是否成功获取到元素
                        //logger.info("已通过xpath找到‘{}’", info);
                    } else {
                        //logger.info("未通过xpath找到‘{}’", info);
                    }
                }

                default -> {
                    // System.out.println("请检查元素查找类型，当前仅支持 'id/classname/content-desc/xpath'");
                    //logger.error("请检查元素查找类型，当前仅支持 'id/classname/content-desc/xpath.");
                    return;
                }
            }

            // 根据步骤的操作类型执行相应操作
            switch (step.getOperateType())//step对象的行为getOperateType()会返回OperateType这个自定义的枚举
            {
                case SCROLLTOTEXT -> baseScreen.scrollToElement("");
                case DOUBLECLICK -> baseScreen.doubleclick(elementInfo);
                case CLICK -> {
                    boolean success = baseScreen.tap(elementInfo);
                    if (success) {
                        //logger.info("执行点击操作成功");
                    } else {
                        //logger.info("执行点击操作失败");
                    }
                }

                case INPUT -> {
                    boolean success = baseScreen.inputText(elementInfo, step.getInputText());
                    if (success) {
                        //logger.info("执行输入操作成功");
                    }
                    //logger.info("执行输入操作失败");
                }
                case SCROLLDOWN -> {
                    try {
                        baseScreen.scrolldown(step.getNumsinfo());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                case SCROLLRIGHT -> {
                    try {
                        baseScreen.scrollright(step.getNumsinfo());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                default -> {
                    //logger.error("请检查操作类型，当前操作类型包括 '点击/文本输入.");
                }
            }
        });
    }

    public void main(String[] args) throws Exception {
        AllTask allTask = Main.generateTask(args[2]);  ////找到项目中的tasks文件夹路径 获取任务目录下的子路径流 这里获取的是tasks下的子路径流
        // 返回一个包含字符串的列表 appList
        List<String> appList = allTask.getAppList();//如果 tasks 文件夹下有子路径 app1 、 app2 ，那么 appList 可能就包含 "app1" 、 "app2"

        appList.forEach(app -> {
            if (app.equals("ximalaya")) { // 假设只对 "App1" 和 "App2" 执行操作
                List<Task> taskByApp = allTask.getTask(app);
                //logger.info("当前任务还有{}", taskByApp);

                if (!taskByApp.isEmpty()) {
                    Task task = taskByApp.get(0);  // 获取第一个任务
                    Step step = task.getSteps().get(0);

                    //打开app
                    MobileDriverService mobileDriverService = Main.beforeExec(step.getAppName(), step.getActivityName(), args[1]);
                    Main.execTask(task);
                    //  taskByApp.remove(0);  // 删除已执行的任务
                    taskByApp.remove(task);
                    Main.afterExec(mobileDriverService);
                }
            }
        });
    }
}



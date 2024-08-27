package com.appium.example.screen;

import com.google.common.collect.ImmutableMap;
import io.appium.java_client.AppiumBy;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static com.appium.example.constant.CommonConstants.MOBILE_PLATFORM_NAME;
import static com.appium.example.constant.DriverConstants.ANDROID;

public class BaseScreen
{
    public final WebDriver driver;
    public final WebDriverWait wait;


    //初始化 driver 并创建一个等待时间为 30 秒的 WebDriverWait 对象。
    public BaseScreen(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }


    //等待by元素可见
    //WebDriverWait 机制来等待指定定位方式的元素变得可见。这在自动化测试中很常见，用于确保在对元素进行操作之前，它已经完全加载并显示在页面上，以提高操作的稳定性和准确性。
    public void waitUntilElementVisible(By by) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    //根据不同的移动平台（Android 或 iOS），通过特定的定位方式滚动到指定文本的元素，并返回该元素。
    public WebElement scrollToElement(String elementText)
    {
        WebElement element;
        if (MOBILE_PLATFORM_NAME.equalsIgnoreCase(ANDROID)) {
            element = driver.findElement(AppiumBy.androidUIAutomator(
                                    "new UiScrollable(new UiSelector().scrollable(true))"
                                            + ".scrollIntoView(new UiSelector().text(\"" + elementText + "\"))"
                    //scrollIntoView 方法的意图是将匹配特定条件的元素滚动到视图中 UiScrollable 类通常与垂直滚动的操作相关联。它的目的是处理可滚动的视图或区域
                    //安卓的实现方式是：首先new一个可滚动的对象 然后滚动到文本为 elementText 的元素位置，使其可见。
                            )
                    );
        } else {
            element = driver.findElement(AppiumBy.iOSNsPredicateString("label == '" + elementText + "'"));
            //IOS的实现方式为：直接找到标签为elementText的元素
        }

        return element;
    }


    public boolean tap(By by) {
        waitUntilElementVisible(by);
        try {
            driver.findElement(by).click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public void scrollAndTap(String elementText)
    {
        scrollToElement(elementText).click();  //首先是调用不同的平台 使其元素可见 然后点击
            }


    public boolean inputText(By by, String text)
    {
        waitUntilElementVisible(by);    //首先是调用不同的平台 使其元素可见
        try {
            driver.findElement(by).sendKeys(text);  //找到元素 发送文本
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void scrollAndInputText(String elementText, String text)
    {
        scrollToElement(elementText).sendKeys(text);//这个函数直接在使其可见的基础上 发送文本
    }


    public void doubleclick(By by){
        waitUntilElementVisible(by);
        RemoteWebElement element = (RemoteWebElement) driver.findElement(by); //通过这个定位到的元素来get他的id
//        RemoteWebElement element = (RemoteWebElement) driver.findElement(By.xpath("//android.widget.FrameLayout[@resource-id=\"com.sup.android.superb:id/action_bar_root\"]/android.widget.FrameLayout"));
        ((JavascriptExecutor) driver).executeScript("mobile: doubleClickGesture", ImmutableMap.of(
                "elementId", element.getId()
        ));
    }

    //上滑
    public void scrolldown(String n) throws InterruptedException {
//        waitUntilElementVisible(by);
        long startTime = System.currentTimeMillis();

        for(int i = 0; i <= Integer.parseInt(n); i++)
//        while ((System.currentTimeMillis() - startTime) < 10000)
        {
            Dimension size = driver.manage().window().getSize();
            int X = size.width / 2;
            int Y = (int) (size.height * 0.2);
            ((JavascriptExecutor) driver).executeScript("mobile: swipeGesture", ImmutableMap.of(
                    "left", 10, "top", 10, "width", X, "height", Y,
                    "direction", "up",
                    "percent", 0.75
            ));

            Thread.sleep(2000);
        }
    }

    //右滑
    public void scrollright(String n) throws InterruptedException {
//        waitUntilElementVisible(by);
        long startTime = System.currentTimeMillis();

        for(int i = 0; i <= Integer.parseInt(n); i++)
//        while ((System.currentTimeMillis() - startTime) < 10000)
        {
            Dimension size = driver.manage().window().getSize();
            int X = size.width/2 ;
            int Y = (int) (size.height * 0.2);
            ((JavascriptExecutor) driver).executeScript("mobile: swipeGesture", ImmutableMap.of(
                    "left", 10, "top", 10, "width", X, "height", Y,
                    "direction", "left",
                    "percent", 0.75
            ));

            Thread.sleep(2000);
        }
    }

    //找元素/////////////
    public void findtext(String targetText) throws InterruptedException {

        if(  driver.findElement(By.xpath("//*[@text='"+targetText+"']") )== null)
        {
            Dimension size = driver.manage().window().getSize();
            int X = size.width/2 ;
            int Y = (int) (size.height * 0.2);
            ((JavascriptExecutor) driver).executeScript("mobile: swipeGesture", ImmutableMap.of(
                    "left", 10, "top", 10, "width", X, "height", Y,
                    "direction", "left",
                    "percent", 0.75
            ));

            Thread.sleep(2000);
        }
        else{
            Thread.sleep(5000);
        }

    }

    public void screenshot(String yourpath,String appname,String taskname) throws IOException {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        String path = yourpath;
        String folderName = appname;
//        String path = "C:\\Users\\86158\\Desktop\\";
//        String folderName = "pipixia";

        File folder = new File(path + folderName);
        try {
            if (!folder.exists()) {
                boolean created = folder.mkdirs();  //创建以该路劲名命名的目录 文件夹
                if (created) {
                    System.out.println("文件夹创建成功：" + folder.getAbsolutePath());
                } else {
                    System.out.println("文件夹创建失败。");
                }
            } else {
                System.out.println("文件夹已存在：" + folder.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        File destinationFile = new File(folder, taskname+".png");
        FileUtils.copyFile(screenshot, destinationFile);

    }


}

package com.appium.example.screen;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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


    //先等待元素可见，然后点击指定的元素
    //这个方法提供了一种统一的方式来执行元素的点击操作，并确保在点击之前元素是可见的，增加了操作的可靠性和稳定性。
/*    public void tap(By by)
    {
        waitUntilElementVisible(by);//调用函数等待可见 直接就是等待显示的函数方法
        driver.findElement(by).click();//先找到元素 再点击
        //driver是WebDriver类型 而WebDriver是一个远程控制接口，允许自省和控制用户代理
        //WebDriver有findElement(by)这个方法  而findElement(by)的返回值WebElement通过click这个方法操作
    } */

    public boolean tap(By by) {
        waitUntilElementVisible(by);
        try {
            driver.findElement(by).click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    //滚动到指定文本的元素并点击。
    //其功能是先滚动到指定文本的元素位置，然后对该元素执行点击操作。 传入的参数elementText
    public void scrollAndTap(String elementText)
    {
        scrollToElement(elementText).click();  //首先是调用不同的平台 使其元素可见 然后点击
        //中间的.就是对 scrollToElement(elementText)返回对象的click方法进行调用
        //返回的是WebElement element; WebElement类中的方法就包括click()；
            }


    //传入的参数为by 对象来定位页面中的特定元素。By 通常是一种用于描述元素定位方式的对象，比如通过 ID、类名、标签名等属性来找到元素
    //等待元素可见，然后向指定元素输入文本。
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

    //导入执行函数
    //滚动到指定文本的元素并输入文本。
    public void scrollAndInputText(String elementText, String text)
    {
        scrollToElement(elementText).sendKeys(text);//这个函数直接在使其可见的基础上 发送文本
    }


}

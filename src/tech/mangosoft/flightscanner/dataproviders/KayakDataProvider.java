package tech.mangosoft.flightscanner.dataproviders;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import tech.mangosoft.flightscanner.Application;
import tech.mangosoft.flightscanner.SeleniumUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

@Component
@Scope("prototype")
@Lazy(value = true)
public class KayakDataProvider {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    private WebDriver driver;

    @Autowired
    private SeleniumUtils utils;

    @Value("${tech.mangosoft.flightscanner.injected_javascript}")
    private String injectedJs;

    private String from, to, month;
    private int day;

    private int uiType = UI_TYPE_DEFAULT;
    public static int UI_TYPE_DEFAULT = 0;
    public static int UI_TYPE_ORANGE = 1;

    //0 == default, 1 == orange

    public KayakDataProvider() {

    }

    public List processFlight(String from, String to, String month, int day) throws IOException, InterruptedException {

        this.from = from;
        this.to = to;
        this.month = month;
        this.day = day;

        //going google
        log.info("Processing flight: " + from + " -> " + to);
        WebElement element = utils.searchGoogle("kayak");

        fillSearchForm(element);

        return new LinkedList();
    }




    private WebElement fillSearchForm(WebElement googleFoundLink) throws InterruptedException {

        log.info("Fill Search form: " );

        //setting origin
        List<WebElement> originPlaceholders = utils.fluentWait(By.xpath("//div[contains(@id, '-origin-airport-display-inner')]"));
        WebElement originPlaceholder = null;

        if (originPlaceholders.size() > 0 ) {
            originPlaceholder = originPlaceholders.get(0);
            uiType = UI_TYPE_ORANGE;
        } else {
            //let's process original UI
            originPlaceholder = utils.fluentWait(By.name("origin")).get(0);
            uiType = UI_TYPE_DEFAULT;
        }



        new Actions(driver).moveToElement(originPlaceholder).click().perform();

        WebElement origin = utils.fluentWait(By.name("origin")).get(0);
        utils.ensureTextIsTyped(origin, this.from);
        utils.randomSleep(3);

        clickOnFirstElementInDropdown("origin");

        utils.randomSleep(3);
        WebElement destination = utils.fluentWait(By.name("destination")).get(0);
        new Actions(driver).moveToElement(destination).click().perform();
        utils.ensureTextIsTyped(destination, this.to);
        utils.randomSleep(3);

        clickOnFirstElementInDropdown("destination");

        clickOnDepartureDates(this.month, this.day);

        //submit form
        By by = By.xpath("//div[contains(@id, \"flights\")]//form[contains(@name, \"searchform\") and contains(@aria-hidden,\"false\")]//*[contains(@id, \"-submit\")]");

        if (uiType == UI_TYPE_ORANGE) {
            by = By.xpath("//form[contains(@name, \"searchform\") and contains(@aria-hidden,\"false\")]//*[contains(@id, \"-submit\")]");
        }

        WebElement btnSubmit = driver.findElement(by);
        btnSubmit.click();

        return btnSubmit;
    }

    private void clickOnFirstElementInDropdown(String name) throws InterruptedException {
        //selecting first item in the dropdown list
        List<WebElement> originElements = utils.fluentWait(By.xpath("//div[contains(@id, '"+name +"-airport-smartbox-dropdown')]/ul/li[contains(@class,'ap')]"));

        //List<WebElement> originElements =  driver.findElements(By.xpath("//div[contains(@id, "+name +"-airport-smartbox-dropdown')]/ul/li[contains(@class,'ap')]"));

        WebElement originListFirstElement = originElements.get(0);
        // Iterate in reverse.
        ListIterator<WebElement> li = originElements.listIterator(originElements.size());
        while(li.hasPrevious()) {
            originListFirstElement = li.previous();
            utils.mouseMoveToElement(originListFirstElement);
        }

        originListFirstElement.click();
        utils.randomSleep(3);
    }


    private void clickOnDepartureDates(String month, int day) throws InterruptedException {

        //default
        By by = By.xpath("//div[contains(@id, 'depart-input')]");
        if (uiType == UI_TYPE_ORANGE) {
            by = By.xpath("//div[contains(@id, 'dateRangeInput-display-start-inner')]");
        }
        driver.findElement(by).click();

        utils.randomSleep(4);

        //display-start-inner
        //nxiD-months
        //class keel-grid monthsGrid
        //nxiD-201804-6
        //$x('//div[@aria-describedby = \'nxiD-201804-15\']/div')

        //start date
        WebElement dayStart = driver.findElement(By.xpath("//div[contains(@aria-describedby, '"+ month +"') and contains(@aria-label,'" + String.valueOf(day) + "')]/div"));
        utils.mouseMoveToElement(dayStart);
        utils.randomSleep(3);
        new Actions(driver).moveToElement(dayStart, 5, 5).click().build().perform();
        //dayStart.click();
        //( (JavascriptExecutor)driver).executeScript("arguments[0].click()", dayStart);
        utils.randomSleep(2);

        //start date
        WebElement dayEnd = driver.findElement(By.xpath("//div[contains(@aria-describedby, '"+ month +"') and contains(@aria-label,'" + String.valueOf(day+1) + "')]/div"));
        utils.mouseMoveToElement(dayEnd
        );
        utils.randomSleep(3);
        new Actions(driver).moveToElement(dayEnd, 5, 5).click().build().perform();
//        dayEnd.click();
        //( (JavascriptExecutor)driver).executeScript("arguments[0].click()", dayEnd);
        utils.randomSleep(2);

    }


}

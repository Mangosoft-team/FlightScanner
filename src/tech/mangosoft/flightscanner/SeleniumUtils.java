package tech.mangosoft.flightscanner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Scope("singleton")
public class SeleniumUtils  {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    private WebDriver driver;

    public SeleniumUtils() {
    }

    public void randomSleep(int sleep) throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextLong(500L*sleep, 1000L*sleep));
    }

    public List<WebElement> fluentWait(final By locator){

        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(10, TimeUnit.SECONDS)
                .pollingEvery(3, TimeUnit.SECONDS)
                .ignoring(org.openqa.selenium.NoSuchElementException.class);

        List<WebElement> foo = wait.until(
                new Function<WebDriver, List<WebElement>>() {
                    public List<WebElement> apply(WebDriver driver) {
                        return driver.findElements(locator);
                    }
                }
        );
        return  foo;
    }




    public void mouseMoveToElement(WebElement element){
        Actions builder = new Actions(driver);
        builder.moveToElement(element).build().perform();
    }


    public WebElement ensureTextIsTyped(WebElement element, String text) throws InterruptedException {
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<text.length(); i++){
            sb.append(text.charAt(i));
            while (! element.getAttribute("value").equals(sb.toString())){
                element.clear();
                element.sendKeys(sb.toString() );
                randomSleep(1);
            }
        }
        return  element;
    }


    public WebElement searchGoogle(String searchString) throws InterruptedException {

        driver.get("http://www.google.com");
        WebElement element = driver.findElement(By.name("q"));
        element.sendKeys(searchString + "\n"); // send also a "\n"
        //element.submit();

        randomSleep(3);

        // wait until the google page shows the result
        WebElement dynamicElement = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("resultStats")));

        List<WebElement> findElements = driver.findElements(By.xpath("//*[@id='rso']//h3/a"));

        log.debug("Searching " + searchString + ". Results: " + findElements.stream().map((x)->{return x.getAttribute("href");})
                .collect(Collectors.joining(", ")));

        // this are all the links you like to visit
        for (WebElement webElement : findElements)
        {
            String href = webElement.getAttribute("href");

            if (href.indexOf("www." + searchString) >-1) {
                //click
                log.info("Found corect link at google: " + href + ". Clicking..." );
                webElement.click();
                randomSleep(5);
                return webElement;
            }
        }
        log.error("Can't find searched website " + searchString);
        throw new RuntimeException("Can't find searched website " + searchString);
    }

}

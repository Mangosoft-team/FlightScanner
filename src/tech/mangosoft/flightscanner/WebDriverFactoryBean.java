package tech.mangosoft.flightscanner;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;

import java.net.URL;
import java.util.Arrays;

public class WebDriverFactoryBean implements FactoryBean<WebDriver> {
    @Value("${webdriver.gecko.driver}")
    private String driverExecutables;

    @Override
    public WebDriver getObject() throws Exception {
   /*
        //firefox
        System.setProperty("webdriver.gecko.driver", driverExecutables);
        MutableCapabilities options = DesiredCapabilities.chrome();
        options.setCapability("browser.link.open_newwindow.restriction", 1);
        options.setCapability("browser.privatebrowsing.autostart", true);
        options.setCapability(ChromeOptions.CAPABILITY, Arrays.asList("--incognito"));
        //options.addPreference("selenium.server.url", "")
*/

        //chrome
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--profile-directory=Default");
        options.addArguments("--incognito");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-plugins-discovery");
        options.addArguments("--disable-infobars");

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);

        WebDriver driver = new RemoteWebDriver(new URL("http://localhost:9515"), options);


        /*//PhantomJS
        DesiredCapabilities caps = DesiredCapabilities.phantomjs();
        caps.setCapability(
                PhantomJSDriverService.PHANTOMJS_CLI_ARGS,
                options);
        WebDriver driver = new RemoteWebDriver(new URL("http://localhost:1234"), caps);
        */

        driver.manage().deleteAllCookies();
        return driver;
    }

    @Override
    public Class<?> getObjectType() {
        return WebDriver.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}


package tech.mangosoft.flightscanner;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import tech.mangosoft.flightscanner.dataproviders.KayakDataProvider;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@SpringBootApplication
@ImportResource("classpath:META-INF/spring/app-context-xml.xml")
@PropertySource("classpath:application.properties")
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    private WebDriver driver;

    @Value("${tech.mangosoft.flightscanner.flightsresource}")
    private String flightResource;

    private Queue<String> flights;

    @Value("${tech.mangosoft.flightscanner.dataprovider.from}")
    private String from;

    @Value("${tech.mangosoft.flightscanner.dataprovider.to}")
    private String to;

    @Value("${tech.mangosoft.flightscanner.dataprovider.month}")
    private String month;

    @Value("${tech.mangosoft.flightscanner.dataprovider.day}")
    private String day;

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }


    public void run(String... args) throws Exception {

    KayakDataProvider kayak = context.getBean(KayakDataProvider.class);
    kayak.processFlight(this.from, this.to, this.month, Integer.parseInt(this.day));

    }


}
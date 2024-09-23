package com.testinium;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
* The type Hooks.
* This class sets driver, capabilities and some of options
*/
public class BaseTest {

    protected static WebDriver driver;
    //dsfsad

    protected static Actions actions;
    protected Logger logger = LoggerFactory.getLogger(getClass());
    DesiredCapabilities capabilities;
    ChromeOptions chromeOptions;
    FirefoxOptions firefoxOptions;

    String browserName = "chrome";
    String selectPlatform = "mac";

    private static final String DEFAULT_DIRECTORY_PATH = "elementValues";
    ConcurrentMap<String, Object> elementMapList = new ConcurrentHashMap<>();

    /**
    * @return the web driver
    */
    public static WebDriver getWebDriver() {
        return driver;
    }

    /**
    * Before test
    * This method Checks testinium key and initialize the webdriver correctly from web_driver package
    */
    @Before
    public void beforeTest() {
        Properties properties = System.getProperties();
            logger.info("************************************  BeforeScenario  ************************************");
            try {
                if (StringUtils.isEmpty(System.getenv("key"))&&StringUtils.isEmpty(properties.getProperty("key"))) {
                    logger.info("Local cihazda " + selectPlatform + " ortamında " + browserName + " browserında test ayağa kalkacak");
                    if ("win".equalsIgnoreCase(selectPlatform)) {
                        if ("chrome".equalsIgnoreCase(browserName)) {
                            driver = new ChromeDriver(chromeOptions());
                            driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
                        } else if ("firefox".equalsIgnoreCase(browserName)) {
                            driver = new FirefoxDriver(firefoxOptions());
                            driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
                        }
                    } else if ("mac".equalsIgnoreCase(selectPlatform)) {
                        if ("chrome".equalsIgnoreCase(browserName)) {
                            driver = new ChromeDriver(chromeOptions());
                            driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
                        } else if ("firefox".equalsIgnoreCase(browserName)) {
                            driver = new FirefoxDriver(firefoxOptions());
                            driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
                        }
                        actions = new Actions(driver);
                    }

                } else {
                    logger.info("************************************   Testiniumda test ayağa kalkacak   ************************************");
                    ChromeOptions options = new ChromeOptions();
                    capabilities = DesiredCapabilities.chrome();
                    options.setExperimentalOption("w3c", false);
                    options.addArguments("disable-translate");
                    options.addArguments("--disable-notifications");
                    options.addArguments("--start-fullscreen");
                    Map<String, Object> prefs = new HashMap<>();
                    options.setExperimentalOption("prefs", prefs);
                    capabilities.setCapability(ChromeOptions.CAPABILITY, options);
                    capabilities.setCapability("key", System.getenv("key"));
                    capabilities.setCapability("key", properties.getProperty("key"));
                    browserName = System.getenv("browser");
                    //driver = new RemoteWebDriver(new URL("https://hubclouddev.testinium.com/wd/hub"), capabilities);
                    driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);
                    //driver = new RemoteWebDriver(new URL("http://host.docker.internal:4444/wd/hub"), capabilities);

                    actions = new Actions(driver);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

    public ChromeOptions chromeOptions() {
        chromeOptions = new ChromeOptions();
        capabilities = DesiredCapabilities.chrome();
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        chromeOptions.setExperimentalOption("prefs", prefs);
        chromeOptions.addArguments("--kiosk");
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--start-fullscreen");
        System.setProperty("webdriver.chrome.driver", "web_driver/chromedriver");
        chromeOptions.merge(capabilities);
        return chromeOptions;
    }

    /**
     * Set Firefox options
     *
     * @return the firefox options
     */
    public FirefoxOptions firefoxOptions() {
        firefoxOptions = new FirefoxOptions();
        capabilities = DesiredCapabilities.firefox();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        firefoxOptions.addArguments("--kiosk");
        firefoxOptions.addArguments("--disable-notifications");
        firefoxOptions.addArguments("--start-fullscreen");
        FirefoxProfile profile = new FirefoxProfile();
        capabilities.setCapability(FirefoxDriver.PROFILE, profile);
        capabilities.setCapability("marionette", true);
        firefoxOptions.merge(capabilities);
        System.setProperty("webdriver.gecko.driver", "web_driver/geckodriver");
        return firefoxOptions;
    }

    /**
    * After test.
    * Quit driver
    */
    @After
    public void afterTest() {
        driver.quit();
    }
}


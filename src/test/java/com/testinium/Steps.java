package com.testinium;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.google.common.reflect.TypeToken;
import com.testinium.model.ElementInfo;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.messages.internal.com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertFalse;

/**
 * The type Steps.
 */
public class Steps {

    /**
     * The Web driver.
     */
    public WebDriver webDriver;
    /**
     * The Actions.
     */
    public Actions actions;
    /**
     * The Web driver wait.
     */
    public WebDriverWait webDriverWait;

    /**
     * The constant JDBC_DRIVER.
     */
    static final String JDBC_DRIVER = "example.jdbc.driver";
    /**
     * The Db url.
     */
    static final String DB_URL = "example.db.url";
    /**
     * The User.
     */
    static final String USER = "example.user";
    /**
     * The Pass.
     */
    static final String PASS = "example.pass";
    /**
     * The Connection.
     */
    Connection connection = null;
    /**
     * The Statement.
     */
    Statement statement = null;
    private final int timeOut = 30;
    private final int sleepTime = 150;
    /**
     * The constant DEFAULT_MAX_ITERATION_COUNT.
     */
    public static int DEFAULT_MAX_ITERATION_COUNT = 150;
    /**
     * The constant DEFAULT_MILLISECOND_WAIT_AMOUNT.
     */
    public static int DEFAULT_MILLISECOND_WAIT_AMOUNT = 100;

    /**
     * The Logger.
     */
    protected Logger logger = Logger.getLogger(getClass());
    private static final String DEFAULT_DIRECTORY_PATH = "elementValues";
    /**
     * The Element map list.
     */
    ConcurrentMap<String, Object> elementMapList = new ConcurrentHashMap<>();
    /**
     * The Users.
     */
    Map<String, String> users = new HashMap<>();

    /**
     * Instantiates a new Steps.
     */
    public Steps() {
        initMap(getFileList());
        this.webDriver = BaseTest.getWebDriver();
        this.actions = new Actions(this.webDriver);
        this.webDriverWait = new WebDriverWait(webDriver, timeOut, sleepTime);
    }


    /**
     * Javascriptclicker.
     *
     * @param element the element
     */
    public void javascriptclicker(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
        executor.executeScript("arguments[0].click();", element);
    }

    /**
     * Find element web element.
     *
     * @param key the key
     * @return the web element
     */
    WebElement findElement(String key) {
        ElementInfo elementInfo = findElementInfoByKey(key);
        By infoParam = getElementInfoToBy(elementInfo);
        WebDriverWait webDriverWait = new WebDriverWait(webDriver, 60);
        WebElement webElement = webDriverWait
                .until(ExpectedConditions.presenceOfElementLocated(infoParam));
        ((JavascriptExecutor) webDriver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'})",
                webElement);
        return webElement;
    }

    /**
     * Find elements list.
     *
     * @param key the key
     * @return the list
     */
    List<WebElement> findElements(String key) {
        ElementInfo elementInfo = findElementInfoByKey(key);
        By infoParam = getElementInfoToBy(elementInfo);
        return webDriver.findElements(infoParam);
    }

    /**
     * Gets element info to by.
     *
     * @param elementInfo the element info
     * @return the element info to by
     */
    public static By getElementInfoToBy(ElementInfo elementInfo) {
        By by = null;
        if (elementInfo.getType().equals("css")) {
            by = By.cssSelector(elementInfo.getValue());
        } else if (elementInfo.getType().equals(("name"))) {
            by = By.name(elementInfo.getValue());
        } else if (elementInfo.getType().equals("id")) {
            by = By.id(elementInfo.getValue());
        } else if (elementInfo.getType().equals("xpath")) {
            by = By.xpath(elementInfo.getValue());
        } else if (elementInfo.getType().equals("linkText")) {
            by = By.linkText(elementInfo.getValue());
        } else if (elementInfo.getType().equals(("partialLinkText"))) {
            by = By.partialLinkText(elementInfo.getValue());
        }
        return by;
    }

    /**
     * Init map.
     *
     * @param fileList the file list
     */
    public void initMap(File[] fileList) {
        Type elementType = new TypeToken<List<ElementInfo>>() {
        }.getType();
        Gson gson = new Gson();
        List<ElementInfo> elementInfoList = null;
        for (File file : fileList) {
            try {
                elementInfoList = gson
                        .fromJson(new FileReader(file), elementType);
                elementInfoList.parallelStream()
                        .forEach(elementInfo -> elementMapList.put(elementInfo.getKey(), elementInfo));
            } catch (FileNotFoundException e) {
                logger.warn("{} not found", e);
            }
        }
    }

    /**
     * Get file list file [ ].
     *
     * @return the file [ ]
     */
    public File[] getFileList() {
        File[] fileList = new File(
                this.getClass().getClassLoader().getResource(DEFAULT_DIRECTORY_PATH).getFile())
                .listFiles(pathname -> !pathname.isDirectory() && pathname.getName().endsWith(".json"));
        if (fileList == null) {
            logger.warn(
                    "File Directory Is Not Found! Please Check Directory Location. Default Directory Path = {}" +
                            DEFAULT_DIRECTORY_PATH);
            throw new NullPointerException();
        }
        return fileList;
    }

    /**
     * Find element info by key element info.
     *
     * @param key the key
     * @return the element info
     */
    public ElementInfo findElementInfoByKey(String key) {
        return (ElementInfo) elementMapList.get(key);
    }

    /**
     * Save value.
     *
     * @param key   the key
     * @param value the value
     */
    public void saveValue(String key, String value) {
        elementMapList.put(key, value);
    }

    /**
     * Gets value.
     *
     * @param key the key
     * @return the value
     */
    public String getValue(String key) {
        return elementMapList.get(key).toString();
    }

    /**
     * Find element with key web element.
     *
     * @param key the key
     * @return the web element
     */
    public WebElement findElementWithKey(String key) {
        return findElement(key);
    }

    /**
     * Query execute.
     *
     * @param q the q
     */
    @And("Execute query {string}")
    public void queryExecute(String q) {
        try {
            logger.info(q + " query execute");

            if (q.contains("CREATE") || q.contains("UPDATE") || q.contains("INSERT") || q.contains("DROP")) {
                statement.executeUpdate(q);
            } else {
                ResultSet resultSet = statement.executeQuery(q);
                resultSet.first();
                users.put("mail", resultSet.getString("mail"));
                users.put("password", resultSet.getString("password"));
                resultSet.close();
            }
            logger.info("EXECUTED QUERY: " + q);
        } catch (SQLException throwables) {
            logger.info(throwables.getMessage());
        }

    }

    /**
     * Close jdbc connection.
     */
    @And("Close JDBC connection")
    public void closeJdbcConnection() {
        try {
            connection.close();
            statement.close();
            logger.info("close JDBC connection");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Write users.
     */
    @And("Write users")
    public void writeUsers() {
        logger.info("Mail: " + users.get("mail") + " Password: " + users.get("password"));
    }

    /**
     * Check element exists then click.
     *
     * @param key the key
     */
    @And("Wait for element and click {string}")
    public void checkElementExistsThenClick(String key) {
        getElementWithKeyIfExists(key);
        clickElement(key);
    }

    @And("initialize JDBC connection")
    public void initializeJdbcConnection() {
        try {
            logger.info("************************************  Initializing JDBC_DRIVER  ************************************");
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Write value to element
     *
     * @param key  the key
     * @param text the text
     */
    @And("Write text {string} to {string} element")
    public void writeValueToElement(String text, String key) {
        findElement(key).sendKeys(text);
    }

    @And("Write {string} value to {string} field")
    public void writeSavedValueToField(String key, String text) {
        findElement(key).sendKeys(users.get(text));
    }

    @Then("Check is element exist {string}")
    public void checkExistanceOfElement(String key) {
        Assert.assertTrue("Element is not visible", findElement(key).isDisplayed());
    }

    /**
     *
     * @param key the key
     */
    @And("Click to element {string}")
    public void elementineTıkla(String key) {
        if (findElement(key).isDisplayed()) {
            hoverElement(findElement(key));
            clickElement(findElement(key));
        }
    }

    @Then("Wait {int} seconds")
    public void waitSeconds(int second) {
        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Go to url
     *
     * @param uri the uri
     */
    @Given("Go to {string}")
    public void goToUrl(String uri) {
        webDriver.get(uri);
    }

    /**
     * Gets element with key if exists.
     *
     * @param key the key
     * @return the element with key if exists
     */
    @Given("Check is element exist with key {string}")
    public WebElement getElementWithKeyIfExists(String key) {
        WebElement webElement;
        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            try {
                webElement = findElementWithKey(key);
                return webElement;
            } catch (WebDriverException e) {
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        assertFalse(Boolean.parseBoolean("Element: '" + key + "' doesn't exist."));
        return null;
    }

    /**
     * Switch to.
     */
    @And("Switch to popup")
    public void switchTo() {
        for (String winHandle : webDriver.getWindowHandles()) {
            webDriver.switchTo().window(winHandle);
        }
    }

    /**
     * Click element.
     *
     * @param key the key
     */
    public void clickElement(String key) {
        if (!key.isEmpty()) {
            hoverElement(findElement(key));
            clickElement(findElement(key));
        }
    }

    /**
     * hoverElement
     *
     * @param element hover
     */
    private void hoverElement(WebElement element) {
        actions.moveToElement(element).build().perform();
    }


    /**
     * clickElement
     *
     * @param element click
     */
    private void clickElement(WebElement element) {
        element.click();
    }

    /**
     * Wait by milli seconds.
     *
     * @param milliseconds the milliseconds
     */
    public void waitByMilliSeconds(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

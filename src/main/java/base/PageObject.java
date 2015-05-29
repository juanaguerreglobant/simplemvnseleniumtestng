package base;

/**
 * Created by jaguerre on 29/05/15.
 */
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.IOException;
        import java.util.Iterator;
        import java.util.Properties;
        import java.util.Set;
        import java.util.concurrent.TimeUnit;

        import org.apache.commons.io.FileUtils;
        import org.openqa.selenium.Alert;
        import org.openqa.selenium.By;
        import org.openqa.selenium.Keys;
        import org.openqa.selenium.OutputType;
        import org.openqa.selenium.Point;
        import org.openqa.selenium.StaleElementReferenceException;
        import org.openqa.selenium.TakesScreenshot;
        import org.openqa.selenium.WebDriver;
        import org.openqa.selenium.WebElement;
        import org.openqa.selenium.chrome.ChromeDriver;
        import org.openqa.selenium.firefox.FirefoxDriver;
        import org.openqa.selenium.htmlunit.HtmlUnitDriver;
        import org.openqa.selenium.ie.InternetExplorerDriver;
        import org.openqa.selenium.interactions.Actions;
        import org.openqa.selenium.internal.Locatable;
        import org.openqa.selenium.support.ui.ExpectedCondition;
        import org.openqa.selenium.support.ui.ExpectedConditions;
        import org.openqa.selenium.support.ui.Select;
        import org.openqa.selenium.support.ui.WebDriverWait;
        import org.testng.Assert;
        import org.testng.ITestContext;
        import testutil.ErrorUtil;
        import com.google.common.base.Predicate;


public class PageObject {

    // initializing the property file reading
    public static Properties CONFIG=null;
    public static Properties OR=null;
    public static Properties MSG=null;
    public static WebDriver driver = null;
    public static Actions action = null;
    public static WebDriverWait wait = null;
    public static String mwh=null;
    public static int implicitTimeOut = 45; //default timeout time for the driver
    public static String baseURL;

    public PageObject(){
        if(driver == null){

            // config property file contains test config
            CONFIG = new Properties();
            // OR property file contains css selectors
            OR = new Properties();
            // MSG Property file contains expected system messages
            MSG = new Properties();

            try{
                //config
                FileInputStream fn = new FileInputStream(System.getProperty("user.dir")+"//src//config//config.properties");
                CONFIG.load(fn);

                //OR
                fn = new FileInputStream(System.getProperty("user.dir")+"//src//config//OR.properties");
                OR.load(fn);

            }catch(Exception e){
                Assert.assertTrue(false, e.getMessage());
            }

            // Initialize the WebDriver
            System.out.println(CONFIG.getProperty("browser"));
            if(CONFIG.getProperty("browser").equalsIgnoreCase("Firefox")){
                driver = new FirefoxDriver();
            }else if(CONFIG.getProperty("browser").equalsIgnoreCase("Chrome")){
                System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\chromedriver.exe");
                driver = new ChromeDriver();
                driver.manage().window().maximize();
            }else if(CONFIG.getProperty("browser").equalsIgnoreCase("IE")){
                System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"\\IEDriverServer.exe");
                driver = new InternetExplorerDriver();
            }else if(CONFIG.getProperty("browser").equalsIgnoreCase("HtmlUnit")){
                driver = new HtmlUnitDriver();
            }

            //wait for 30 seconds if element is not found before throwing error
            driver.manage().timeouts().implicitlyWait(implicitTimeOut, TimeUnit.SECONDS);

            action = new Actions(driver);
            wait = new WebDriverWait(driver, 60);
            mwh = driver.getWindowHandle();

        }
    }

    //page elements
    public By openModalLocator = By.cssSelector("body.modal-open");
    public By modalSubmitLocator = By.cssSelector("input[name='commit']");
    public By homeLinkLocator = By.cssSelector("img[title='Capstan Home']");
    public By searchButtonLocator = By.cssSelector("button[type='submit']>i");
    public By userDropdownLocator = By.xpath("//ul[contains(concat(' ', normalize-space(@class), ' '), ' pull-right ')]/li[last()]/a");
    public By pageLocator = By.cssSelector("body");
    public By reportsLinkLocator = By.linkText("Reports");

    // store screenshots
    public static void takeScreenshot(String filename) {
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File(System.getProperty("user.dir")+"\\Screenshots\\"+filename+".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Switch to popup window
    public void switchToPopUp(){

        Set<String> s=driver.getWindowHandles();

        Iterator<String> ite=s.iterator();

        while(ite.hasNext()){
            String popupHandle=ite.next().toString();
            if(!popupHandle.contains(mwh)){
                driver.switchTo().window(popupHandle);
            }
        }
    }

    //close popup window and switch to main window
    public void closePopUp(){
        driver.close();
        driver.switchTo().window(mwh);
    }

    //click webelement
    public void click(WebElement e){
        try{
            e.click();
        }catch(Exception f){
            try{
                action.moveToElement(e).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ARROW_DOWN).moveToElement(e).perform();
                try{
                    e.click();
                }catch(StaleElementReferenceException ser){
                    click(e);
                }
            }catch(Exception g){
                action.moveToElement(e).sendKeys(Keys.ARROW_UP).sendKeys(Keys.ARROW_UP).moveToElement(e).perform();
                e.click();
            }
        }
    }

    //click webelement using By
    public void click(By by){
        try{
            driver.findElement(by).click();
        }catch(Exception f){
            try{
                wait.until(elementPresent(by));
                WebElement e = driver.findElement(by);
                action.moveToElement(e).sendKeys(Keys.ARROW_DOWN).moveToElement(e).click().perform();
            }catch(StaleElementReferenceException ser){
                click(by);
            }
            catch(Exception g){
                WebElement e = driver.findElement(by);
                action.moveToElement(e).sendKeys(Keys.ARROW_UP).moveToElement(e).click().perform();
            }
        }
    }

    //makes sure that webelement comes into view, and then clicks
    public void clickFocus(By by){
        wait.until(elementPresent(by));
        WebElement e = driver.findElement(by);
        action.moveToElement(e).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ARROW_DOWN).moveToElement(e).click().perform();
    }


    //mouses over the element at the given locator
    public void mouseOver(WebElement e){
        click(e);
        Actions action = new Actions(driver);
        action.moveToElement(e).build().perform();
    }

    //send keys to text field webelement
    public void type(WebElement e, String value){
        try{
            e.sendKeys(Keys.CONTROL, "a");
            e.sendKeys(value);
        }catch(Exception err){
            ErrorUtil.addVerificationFailure(err);
        }
    }

    //send keys to text field webelement
    public void type(By by, String value){
        try{
            driver.findElement(by).sendKeys(Keys.CONTROL, "a");
            driver.findElement(by).sendKeys(value);
            if(!driver.findElement(by).getAttribute("value").equals(value)){
                driver.findElement(by).sendKeys(Keys.CONTROL, "a");
                driver.findElement(by).sendKeys(value);
            }
        }catch(Exception err){
            ErrorUtil.addVerificationFailure(err);
        }
    }


    //enter the path to a file contained in the testfiles directory of the selenium path (uses webelement)
    public void type_file(WebElement e, String value){
        if(value!=""&&value!=null){
            try{
                e.sendKeys(System.getProperty("user.dir")+"\\testfiles\\"+ value);
            }catch(Exception err){
                ErrorUtil.addVerificationFailure(err);
            }
        }
    }

    //gets the text of a webelement
    public String getText(By by){
        String text = null;
        WebElement e = driver.findElement(by);
        text = e.getText();
        return text;
    }

    //checks if an element is present on the page using cssVal
    public boolean isElementPresent(String cssVal){
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        try
        {
            driver.findElement(By.cssSelector(OR.getProperty(cssVal)));
            return true;
        }
        catch(Exception e)
        {
            try{
                driver.findElement(By.cssSelector(cssVal));
                return true;
            }catch(Exception f){
                return false;
            }
        }
        finally
        {
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        }
    }

    //checks if a webelement is present on the page
    public boolean isElementPresent(By by){
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
        boolean exists = driver.findElements( by ).size() != 0;
        driver.manage().timeouts().implicitlyWait(implicitTimeOut, TimeUnit.SECONDS);
        return exists;
    }

    //checks if a webelement is present on the page
    public boolean isElementPresentInOtherElement(WebElement e, By by){
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
        boolean exists = e.findElements( by ).size() != 0;
        driver.manage().timeouts().implicitlyWait(implicitTimeOut, TimeUnit.SECONDS);
        return exists;
    }

    //checks if link is present on the page
    public boolean isLinkPresent(String name){
        driver.manage().timeouts().implicitlyWait(implicitTimeOut, TimeUnit.SECONDS);
        try{
            driver.findElement(By.linkText(name));
            return true;
        }catch(Exception e){
            return false;
        }finally{
            driver.manage().timeouts().implicitlyWait(implicitTimeOut, TimeUnit.SECONDS);
        }

    }

    //checks if text is present on the page
    public boolean isTextPresent(String text){
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        String txt = text.replaceAll("'", "\'");
        try{
            return driver.findElement(pageLocator).getText().contains(txt);
        }catch(Throwable t){
            return false;
        }finally{
            driver.manage().timeouts().implicitlyWait(implicitTimeOut, TimeUnit.SECONDS);
        }
    }

    //clicks a link on the page
    public void clickLink(String name){
        try{
            try{
                driver.findElement(By.linkText(name)).click();
            }catch(Throwable t){
                driver.findElement(By.xpath("//*[contains(text(), '"+name+"')]")).click();
            }
        }catch(Throwable t){
            ErrorUtil.addVerificationFailure(t);
        }
    }

    //returns true if current page title matches the provided string
    public boolean isTitle(String expectedTitle){
        try{
            return getTitle().toLowerCase().contains(expectedTitle.toLowerCase());
        }catch(Exception e){

        }
        return false;
    }

    //loads the current page (takes in page url)
    public void loadPage(String page, ITestContext context){
        //loads site
        baseURL = context.getCurrentXmlTest().getParameter("selenium.url");
        PageObject.driver.get(baseURL+page);
    }

    //loads a page (assumes baseURL has already been set)
    public void loadPage(String page){
        //loads page
        PageObject.driver.get(baseURL+page);
    }

    //loads prod page
    public void loadProdPage(String page){
        //loads site
        PageObject.driver.get("http://capstan.stonecroptech.com/"+page);
    }



    //select from dropdown list
    public void select(WebElement e, String value){
        if (value!=""&&value!=null){
            try{
                Select selection = new Select(e);
                selection.selectByVisibleText(value);
            }catch(Exception err){
                Assert.assertTrue(false, err.getMessage());
            }
        }else{
            return;
        }
    }

    //select from dropdown list
    public void select(By by, String value){
        if (value!=""&&value!=null){
            Select selection = new Select(driver.findElement(by));
            selection.selectByVisibleText(value);
        }else{
            return;
        }
    }


    //selects a checkbox WebElement
    public void selectCheckBox(WebElement e){
        if (!e.isSelected()){
            e.click();
        }
    }

    //selects a checkbox using By
    public void selectCheckBox(By by){
        WebElement e = driver.findElement(by);
        if (!e.isSelected()){
            e.click();
        }
    }


    //deselects a checkbox WebElement
    public void deselectCheckBox(WebElement e){
        if (e.isSelected()){
            e.click();
        }
    }

    //deselects a checkbox using By
    public void deselectCheckBox(By by){
        WebElement e = driver.findElement(by);
        if (e.isSelected()){
            e.click();
        }
    }



    // Finds webelement input type and enters value accordingly
    public void enter(WebElement e, String value){
        try{
            if (e.getTagName().equalsIgnoreCase("iframe")){
                type(e, value);
            }else{
                if(e.getAttribute("type").equalsIgnoreCase("checkbox")){
                    if (value.equalsIgnoreCase("Y")){
                        selectCheckBox(e);
                    }else if (value.equalsIgnoreCase("N")||value.isEmpty()){
                        deselectCheckBox(e);
                    }
                }else if(e.getAttribute("type").equalsIgnoreCase("file")){
                    type_file(e, value);
                }else if(e.getAttribute("type").equalsIgnoreCase("text")||(e.getTagName().equalsIgnoreCase("textarea"))){
                    type(e, value);
                }else if(e.getTagName().equalsIgnoreCase("select")&&!value.isEmpty()){
                    select(e, value);
                }
            }
        }catch(Throwable err){
            ErrorUtil.addVerificationFailure(err);
        }
    }

    //waits for modal to Close
    public void waitForModalClose(){
        wait.withTimeout(10, TimeUnit.SECONDS).until(elementNotPresent(openModalLocator));
        wait.withTimeout(implicitTimeOut, TimeUnit.SECONDS);
    }

    //get current page title
    public String getTitle(){
        System.out.println(driver.getTitle());
        return driver.getTitle();
    }

    //go back in browser
    public void goBack(){
        driver.navigate().back();
    }

    //refreshes page
    public void refresh(){
        driver.navigate().refresh();
    }

    //waits until element is not present
    public Predicate<WebDriver> elementNotPresent(final By by) {
        return new Predicate<WebDriver>() {
            @Override public boolean apply(WebDriver driver) {
                return !isElementPresent(by);
            }
        };
    }

    //waits until element is present
    public Predicate<WebDriver> elementPresent(final By by) {
        return new Predicate<WebDriver>() {
            @Override public boolean apply(WebDriver driver) {
                return isElementPresent(by);
            }
        };
    }
}

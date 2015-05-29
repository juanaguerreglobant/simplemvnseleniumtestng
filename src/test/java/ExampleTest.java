/**
 * Created by jaguerre on 29/05/15.
 */
import base.PageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.HomePage;


public class ExampleTest {

    @Test
    public void fillForm(){
        HomePage homePage = new HomePage();
        PageObject.driver.get("http://goo.gl/gUqDHg");
        homePage.fillForm("Test", "Automation", "Nowhere Street", "test@automation.com", "Blue");
        homePage.submitForm();
    }
    @Test
     public void fillFormWithoutName(){
        HomePage homePage = new HomePage();
        PageObject.driver.get("http://goo.gl/gUqDHg");
        homePage.fillForm("", "", "Nowhere Street", "test@automation.com", "Blue");
        homePage.submitForm();
    }

    @AfterTest
    public void end(){
        PageObject.driver.quit();
    }
}
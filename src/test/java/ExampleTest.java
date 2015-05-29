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
import pages.StartUp;


public class ExampleTest {

    @BeforeTest
    public void loadPage(){

    }
    @Test
    public void fillForm(){
        HomePage homePage = new HomePage();
        homePage.loadBaseUrl();
        homePage.fillForm("Test", "Automation", "Nowhere Street", "test@automation.com", "Blue");
        homePage.submitForm();
    }
    @Test
     public void fillFormWithoutName(){
        HomePage homePage = new HomePage();
        homePage.loadBaseUrl();
        homePage.fillForm("", "", "Nowhere Street", "test@automation.com", "Blue");
        homePage.submitForm();
    }

    @AfterTest
    public void end(){
        PageObject.driver.quit();
    }
}
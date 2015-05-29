package pages;

import base.PageObject;
import org.openqa.selenium.By;

/**
 * Created by jaguerre on 29/05/15.
 */
public class StartUp extends PageObject {

    public void loadBaseUrl(){
        driver.get(CONFIG.getProperty("baseurl"));
    }

}

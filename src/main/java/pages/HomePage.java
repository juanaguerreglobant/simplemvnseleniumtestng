package pages;

import base.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Created by jaguerre on 29/05/15.
 */
public class HomePage extends PageObject {
    By byFirstNameField = By.id("entry_311820602");
    By byLastNameField = By.id("entry_1396008427");
    By byAddressField = By.id("entry_1447188970");
    By byEmailAddressField = By.id("entry_1453012221");
    By byEyeColorDrop = By.id("entry_1264472189");
    By bySubmitBtn = By.id("ss-submit");

    public void fillForm(String fName, String lName, String address, String emailAddress, String eyeColor){
        type(byFirstNameField, fName);
        type(byLastNameField, lName);
        type(byAddressField, address);
        type(byEmailAddressField, emailAddress);
        select(byEyeColorDrop, eyeColor);
    }

    public void submitForm(){
        click(bySubmitBtn);
    }


}

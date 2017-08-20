package info.yinhua.business;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import info.yinhua.business.CompanyInfo;

public class MyElement {
	
	@FindBy(xpath = CompanyInfo.XPATH_NP)
	public WebElement np;

	public MyElement(WebDriver driver) {
		PageFactory.initElements(driver, this);
	}
}

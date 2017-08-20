package info.yinhua;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import info.yinhua.business.CompanyInfo;
import info.yinhua.business.Condition;
import info.yinhua.business.ProductsManage;

/**
 *
 */
public class App
{
	public static void main(String[] args) {
//		WebDriver driver = new FirefoxDriver();
//		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		byte flg = 1;

		if (flg == 1) {
			// 1 TODO
			Condition c = new Condition("uk", "coat");

			CompanyInfo ci = new CompanyInfo(c);
			if (ci.search())
				ci.analy();
		} else if (flg == 2) {
			// 2
			ProductsManage pm = new ProductsManage();
			pm.update();
		}
    }
}

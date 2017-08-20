package info.yinhua.business;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * paused 2017-08-20
 */
public class ProductsManage extends SuperDriver
{
	private String list_url, iframe_id, class1, class2;

	private Map<String, String> groupMap = new HashMap<String, String>();

	public ProductsManage() {
		super();

		// TODO
		groupMap.put("Coveralls", "//*[@id=\"simple-group-select-0\"]/div/div/div/ul/li[2]");

		InputStream is = null;

		try {
			is = ClassLoader.getSystemResourceAsStream("config.properties");
			
			Properties properties = new Properties();
			properties.load(is);
			list_url = properties.getProperty("list_url");
			u = properties.getProperty("p.username");
			p = properties.getProperty("p.password");
			iframe_id = properties.getProperty("iframe_id");
			class1 = properties.getProperty("class_1");
			class2 = properties.getProperty("class_2");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.driver = new FirefoxDriver();
	}

	public void update() {

		// init wait
		webDriverWait = new WebDriverWait(driver, 10);
		ExpectedCondition<Boolean> expectedCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				List<WebElement> list = driver.findElements(By.id("layoutBox"));
				return list.size() > 0;
			}
		};

		driver.get(list_url);
		WebDriver iframe = driver.switchTo().frame(iframe_id);

		WebElement element = iframe.findElement(By.id("fm-login-id"));
		element.sendKeys(u);

		element = iframe.findElement(By.id("fm-login-password"));
		element.sendKeys(p);
		element.submit();

		// XXX slide to verify
		List<WebElement> slideList = driver.findElements(By.id("..."));
		if (!slideList.isEmpty()) {
			Actions builder = new Actions(driver);
			Action dragAndDrop = builder.clickAndHold(slideList.get(0)).moveByOffset(0,50).release().build();
			dragAndDrop.perform();
		}

		webDriverWait.until(expectedCondition);

		// close ad
		List<WebElement> adList = driver.findElements(By.className(class1));
		if (!adList.isEmpty())
			adList.get(0).click();

		each();

		driver.quit();
	}

	private void each() {

		WebElement element;

		String div3 = "//*[@id=\"layoutBox\"]/div/div/div/div[3]";

		ExpectedCondition<Boolean> expectedCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				List<WebElement> list = driver.findElements(By.className(class2));
				return list.isEmpty();
			}
		};

		for (String key : groupMap.keySet()) {
			String xpath = groupMap.get(key);

			// select-arrow-down
			element = driver.findElement(By.xpath("//*[@id=\"simple-group-select-0\"]/span/i"));
			element.click();

			// select each group
			element = driver.findElement(By.xpath(xpath));
			element.click();

			// check selected
			element = driver.findElement(By.xpath("//*[@id=\"simple-group-select-0\"]/span/input"));
			assert key.equals(element.getAttribute("value"));

			// search button
			element = driver.findElement(
					By.xpath(div3 + "/div[1]/div/div[1]/div/div[2]/div/div/form[2]/div/div[3]/button[1]"));
			element.click();
			webDriverWait.until(expectedCondition);

			// count of search
			List<WebElement> countList = driver.findElements(By.className("custom-result"));

			// XXX the while code has not tested
			while (!countList.isEmpty() && !"0".equals(countList.get(0).getText().split(":")[1].trim())) {

				// checkbox
				element = driver
						.findElement(By.xpath(div3 + "/div[2]/div/div/div[1]/div/table/tbody/tr/th[1]/div/label"));
				element.click();

				// check checkbox
				element = driver.findElement(
						By.xpath(div3 + "/div[2]/div/div/div[1]/div/table/tbody/tr/th[1]/div/label/input"));
				assert "true".equals(element.getAttribute("aria-checked"));

				// move-to button
				element = driver.findElement(By.xpath(div3 + "/div[1]/div/div[3]/button[2]"));
				element.click();

				// 未分组
				element = driver.findElement(By.xpath("/html/body/div[6]/div/div/ul/li[last()]"));
				element.click();

				// 确认
				element = driver.findElement(By.xpath("/html/body/div[6]/div/div/div/div/button[1]"));
				element.click();

				// search button
				element = driver.findElement(
						By.xpath(div3 + "/div[1]/div/div[1]/div/div[2]/div/div/form[2]/div/div[3]/button[1]"));
				element.click();
				webDriverWait.until(expectedCondition);

				countList = driver.findElements(By.className("custom-result"));
			}
			// ...

		}
	}
}

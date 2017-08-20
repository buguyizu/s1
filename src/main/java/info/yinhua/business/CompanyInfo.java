package info.yinhua.business;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.URIBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import info.yinhua.business.Const;
import info.yinhua.business.MyElement;

/**
 *
 */
public class CompanyInfo extends SuperDriver {

	public static final String HTTP = "http", MAIL = "邮  箱", NEXT_PAGE = "下一页",
			XPATH_NP = "//*[@id=\"Pager\"]/ul/li[@class=\"active\"]/following-sibling::li[1]/a"
			;

	private static final By by_u = By.id("tboxAccount"),
			by_p = By.id("tboxPassword"),
			by_startdate = By.id("tboxSatrtDate"),
			by_product = By.id("tboxProduct"),
			by_search = By.id("btnSubmit"),
			by_nextpage = By.xpath(XPATH_NP),
			by_names = By.xpath("//*[@id=\"ui-view-dataList\"]/table/tbody/tr/td[5]/a/p/strong");

	// search condition
	private Condition condition;

	private String info_url, param1, param2;
	private int default_page_size = 10;

	private Set<String> set = new HashSet<String>();

	public CompanyInfo(Condition condition) {
		super();

		this.condition = condition;

		InputStream is = null;

		try {
			// http://www.cnblogs.com/CloudTeng/archive/2012/04/08/2438028.html
			is = ClassLoader.getSystemResourceAsStream("config.properties");
			
			Properties properties = new Properties();
			properties.load(is);
			login_url = properties.getProperty("login_url");
			u = properties.getProperty("c.username");
			p = properties.getProperty("c.password");

			info_url = properties.getProperty("info_url");
			param1 = properties.getProperty("param1");
			param2 = properties.getProperty("param2");

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
	}

	public boolean search() {

		driver = new FirefoxDriver();

		// init wait
		WebDriverWait webDriverWait = new WebDriverWait(driver, 8);
		ExpectedCondition<Boolean> expectedCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				List<WebElement> list = driver.findElements(By.xpath("//body/div"));
				return list.size() == 1;
			}
		};

		/** 1.loging */
		driver.get(login_url);

		WebElement element = driver.findElement(by_u),
				element2 = driver.findElement(by_p);

		element.sendKeys(u);
		element2.sendKeys(p);
		element.submit();

//		ExpectedCondition<Boolean> expectedCondition0 = new ExpectedCondition<Boolean>() {
//			public Boolean apply(WebDriver d) {
//				List<WebElement> list = driver.findElements(By.id("pageshow"));
//				return list.size() == 1;
//			}
//		};
//		webDriverWait.until(expectedCondition0);

		/** 2.search */
		driver.navigate().to(condition.getUrl());

//		driver.findElement(by_startdate).sendKeys(country.startdate);
		driver.findElement(by_product).sendKeys(condition.getKeyword());
		driver.findElement(by_search).click();
		webDriverWait.until(expectedCondition);

		/** 3.write the first page to file */
		File f = new File(Const.file1);
		FileUtils.deleteQuietly(f);
		int page_count = writeNameOfEachPage(f), total_count = page_count;

		if (page_count == default_page_size) {
			/** 4.pagedown and write to file */
			element = driver.findElement(by_nextpage);

			Random r = new Random();

			// decide if has next page
			while (!"disabled".equals(element.getAttribute("class"))
					&& element.getText().indexOf(NEXT_PAGE) == -1
					&& page_count == default_page_size) {

				element.click();
				webDriverWait.until(expectedCondition);
				try {
					Thread.sleep(000 + r.nextInt(99));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				MyElement me = new MyElement(driver);
				element = me.np;

				page_count = writeNameOfEachPage(f);
				total_count += page_count;
			}
		}

		driver.quit();
		log.info("search end!");

		return total_count > 0;
	}

	public void analy() {

		driver = new HtmlUnitDriver();
//		driver = new FirefoxDriver();

		/** 1.login */
		driver.get(login_url);

		WebElement element = driver.findElement(by_u),
				element2 = driver.findElement(by_p);

		element.sendKeys(u);
		element2.sendKeys(p);

		element.submit();

		/** 2.read url from file */
		File f = new File(Const.file1),
				f2 = new File(Const.file2
						+ "_" + condition.getCountry() + "_" + condition.getKeyword() + "_" + date + Const.suffix);
		FileUtils.deleteQuietly(f2);
		Random r = new Random();

		try {
			List<String> lines = FileUtils.readLines(f);

			int i = 0;

			for (String line : lines) {
				
				if (line == null)
					continue;

				// http
				// because some name contains ','
				String[] arr = line.split(HTTP);
				
				if (arr.length < 2)
					continue;

				arr[0] = arr[0].split(Const.separator)[0];
				arr[1] = HTTP + arr[1];

				String mail = writeEachInfo(f2, arr);
				log.info(i++ + ": " + mail);

				try {
					Thread.sleep(200 + r.nextInt(999));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		driver.quit();
		log.info(" END !");
	}

	private String getMail(String text) {
		String mail = "";

		int i = text.indexOf(MAIL), j;

		if (i > -1) {
			i += MAIL.length();

			if (driver instanceof FirefoxDriver) {

				j = text.indexOf("\n", i);

			} else if (driver instanceof HtmlUnitDriver) {

				j = text.indexOf("  ", i);
				if (j == -1)
					j = text.length();
				else {
					j--;
				}

			} else
				return mail;

			mail = text.substring(i, j);
		}

		return mail.trim();
	}

	private int writeNameOfEachPage(File f) {

		List<WebElement> names = driver.findElements(by_names);
		try {
			// each name
			for (WebElement e : names) {
				String name = e.getText();
				URIBuilder ub = new URIBuilder(info_url);
				ub.addParameter(param1, URLEncoder.encode(name, "utf-8"));
				ub.addParameter(param2, condition.getCountryName());

				if (set.add(name))
					FileUtils.writeStringToFile(f, name + Const.separator + ub.toString() + Const.cr_lf, true);
				log.info(name);
			}
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return names.size();
	}

	private String writeEachInfo(File f, String[] arr) {

		/** access each url */
		driver.get(arr[1]);
		String mail = "";

		List<WebElement> list = driver.findElements(By.xpath("//ul"));

		if (list.size() > 1) {
			WebElement we = list.get(1);
			mail = getMail(we.getText());
		}

		try {
			if (!mail.isEmpty())
				FileUtils.writeStringToFile(f, arr[0] + Const.separator + mail + Const.cr_lf, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mail;
	}
}

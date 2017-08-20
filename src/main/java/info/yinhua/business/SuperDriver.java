package info.yinhua.business;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import info.yinhua.business.Const;

public class SuperDriver {

	protected Logger log;
	protected WebDriver driver;
	protected WebDriverWait webDriverWait;
	protected String date, login_url, u, p;

	protected SuperDriver() {

		System.setProperty("webdriver.firefox.bin", Const.firefox);

		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(new Formatter() {

			@Override
			public String format(LogRecord record) {
				return record.getLevel() + ":" + record.getMessage() + "\n";
			}
		});

		log = Logger.getLogger("gobal");
		log.addHandler(consoleHandler);
		log.setUseParentHandlers(false);

		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		date = df.format(new Date());

	}
}

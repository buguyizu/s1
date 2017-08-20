package info.yinhua.business;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Condition {

	private String url;
	private String keyword;
	private String startdate;
	private String country;
	private String countryName;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	// example ("uk", "shirts");
	public Condition(String coutry, String keyword) {

		InputStream is = null;

		try {
			is = ClassLoader.getSystemResourceAsStream("config.properties");
			
			Properties properties = new Properties();
			properties.load(is);
			String searchUrl = properties.getProperty("search_url");

			this.keyword = keyword;
			if ("uk".equals(coutry)) {
				this.country = coutry;
				this.url = searchUrl + "UK";
				this.countryName = "英国";
			}

			// XXX
			assert "uk".equals(coutry);
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
}

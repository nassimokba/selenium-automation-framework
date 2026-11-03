package webdriver;

import exceptions.BrowserNotSupportedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class WebdriverFactory {

    private static final String[] BROWSER_ARGS = {"--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu"};
    private static final String[] CHROMIUM_HEADLESS_ARGS = {"--headless=new", "--window-size=1920,1080"};
    private static final String[] FIREFOX_HEADLESS_ARGS = {"-headless", "--width=1920", "--height=1080"};

    public RobustWebdriver create(WebdriverConfig config) {
        String browserName = config.getBrowser().toUpperCase(Locale.ROOT);
        if (!isBrowserSupportedByOs(browserName)) throw new BrowserNotSupportedException(config.getBrowser());

        WebDriver driver;
        switch (browserName) {
            case "FIREFOX":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments(BROWSER_ARGS);
                if (config.isHeadless()) firefoxOptions.addArguments(FIREFOX_HEADLESS_ARGS);
                firefoxOptions.setCapability("webSocketUrl", true);
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case "CHROME":
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments(BROWSER_ARGS);
                if (config.isHeadless()) chromeOptions.addArguments(CHROMIUM_HEADLESS_ARGS);
                chromeOptions.setCapability("webSocketUrl", true);
                driver = new ChromeDriver(chromeOptions);
                break;
            case "EDGE":
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments(BROWSER_ARGS);
                if (config.isHeadless()) edgeOptions.addArguments(CHROMIUM_HEADLESS_ARGS);
                edgeOptions.setCapability("webSocketUrl", true);
                driver = new EdgeDriver(edgeOptions);
                break;
            default:
                throw new BrowserNotSupportedException(config.getBrowser());
        }

        driver.manage().window().maximize();
        return new RobustWebdriver(driver);
    }

    private boolean isBrowserSupportedByOs(String browserName) {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);

        if (os.contains("win")) return !browserName.contains("SAFARI");
        if (os.contains("mac")) return !browserName.contains("EDGE");
        return true;
    }
}
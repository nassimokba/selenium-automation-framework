package webdriver;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class RobustWebdriver implements WebDriver {
    private final int MAX_TRIES = 5;
    private final WebDriver webdriver;
    private DevTools devTools;

    public RobustWebdriver(WebDriver webdriver) {
        this.webdriver = webdriver;
    }

    public void executeScript(String jsScript, Object... args) {
        ((JavascriptExecutor) webdriver).executeScript(jsScript, args);
    }

    public File takeScreenshot(String path, String name) {
        File target;
        try {
            File src = ((TakesScreenshot) webdriver).getScreenshotAs(OutputType.FILE);
            target = new File(String.format("%s/%s.png", path, name));
            FileUtils.copyFile(src, target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return target;
    }

    public File takeFullPageScreenshot(String path, String filename) {
        File target = new File(String.format("%s/%s.png", path, filename));
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("captureBeyondViewport", true);
            params.put("format", "png");
            Map<String, Object> result;
            if (webdriver instanceof ChromeDriver)
                result = ((ChromeDriver) webdriver).executeCdpCommand("Page.captureScreenshot", params);
            else if (webdriver instanceof EdgeDriver)
                result = ((EdgeDriver) webdriver).executeCdpCommand("Page.captureScreenshot", params);
            else throw new UnsupportedOperationException("Full page screenshot not supported for this driver");
            FileUtils.writeByteArrayToFile(target, Base64.getDecoder().decode((String) result.get("data")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return target;
    }

    public void setDownloadDir(Path path) {
        Map<String, Object> params = new HashMap<>();
        params.put("behavior", "allow");
        params.put("downloadPath", path.toString());

        if (webdriver instanceof ChromeDriver)
            ((ChromeDriver) webdriver).executeCdpCommand("Browser.setDownloadBehavior", params);
        else if (webdriver instanceof EdgeDriver)
            ((EdgeDriver) webdriver).executeCdpCommand("Browser.setDownloadBehavior", params);
        else throw new UnsupportedOperationException("setDownloadDir not supported for this driver");
    }

    public String savePageHTML() {
        return webdriver.getPageSource();
    }

    public Actions actions() {
        return new Actions(webdriver);
    }

    @Override
    public void get(String url) {
        webdriver.get(url);
    }

    @Override
    public String getCurrentUrl() {
        return webdriver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return webdriver.getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        List<WebElement> elements = null;
        boolean found = false;
        int tries = 0;
        while (tries < MAX_TRIES && !found) {
            try {
                elements = webdriver.findElements(by);
                found = true;
            } catch (StaleElementReferenceException ignored) {
            }
            tries++;
        }
        return elements;
    }

    @Override
    public WebElement findElement(By by) {
        WebElement element = null;
        boolean found = false;
        int tries = 0;
        while (tries < MAX_TRIES && !found) {
            try {
                element = webdriver.findElement(by);
                found = true;
            } catch (StaleElementReferenceException ignored) {
            }
            tries++;
        }
        return element;
    }

    @Override
    public String getPageSource() {
        return webdriver.getPageSource();
    }

    @Override
    public void close() {
        webdriver.close();
    }

    @Override
    public void quit() {
        webdriver.quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return webdriver.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return webdriver.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return webdriver.switchTo();
    }

    @Override
    public Navigation navigate() {
        return webdriver.navigate();
    }

    @Override
    public Options manage() {
        return webdriver.manage();
    }
}

package webdriver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebdriverManager {

    @Autowired
    private WebdriverFactory webdriverFactory;
    private RobustWebdriver webdriver;
    private final WebdriverConfig config;

    public WebdriverManager(WebdriverConfig config) {
        this.config = config;
    }

    public RobustWebdriver getWebdriver() {
        if (webdriver == null) webdriver = webdriverFactory.create(config);
        return webdriver;
    }

    public void openUrl(String url) {
        getWebdriver().get(url);
    }

    public void quitDriver() {
        if (webdriver != null) {
            webdriver.quit();
            webdriver = null;
        }
    }
}
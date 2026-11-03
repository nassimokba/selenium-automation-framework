package webdriver;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class WebdriverConfig {
    private final String browser;
    private final boolean headless;

    public WebdriverConfig(@Value("${webdriver.browser}") String browser,
                           @Value("${webdriver.headless}") boolean headless) {
        this.browser = browser;
        this.headless = headless;
    }
}
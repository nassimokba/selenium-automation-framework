package cucumber;

import context.GlobalExecutionContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import report.factory.AttachmentFactory;
import utils.LoggerUtils;
import webdriver.WebdriverManager;
import webdriver.bidi.BrowserMonitor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CucumberHooks {

    private Logger logger = LoggerUtils.getLogger(CucumberHooks.class);

    @Value("${base.url}")
    private String baseUrl;

    @Autowired
    private WebdriverManager webdriverManager;

    @Autowired
    private GlobalExecutionContext globalExecutionContext;

    @Autowired
    private BrowserMonitor browserMonitor;

    @Before
    public void before() {
        browserMonitor.start();
        webdriverManager.openUrl(baseUrl);
    }

    @After
    public void after(Scenario scenario) {
        if (scenario.isFailed()) handleFailedScenario(globalExecutionContext.getCurrentScenario());
        webdriverManager.quitDriver();
        browserMonitor.stop();
    }

    private void handleFailedScenario(report.model.Scenario scenario) {
        try {
            String screenshotPath = scenario.getPath().toString();
            File screenshot = webdriverManager.getWebdriver()
                    .takeScreenshot(screenshotPath,
                            String.format("%s_error", scenario.getId()));

            byte[] screenshotBytes = Files.readAllBytes(screenshot.toPath());
            scenario.addAttachment(AttachmentFactory.screenshot(screenshotBytes));

            Files.deleteIfExists(screenshot.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

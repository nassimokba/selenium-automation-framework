package cucumber;

import context.GlobalExecutionContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import webdriver.WebdriverManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CucumberHooks {

    @Value("${base.url}")
    private String baseUrl;

    @Autowired
    private WebdriverManager webdriverManager;

    @Autowired
    private GlobalExecutionContext globalExecutionContext;

    @Before
    public void before() {
        webdriverManager.openUrl(baseUrl);
    }

    @After
    public void after(Scenario scenario) {
        if (scenario.isFailed()) handleFailedScenario(globalExecutionContext.getCurrentScenario());
        webdriverManager.quitDriver();
    }

    private void handleFailedScenario(report.model.Scenario scenario) {
        try {
            String screenshotPath = Path.of(globalExecutionContext.getReportOutputPath(), "screenshots").toString();
            File screenshot = webdriverManager.getWebdriver()
                    .takeScreenshot(screenshotPath,
                            String.format("%s_error", scenario.getName()));

            byte[] screenshotBytes = Files.readAllBytes(screenshot.toPath());
            scenario.setScreenshot(screenshotBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

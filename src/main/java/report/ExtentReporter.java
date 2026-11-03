package report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import report.model.Scenario;
import report.model.StepScenario;
import utils.LoggerUtils;

import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExtentReporter implements IReporter {

    private final Map<String, ExtentTest> features = new HashMap<>();
    private Logger logger = LoggerUtils.getLogger(ExtentReporter.class);
    private ExtentReports extent;

    private static String addCustomCss() {
        return """
                textarea {
                    field-sizing: content;
                    width: 100%;
                    resize: none;
                }
                """;
    }

    @Override
    public void createReport(String name, Path reportPath) {
        String path = Path.of(reportPath.toString(), String.format("%s.html", name)).toString();
        ExtentSparkReporter extentSparkReporter = new ExtentSparkReporter(path);
        extentSparkReporter.config().setDocumentTitle("Cucumber Test Report");
        extentSparkReporter.config().setReportName("Selenium Test Automation Report");
        extentSparkReporter.config().setCss(addCustomCss());

        extent = new ExtentReports();
        extent.attachReporter(extentSparkReporter);
    }

    @Override
    public void createScenario(Scenario scenario) {
        ExtentTest node = getOrCreateFeature(scenario.getParentFeature().getName()).createNode(scenario.getName());
        for (StepScenario stepScenario : scenario.getStepList())
            node.log(Status.PASS, stepScenario.getKeyword() + stepScenario.getText());
        if (scenario.getErrorMessage() != null) {
            if (scenario.getScreenshot() != null) {
                String base64 = Base64.getEncoder().encodeToString(scenario.getScreenshot());
                node.fail(String.format("%s %s", MarkupHelper.createCodeBlock(scenario.getErrorMessage()).getMarkup(),
                        formatScreenshot(base64)));
            } else node.fail(scenario.getErrorMessage());
        }
        logger.info("createScenario {}", scenario.getId());
    }

    @Override
    public void writeReport() {
        extent.flush();
    }

    @Override
    public boolean isReportCreated() {
        return extent != null;
    }

    private ExtentTest getOrCreateFeature(String featureName) {
        ExtentTest feature = features.get(featureName);
        if (feature == null) {
            feature = extent.createTest(featureName);
            features.put(featureName, feature);
        }
        return feature;
    }

    private String formatScreenshot(String base64) {
        return "<br/><img src='data:image/png;base64," + base64 + "' style='max-width:600px'/>";
    }
}

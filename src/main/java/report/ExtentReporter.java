package report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import report.model.Attachment;
import report.model.Scenario;
import report.model.StepScenario;
import utils.LoggerUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
public class ExtentReporter implements IReporter {

    private final Map<String, ExtentTest> features = new HashMap<>();
    private Logger logger = LoggerUtils.getLogger(ExtentReporter.class);
    private ExtentReports extent;
    private Path reportPath;

    @Override
    public void createReport(String name, Path reportPath) {
        this.reportPath = reportPath;
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
        node.getModel().setStartTime(Date.from(scenario.getStartTime()));
        for (StepScenario stepScenario : scenario.getStepList())
            node.log(Status.PASS, stepScenario.getKeyword() + stepScenario.getText());
        if (toExtentStatus(scenario.getStatus()).equals(Status.FAIL)) {
            List<Attachment> attachmentList = scenario.getAttachmentByType(MimeType.IMAGE_PNG);
            for (Attachment attachment : attachmentList) {
                String base64 = Base64.getEncoder().encodeToString(attachment.getContent());
                node.fail(String.format("%s %s",
                        MarkupHelper.createCodeBlock(scenario.getErrorMessage()).getMarkup(),
                        formatScreenshot(base64)));
            }

            attachmentList = scenario.getAttachmentByType(MimeType.TEXT_PLAIN);
            node.info(createAttachmentBlock(attachmentList));
        }
        logger.info("createScenario {}", scenario.getId());
    }

    @Override
    public Path getReportPath() {
        return reportPath;
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

    private Status toExtentStatus(String status) {
        switch (status.toUpperCase()) {
            case "PASSED":
                return Status.PASS;
            case "FAILED":
                return Status.FAIL;
            case "SKIPPED":
                return Status.SKIP;
            default:
                return Status.WARNING;
        }
    }

    private String formatScreenshot(String base64) {
        return "<br/><img src='data:image/png;base64," + base64 + "' style='max-width:600px'/>";
    }

    private String createAttachmentBlock(List<Attachment> attachmentList) {
        StringBuilder html = new StringBuilder();
        html.append("<p>Error log files:</p>");
        html.append("<ol>");

        for (Attachment attachment : attachmentList) {
            String url = attachment.getPath().toAbsolutePath().toUri().toString();

            html.append("<li><a href=\"")
                    .append(url)
                    .append("\" target=\"_blank\">")
                    .append(attachment.getName())
                    .append("</a></li>");
        }
        html.append("</ol>");

        return html.toString();
    }

    private static String addCustomCss() {
        return """
                textarea {
                    field-sizing: content;
                    width: 100%;
                    resize: none;
                }
                """;
    }
}

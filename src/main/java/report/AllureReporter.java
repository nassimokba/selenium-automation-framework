package report;

import io.qameta.allure.AllureExternalKey;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.AttachmentOptions;
import io.qameta.allure.FileSystemResultsWriter;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.model.TestResult;
import io.qameta.allure.util.ResultsUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import report.model.Scenario;
import report.model.StepScenario;
import utils.LoggerUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Component
public class AllureReporter implements IReporter {

    private Logger logger = LoggerUtils.getLogger(AllureReporter.class);
    private AllureLifecycle allureReport;
    private AllureExternalKey key;
    private Path reportPath;

    @Override
    public void createReport(String name, Path reportPath) {
        this.reportPath = reportPath;
        allureReport = new AllureLifecycle(new FileSystemResultsWriter(reportPath));
        key = AllureExternalKey.random(AllureReporter.class);
    }

    @Override
    public void createScenario(Scenario scenario) {
        String uuid = UUID.randomUUID().toString();

        long start = scenario.getStartTime().toEpochMilli();
        long stop = scenario.getEndTime().toEpochMilli();

        TestResult testResult = new TestResult()
                .setUuid(uuid)
                .setName(scenario.getName())
                .setFullName(scenario.getParentFeature().getName())
                .setStart(start)
                .setStop(stop)
                .setHistoryId(ResultsUtils.md5(scenario.getParentFeature().getName()))
                .setLabels(List.of(ResultsUtils.createFeatureLabel(scenario.getParentFeature().getName())));

        allureReport.scheduleTest(key, testResult);
        allureReport.startTest(key);
        allureReport.stopTest(key);

        allureReport.updateTest(key, result -> {
            result.setStart(start);
            result.setStop(stop);
            result.setStatus(toAllureStatus(scenario.getStatus()));

            for (StepScenario step : scenario.getStepList()) {
                result.getSteps().add(new StepResult()
                        .setName(step.getText())
                        .setStatus(toAllureStatus(step.getStatus())));
            }

            if (scenario.getErrorMessage() != null) {
                result.setStatusDetails(new StatusDetails().setMessage(scenario.getErrorMessage()));
                if (scenario.getScreenshot() != null) {
                    allureReport.addAttachment(key,
                            String.format("Screenshot scenario %s", scenario.getId()),
                            "image/png",
                            new ByteArrayInputStream(scenario.getScreenshot()),
                            AttachmentOptions.empty());
                }
            }
        });
    }

    @Override
    public boolean isReportCreated() {
        return allureReport != null;
    }

    @Override
    public void writeReport() {
        allureReport.writeTest(key);
        generateHTML();
    }

    public void generateHTML() {
        Path cli = Path.of("target", "allure-cli", "allure-2.44.0", "bin", "allure").toAbsolutePath();
        Path html = reportPath.resolveSibling("allure-report");

        cli.toFile().setExecutable(true);
        try {
            Process process = new ProcessBuilder(
                    cli.toString(), "generate", reportPath.toString(), "--clean", "-o", html.toString())
                    .inheritIO()
                    .start();
            if (process.waitFor() != 0) logger.error("Cannot generate {} file", html);
        } catch (IOException e) {
            logger.warn("Allure report CLI not found.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Status toAllureStatus(String status) {
        switch (status.toUpperCase()) {
            case "PASSED":
                return Status.PASSED;
            case "FAILED":
                return Status.FAILED;
            case "SKIPPED":
                return Status.SKIPPED;
            default:
                return Status.BROKEN;
        }
    }
}

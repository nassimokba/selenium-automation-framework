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
import report.model.Attachment;
import report.model.ReportConfig;
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
    private Path reportArtifactsPath;

    @Override
    public void createReport(ReportConfig reportConfig) {
        this.reportPath = reportConfig.getReportPath();
        this.reportArtifactsPath = reportConfig.getReportArtifactsPath();
        allureReport = new AllureLifecycle(new FileSystemResultsWriter(reportArtifactsPath));
        key = AllureExternalKey.random(AllureReporter.class);
    }

    @Override
    public void createScenario(Scenario scenario) {
        String uuid = UUID.randomUUID().toString();
        String fullName = String.format("%s: %s", scenario.getParentFeature().getName(), scenario.getName());

        TestResult testResult = new TestResult()
                .setUuid(uuid)
                .setName(scenario.getName())
                .setFullName(fullName)
                .setHistoryId(ResultsUtils.md5(fullName))
                .setLabels(List.of(ResultsUtils.createFeatureLabel(scenario.getParentFeature().getName())));

        allureReport.scheduleTest(key, testResult);
        allureReport.startTest(key);
        allureReport.stopTest(key);

        Status status = toAllureStatus(scenario.getStatus());

        allureReport.updateTest(key, result -> {
            result.setStatus(status);
            result.setStart(scenario.getStartTime().toEpochMilli());
            result.setStop(scenario.getEndTime().toEpochMilli());

            for (StepScenario step : scenario.getStepList()) {
                result.getSteps().add(new StepResult()
                        .setName(step.getText())
                        .setStatus(toAllureStatus(step.getStatus())));
            }
            if (status == Status.FAILED) {
                result.setStatusDetails(new StatusDetails().setMessage(scenario.getErrorMessage()));
                addAttachements(scenario);
            }
        });
    }

    @Override
    public boolean isReportCreated() {
        return allureReport != null;
    }

    @Override
    public Path getReportArtifactsPath() { return reportArtifactsPath; }

    @Override
    public void writeReport() {
        allureReport.writeTest(key);
        generateHTML();
    }

    public void generateHTML() {
        Path cli = Path.of("target", "allure-cli", "allure-2.44.0", "bin", "allure").toAbsolutePath();

        cli.toFile().setExecutable(true);
        try {
            Process process = new ProcessBuilder(
                    cli.toString(), "generate", reportPath.toString(), "--clean", "-o", reportPath.toString())
                    .inheritIO()
                    .start();
            if (process.waitFor() != 0) logger.error("Cannot generate {} file", reportPath.toString());
        } catch (IOException e) {
            logger.warn("Allure report CLI not found.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void addAttachements(Scenario scenario) {
        List<Attachment> attachmentList = scenario.getAttachments();
        for (Attachment attachment : attachmentList) {
            allureReport.addAttachment(key,
                    attachment.getName(),
                    attachment.getMimeType().getValue(),
                    new ByteArrayInputStream(attachment.getContent()),
                    AttachmentOptions.empty());
        }
    }

    private Status toAllureStatus(String status) {
        if (status == null) return Status.BROKEN;

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
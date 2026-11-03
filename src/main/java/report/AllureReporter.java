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
import org.springframework.stereotype.Component;
import report.model.Scenario;
import report.model.StepScenario;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Component
public class AllureReporter implements IReporter {

    private AllureLifecycle allureReport;
    private AllureExternalKey key;


    @Override
    public void createReport(String name, Path reportPath) {
        allureReport = new AllureLifecycle(new FileSystemResultsWriter(reportPath));
        key = AllureExternalKey.random(AllureReporter.class);
    }

    @Override
    public void createScenario(Scenario scenario) {
        String uuid = UUID.randomUUID().toString();
        String fullName = String.format("%s: %s", scenario.getFeatureName(), scenario.getName());

        TestResult testResult = new TestResult()
                .setUuid(uuid)
                .setName(scenario.getName())
                .setFullName(fullName)
                .setHistoryId(ResultsUtils.md5(fullName))
                .setLabels(List.of(ResultsUtils.createFeatureLabel(scenario.getFeatureName())));

        allureReport.scheduleTest(key, testResult);
        allureReport.startTest(key);
        allureReport.stopTest(key);

        allureReport.updateTest(key, result -> {
            result.setStatus(toAllureStatus(scenario.getStatus()));

            for (StepScenario step : scenario.getStepList()) {
                result.getSteps().add(new StepResult()
                        .setName(step.getText())
                        .setStatus(toAllureStatus(step.getStatus())));
            }

            if (scenario.getErrorMessage() != null) {
                result.setStatusDetails(new StatusDetails().setMessage(scenario.getErrorMessage()));
                if (scenario.getScreenshot() != null) {
                    allureReport.addAttachment(key, "Screenshot", "image/png",
                            new ByteArrayInputStream(scenario.getScreenshot()), AttachmentOptions.empty());
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

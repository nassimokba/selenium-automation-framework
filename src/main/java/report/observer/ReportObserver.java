package report.observer;

import exceptions.ReporterNotSupportedException;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import report.*;
import report.factory.AttachmentFactory;
import report.model.ReportConfig;
import report.model.Scenario;
import utils.DirectoryUtils;
import utils.FileUtils;
import utils.LoggerUtils;
import java.nio.file.Path;

@Component
public class ReportObserver implements ExecutionObserver {

    private Logger logger = LoggerUtils.getLogger(ReportObserver.class);
    private IReporter reporter;
    private ReportConfig reportConfig;

    @Override
    public void onExecutionStarted(ReportConfig reportConfig) {
        this.reportConfig = reportConfig;
        if (reporter == null) initReport();
        if (!reporter.isReportCreated()) reporter.createReport(reportConfig);
    }

    @Override
    public void onScenarioStarted(Scenario scenario) {
        Path reportPath = reporter.getReportArtifactsPath();
        Path scenarioPath = reportPath.resolve(scenario.getParentFeature().getId()).resolve(scenario.getId());
        DirectoryUtils.createDirectory(scenarioPath);
        scenario.setPath(scenarioPath);
    }

    @Override
    public void onScenarioFinished(Scenario scenario) {
        if (scenario.getConsoleError() != null) {
            Path path = scenario.getPath().resolve(String.format("%s_console_errors.log", scenario.getId()));
            FileUtils.writeContentToFile(path, scenario.getConsoleError().toString());
            scenario.addAttachment(AttachmentFactory.buildAttachment(path, MimeType.TEXT_PLAIN));
        }
        if (scenario.getNetworkError() != null) {
            Path path = scenario.getPath().resolve(String.format("%s_network_errors.log", scenario.getId()));
            FileUtils.writeContentToFile(path, scenario.getNetworkError().toString());
            scenario.addAttachment(AttachmentFactory.buildAttachment(path, MimeType.TEXT_PLAIN));
        }
        reporter.createScenario(scenario);
        reporter.writeReport();
    }

    private void initReport() {
        Reporters tool = Reporters.valueOf(reportConfig.getReportType().toUpperCase());
        switch (tool) {
            case EXTENT:
                reporter = new ExtentReporter();
                break;
            case ALLURE:
                reporter = new AllureReporter();
                break;
            default:
                throw new ReporterNotSupportedException(reportConfig.getReportType());
        }
    }
}

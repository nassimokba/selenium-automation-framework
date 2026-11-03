package report.observer;

import exceptions.ReporterNotSupportedException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import report.AllureReporter;
import report.ExtentReporter;
import report.IReporter;
import report.Reporters;
import report.model.Scenario;
import utils.LoggerUtils;

import java.nio.file.Path;

@Component
public class ReportObserver implements ExecutionObserver {

    private Logger logger = LoggerUtils.getLogger(ReportObserver.class);
    private IReporter reporter;

    @Value("${report}")
    private String report;

    @Override
    public void onExecutionStarted(String name, Path path) {
        if (reporter == null) initReport();
        if (!reporter.isReportCreated()) reporter.createReport(name, path);
    }

    @Override
    public void onScenarioFinished(Scenario scenario) {
        reporter.createScenario(scenario);
        reporter.writeReport();
    }

    private void initReport() {
        Reporters tool = Reporters.valueOf(report.toUpperCase());
        switch (tool) {
            case EXTENT:
                reporter = new ExtentReporter();
                break;
            case ALLURE:
                reporter = new AllureReporter();
                break;
            default:
                throw new ReporterNotSupportedException(report);
        }
    }
}

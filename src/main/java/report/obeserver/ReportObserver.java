package report.obeserver;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import report.IReporter;
import report.model.Scenario;
import utils.LoggerUtils;

import java.nio.file.Path;

@Component
public class ReportObserver implements ExecutionObserver {

    private Logger logger = LoggerUtils.getLogger(ReportObserver.class);

    @Autowired
    private IReporter reporter;

    @Override
    public void onExecutionStarted(String name, Path path) {
        if (!reporter.isReportCreated()) reporter.createReport(name, path);
    }

    @Override
    public void onScenarioFinished(Scenario scenario) {
        reporter.createScenario(scenario);
        reporter.writeReport();
    }
}

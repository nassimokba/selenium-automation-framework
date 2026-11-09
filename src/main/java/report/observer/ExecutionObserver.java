package report.observer;

import report.model.ReportConfig;
import report.model.Scenario;

import java.nio.file.Path;

public interface ExecutionObserver {

    void onExecutionStarted(ReportConfig reportConfig);

    void onScenarioStarted(Scenario scenario);

    void onScenarioFinished(Scenario scenario);
}

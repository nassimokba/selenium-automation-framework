package report.observer;

import report.model.Scenario;

import java.nio.file.Path;

public interface ExecutionObserver {

    void onExecutionStarted(String name, Path path);

    void onScenarioFinished(Scenario scenario);
}

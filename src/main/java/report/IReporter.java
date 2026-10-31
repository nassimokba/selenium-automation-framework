package report;

import report.model.Scenario;

import java.nio.file.Path;

public interface IReporter {

    void createReport(String name, Path reportPath);

    void createScenario(Scenario scenario);

    boolean isReportCreated();

    void writeReport();
}

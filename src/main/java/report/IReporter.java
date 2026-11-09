package report;

import report.model.ReportConfig;
import report.model.Scenario;

import java.nio.file.Path;

public interface IReporter {

    void createReport(ReportConfig reportConfig);

    void createScenario(Scenario scenario);

    boolean isReportCreated();

    Path getReportPath();

    Path getReportArtifactsPath();

    void writeReport();
}

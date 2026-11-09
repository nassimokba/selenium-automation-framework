package context;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import report.model.Feature;
import report.model.ReportConfig;
import report.model.Scenario;
import report.observer.ReportObserver;
import utils.DateUtils;
import utils.DirectoryUtils;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class GlobalExecutionContext {

    @Autowired
    private ReportConfig reportConfig;
    @Autowired
    private ReportObserver reportObserver;
    @Getter
    private List<Feature> featureList = new ArrayList<>();
    @Getter
    private Feature currentFeature;
    @Getter
    private Scenario currentScenario;
    private String executionStartTimestamp;

    public void startTestExecution() {
        if (!isStartTimeSet()) {
            executionStartTimestamp = DateUtils.currentTimestamp(DateUtils.YYYYMMDD_HHMMSS);
            Path executionPath = DirectoryUtils.createDirectory(executionStartTimestamp, reportConfig.getReportPath());
            Path artifactsPath = DirectoryUtils.createDirectory(reportConfig.getReportArtifactsPath().resolve(executionStartTimestamp));
            reportObserver.onExecutionStarted(reportConfig.getReportName(), executionPath, artifactsPath);
            reportConfig.setReportPath(executionPath);
        }
    }

    public void startFeature(Feature feature) {
        this.currentFeature = feature;
        addFeature(feature);
    }

    public void startScenario(Scenario scenario) {
        currentScenario = scenario;
        addScenario(scenario);
        reportObserver.onScenarioStarted(scenario);
    }

    public void endScenario(Scenario scenario) {
        reportObserver.onScenarioFinished(scenario);
    }

    public void addFeature(Feature feature) {
        if (!featureList.contains(feature)) featureList.add(feature);
    }

    public void addScenario(Scenario scenario) {
        Feature parentFeature = featureList.stream().filter(feature -> feature.getUri().equals(scenario.getUri())).findFirst().get();
        parentFeature.addScenario(scenario);
    }

    private boolean isStartTimeSet() {
        return executionStartTimestamp != null;
    }
}

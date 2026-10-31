package context;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import report.model.Feature;
import report.model.Scenario;
import report.obeserver.ReportObserver;
import utils.DateUtils;
import utils.DirectoryUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
@Getter
public class GlobalExecutionContext {

    @Autowired
    private ReportObserver reportObserver;
    @Value("${report.name}")
    private String reportName;
    @Value("${report.output.path}")
    private String reportOutputPath;
    private List<Feature> featureList = new ArrayList<>();
    private Feature currentFeature;
    private Scenario currentScenario;
    private String executionStartTimestamp;

    public void startTestExecution() {
        if (!isStartTimeSet()) {
            executionStartTimestamp = DateUtils.currentTimestamp(DateUtils.YYYYMMDD_HHMMSS);
            Path executionPath = DirectoryUtils.createDirectory(executionStartTimestamp, Path.of(reportOutputPath));
            reportObserver.onExecutionStarted(reportName, executionPath);
            reportOutputPath = executionPath.toString();
        }
    }

    public void startFeature(Feature feature) {
        this.currentFeature = feature;
        addFeature(feature);
    }

    public void startScenario(Scenario scenario) {
        currentScenario = scenario;
        addScenario(scenario);
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

package context;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import report.model.Feature;
import report.model.Scenario;
import report.obeserver.ReportObserver;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
public class GlobalExecutionContext {

    @Value("${output.path}")
    private String reportPath;
    private List<Feature> featureList = new ArrayList<>();
    private Feature currentFeature;
    private Scenario currentScenario;

    @Autowired
    private ReportObserver observer;

    public void startFeature(Feature feature) {
        this.currentFeature = feature;
        addFeature(feature);
    }

    public void startScenario(Scenario scenario) {
        currentScenario = scenario;
        addScenario(scenario);
    }

    public void endScenario(Scenario scenario) {
        observer.onScenarioFinished(scenario);
    }

    public void addFeature(Feature feature) {
        if (!featureList.contains(feature)) featureList.add(feature);
    }

    public void addScenario(Scenario scenario) {
        Feature parentFeature = featureList.stream().filter(feature -> feature.getUri().equals(scenario.getUri())).findFirst().get();
        parentFeature.addScenario(scenario);
    }

    public Scenario getCurrentScenario() {
        return currentScenario;
    }
}

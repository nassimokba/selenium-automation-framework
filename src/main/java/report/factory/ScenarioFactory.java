package report.factory;

import io.cucumber.plugin.event.TestCase;
import report.model.Feature;
import report.model.Scenario;

import java.util.ArrayList;

public class ScenarioFactory {

    public static Scenario buildScenario(TestCase testCase, Feature feature) {
        int index = testCase.getTags().size() - 1;
        String tag = testCase.getTags().get(index).replace("@", "");
        return Scenario.builder()
                .uri(testCase.getUri())
                .featureName(feature.getName())
                .line(testCase.getLocation().getLine())
                .name(tag)
                .description(testCase.getName())
                .stepList(new ArrayList<>())
                .build();
    }
}

package report.factory;

import io.cucumber.plugin.event.TestCase;
import report.model.Feature;
import report.model.Scenario;

import java.time.Instant;
import java.util.ArrayList;

public class ScenarioFactory {

    public static Scenario buildScenario(TestCase testCase, Feature feature) {
        int index = testCase.getTags().size() - 1;
        String tag = testCase.getTags().get(index).replace("@", "");
        return Scenario.builder()
                .uri(testCase.getUri())
                .parentFeature(feature)
                .line(testCase.getLocation().getLine())
                .id(tag)
                .startTime(Instant.now())
                .description(testCase.getName())
                .stepList(new ArrayList<>())
                .attachments(new ArrayList<>())
                .build();
    }
}

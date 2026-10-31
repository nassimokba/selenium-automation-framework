package report.factory;

import io.cucumber.plugin.event.TestCase;
import report.model.Scenario;

import java.util.ArrayList;

public class ScenarioFactory {

    public static Scenario buildScenario(TestCase testCase) {
        return Scenario.builder()
                .uri(testCase.getUri())
                .line(testCase.getLocation().getLine())
                .name(testCase.getName())
                .stepList(new ArrayList<>())
                .build();
    }
}

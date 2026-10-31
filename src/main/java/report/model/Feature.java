package report.model;

import lombok.Builder;
import lombok.Getter;

import java.net.URI;
import java.util.List;

@Builder
@Getter
public class Feature {
    private final URI uri;
    private final String name;
    private final List<Scenario> scenarios;

    public void addScenario(Scenario scenario) {
        scenarios.add(scenario);
    }
}

package report.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.util.List;

@Builder
@Getter
@Setter
public class Scenario {

    private URI uri;
    private int line;
    private String name;
    private String status;
    private String errorMessage;
    private List<StepScenario> stepList;

    public void addStep(StepScenario step) {
        stepList.add(step);
    }

    public StepScenario getCurrentStep() {
        return stepList.get(stepList.size() - 1);
    }
}

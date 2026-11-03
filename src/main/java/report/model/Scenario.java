package report.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import webdriver.model.ConsoleError;
import webdriver.model.NetworkError;

import java.net.URI;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Builder
@Getter
@Setter
public class Scenario {

    private URI uri;
    private int line;
    private Feature parentFeature;
    private String id;
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<StepScenario> stepList;
    private StepScenario currentStep;
    private String status;
    private String errorMessage;
    private byte[] screenshot;
    private ConsoleError consoleError;
    private NetworkError networkError;

    public void addStep(StepScenario step) {
        stepList.add(step);
        currentStep = step;
    }

    public String getName() { return String.format("%s: %s", id, description); }

    public StepScenario getCurrentStep() {
        return currentStep;
    }

    public String getDuration() {
        Duration duration = Duration.between(startTime, endTime);

        return String.format("%02d:%02d:%02d",
                duration.toHours(),
                duration.toMinutesPart(),
                duration.toSecondsPart());
    }
    public void addConsoleError(ConsoleError consoleError) {
        this.consoleError = consoleError;
    }

    public void addNetworkError(NetworkError networkError) {
        this.networkError = networkError;
    }
}

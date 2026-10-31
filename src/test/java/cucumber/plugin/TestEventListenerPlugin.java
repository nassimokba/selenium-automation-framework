package cucumber.plugin;

import context.GlobalExecutionContext;
import context.SpringContext;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import org.slf4j.Logger;
import report.factory.FeatureFactory;
import report.factory.ScenarioFactory;
import report.factory.StepScenarioFactory;
import report.model.Feature;
import report.model.Scenario;
import report.model.StepScenario;
import utils.JsonExporter;
import utils.LoggerUtils;

import java.net.URI;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestEventListenerPlugin implements ConcurrentEventListener {

    private final Map<URI, Feature> features = new LinkedHashMap<>();
    private Logger logger = LoggerUtils.getLogger(TestEventListenerPlugin.class);
    private GlobalExecutionContext globalExecutionContext;

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        eventPublisher.registerHandlerFor(TestRunStarted.class, this::onTestRunStarted);
        eventPublisher.registerHandlerFor(TestSourceRead.class, this::onTestSourceRead);
        eventPublisher.registerHandlerFor(TestCaseStarted.class, this::onTestCaseStarted);
        eventPublisher.registerHandlerFor(TestStepStarted.class, this::onTestStepStarted);
        eventPublisher.registerHandlerFor(EmbedEvent.class, this::onEmbedEvent);
        eventPublisher.registerHandlerFor(WriteEvent.class, this::onWriteEvent);
        eventPublisher.registerHandlerFor(TestStepFinished.class, this::onTestStepFinished);
        eventPublisher.registerHandlerFor(TestCaseFinished.class, this::onTestCaseFinished);
        eventPublisher.registerHandlerFor(TestRunFinished.class, this::onTestRunFinished);
    }

    private void onWriteEvent(WriteEvent writeEvent) {
        logger.debug("onWriteEvent");
    }

    private void onEmbedEvent(EmbedEvent embedEvent) {
    }

    private void onTestRunStarted(TestRunStarted testRunStarted) {
    }

    private void onTestSourceRead(TestSourceRead testSourceRead) {
        Feature feature = FeatureFactory.buildFeature(testSourceRead);
        features.put(testSourceRead.getUri(), feature);
    }

    private void onTestCaseStarted(TestCaseStarted testCaseStarted) {
        globalExecutionContext = SpringContext.getBean(GlobalExecutionContext.class);

        Feature feature = features.get(testCaseStarted.getTestCase().getUri());
        Scenario scenario = ScenarioFactory.buildScenario(testCaseStarted.getTestCase());
        globalExecutionContext.startFeature(feature);
        globalExecutionContext.startScenario(scenario);
    }

    private void onTestStepStarted(TestStepStarted testStep) {
        Step step = StepScenarioFactory.getStep(testStep.getTestStep());
        if (step != null) {
            StepScenario stepScenario = StepScenarioFactory.buildStep(testStep.getTestStep());
            Scenario currentScenario = getCurrentScenario();
            currentScenario.addStep(stepScenario);
            logger.debug("onTestStepStarted - {} {}", stepScenario.getKeyword(), stepScenario.getText());
        }
    }

    private void onTestStepFinished(TestStepFinished testStep) {
        Scenario currentScenario = getCurrentScenario();
        if (currentScenario != null && !currentScenario.getStepList().isEmpty()) {
            currentScenario.getCurrentStep().updateStatus(testStep.getResult().getStatus().name());
            if (testStep.getResult().getStatus().equals(Status.FAILED))
                currentScenario.getCurrentStep().setErrorMessage(testStep.getResult().getError().getMessage());
            logger.debug("onTestStepFinished - {} {} {}",
                    currentScenario.getCurrentStep().getKeyword(),
                    currentScenario.getCurrentStep().getText(),
                    currentScenario.getCurrentStep().getStatus());
        }
    }

    private void onTestCaseFinished(TestCaseFinished testCaseFinished) {
        Scenario currentScenario = getCurrentScenario();
        currentScenario.setStatus(testCaseFinished.getResult().getStatus().name());
        if (testCaseFinished.getResult().getStatus().equals(Status.FAILED))
            currentScenario.setErrorMessage(testCaseFinished.getResult().getError().getMessage());
        JsonExporter.writeList(globalExecutionContext.getCurrentFeature().getScenarios(),
                Path.of(globalExecutionContext.getReportPath(), "scenarios.json"));
        logger.debug("onTestCaseFinished - status {}", getCurrentScenario().getStatus());
        globalExecutionContext.endScenario(currentScenario);
    }

    public void onTestRunFinished(TestRunFinished testRunFinished) {
        logger.debug("onTestRunFinished");
    }

    private Scenario getCurrentScenario() {
        return globalExecutionContext.getCurrentScenario();
    }
}

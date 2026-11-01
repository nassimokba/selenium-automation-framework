package webdriver.bidi;

import context.GlobalExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webdriver.model.ConsoleError;
import webdriver.model.NetworkError;

@Component
public class ScenarioEventRecorder implements BrowserEventObserver {

    @Autowired
    private GlobalExecutionContext context;

    @Override
    public void onNetworkError(NetworkError error) {
        context.getCurrentScenario().addNetworkError(error);
    }

    @Override
    public void onConsoleError(ConsoleError error) {
        context.getCurrentScenario().addConsoleError(error);
    }
}
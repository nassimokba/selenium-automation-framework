package webdriver.bidi;

import org.openqa.selenium.bidi.log.LogLevel;
import org.openqa.selenium.bidi.module.LogInspector;
import org.openqa.selenium.bidi.module.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webdriver.WebdriverManager;
import webdriver.model.ConsoleError;
import webdriver.model.NetworkError;

import java.util.List;

@Component
public class BrowserMonitor {

    @Autowired
    private List<BrowserEventObserver> observers;
    @Autowired
    private WebdriverManager webdriverManager;
    private LogInspector logInspector;
    private Network network;

    public void start() {
        logInspector = webdriverManager.getWebdriver().createLogInspector();
        network = webdriverManager.getWebdriver().createNetwork();
        webdriverManager.getWebdriver().addRequestBodyCollector();

        logInspector.onConsoleEntry(e -> {
            if (LogLevel.ERROR.equals(e.getLevel())) {
                observers.forEach(o -> o.onConsoleError(new ConsoleError("error", e.getText())));
            }
        });
        logInspector.onJavaScriptException(e ->
                observers.forEach(o -> o.onConsoleError(new ConsoleError("js", e.getText()))));

        network.onResponseCompleted(r -> {
            long status = r.getResponseData().getStatus();
            if (status < 400) return;

            var req = r.getRequest();
            String body = null;
            if ("POST".equalsIgnoreCase(req.getMethod())) body = fetchBody(req.getRequestId());
            NetworkError error = new NetworkError(req.getMethod(), req.getUrl(), status, body);
            observers.forEach(o -> o.onNetworkError(error));
        });
    }

    public void stop() {
        if (logInspector != null) logInspector.close();
        if (network != null) network.close();
    }

    private String fetchBody(String requestId) {
        try {
            return webdriverManager.getWebdriver().getRequestBody(requestId).toString();
        } catch (RuntimeException ex) {
            return null; // body not retained; the failed call is still reported
        }
    }
}
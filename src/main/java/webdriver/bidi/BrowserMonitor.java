package webdriver.bidi;

import org.openqa.selenium.bidi.log.LogLevel;
import org.openqa.selenium.bidi.module.LogInspector;
import org.openqa.selenium.bidi.module.Network;
import org.openqa.selenium.bidi.network.RequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webdriver.WebdriverManager;
import webdriver.model.ConsoleError;
import webdriver.model.NetworkError;

import java.time.Instant;
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
                observers.forEach(o -> o.onConsoleError(
                        new ConsoleError(Instant.ofEpochMilli(e.getTimestamp()), "error", e.getText())));
            }
        });
        logInspector.onJavaScriptException(e ->
                observers.forEach(o -> o.onConsoleError(
                        new ConsoleError(Instant.ofEpochMilli(e.getTimestamp()), "js", e.getText()))));

        network.onResponseCompleted(request -> {
            long status = request.getResponseData().getStatus();
            if (status >= 400) {
                RequestData requestData = request.getRequest();
                String body = null;
                if ("POST".equalsIgnoreCase(requestData.getMethod())) body = fetchBody(requestData.getRequestId());
                NetworkError error = new NetworkError(Instant.ofEpochMilli(request.getTimestamp()), requestData.getMethod(), requestData.getUrl(), status, body);
                observers.forEach(o -> o.onNetworkError(error));
            }
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
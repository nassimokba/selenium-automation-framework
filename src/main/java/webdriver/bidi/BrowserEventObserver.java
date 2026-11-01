package webdriver.bidi;

import webdriver.model.ConsoleError;
import webdriver.model.NetworkError;

public interface BrowserEventObserver {
    void onNetworkError(NetworkError error);

    void onConsoleError(ConsoleError error);
}
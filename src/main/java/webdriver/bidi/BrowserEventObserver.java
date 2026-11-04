package webdriver.bidi;

public interface BrowserEventObserver {
    void onNetworkError(NetworkError error);

    void onConsoleError(ConsoleError error);
}
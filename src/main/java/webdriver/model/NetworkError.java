package webdriver.model;

public class NetworkError {
    private final String method;
    private final String url;
    private final long status;
    private final String requestBody;

    public NetworkError(String method, String url, long status, String requestBody) {
        this.method = method;
        this.url = url;
        this.status = status;
        this.requestBody = requestBody;
    }
}
package webdriver.model;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
public class NetworkError {
    private final Instant timestamp;
    private final String method;
    private final String url;
    private final long status;
    private final String requestBody;

    public NetworkError(Instant timestamp, String method, String url, long status, String requestBody) {
        this.timestamp = timestamp;
        this.method = method;
        this.url = url;
        this.status = status;
        this.requestBody = requestBody;
    }
}
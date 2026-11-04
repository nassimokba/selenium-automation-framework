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
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append("Timestamp: ").append(timestamp).append(System.lineSeparator())
                .append("Method: ").append(method).append(System.lineSeparator())
                .append("URL: ").append(url).append(System.lineSeparator())
                .append("Status: ").append(status).append(System.lineSeparator());
        if (requestBody != null) sb.append("Request body: ").append(requestBody).append(System.lineSeparator());

        return sb.append("-".repeat(60)).toString();
    }
}
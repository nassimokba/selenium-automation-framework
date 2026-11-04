package webdriver.bidi;

import lombok.Getter;
import java.time.Instant;

@Getter
public class ConsoleError {
    private final Instant timestamp;
    private final String level;
    private final String text;

    public ConsoleError(Instant timestamp, String level, String text) {
        this.timestamp = timestamp;
        this.level = level;
        this.text = text;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append("Timestamp: ").append(timestamp).append(System.lineSeparator())
                .append("Level: ").append(level).append(System.lineSeparator())
                .append("Message: ").append(text).append(System.lineSeparator());

        return sb.append("-".repeat(60)).toString();
    }
}
package webdriver.model;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
public class ConsoleError {
    private final Instant timestamp;
    private final String level;
    private final String text;

    public ConsoleError(Instant timestamp, String level, String text) {
        this.timestamp = timestamp;
        this.level = level;
        this.text = text;
    }
}
package webdriver.model;

import lombok.Getter;

@Getter
public class ConsoleError {
    private final String level;
    private final String text;

    public ConsoleError(String level, String text) {
        this.level = level;
        this.text = text;
    }
}
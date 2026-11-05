package report;

import lombok.Getter;

@Getter
public enum MimeType {
    IMAGE_PNG("image/png"),
    TEXT_PLAIN("text/plain");

    private final String value;

    MimeType(String value) {
        this.value = value;
    }
}

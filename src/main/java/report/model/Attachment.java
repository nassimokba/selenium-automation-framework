package report.model;

import lombok.Builder;
import lombok.Getter;
import report.MimeType;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@Getter
@Builder
public class Attachment {
    private final String name;
    private final MimeType mimeType;
    private final byte[] content;
    private final Path path;
}
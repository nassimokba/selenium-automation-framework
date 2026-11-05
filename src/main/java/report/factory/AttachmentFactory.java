package report.factory;

import report.MimeType;
import report.model.Attachment;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AttachmentFactory {

    public static Attachment buildAttachment(Path file, MimeType mimeType) {
        try {
            return Attachment.builder()
                    .name(file.getFileName().toString())
                    .mimeType(mimeType)
                    .content(Files.readAllBytes(file))
                    .path(file)
                    .build();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
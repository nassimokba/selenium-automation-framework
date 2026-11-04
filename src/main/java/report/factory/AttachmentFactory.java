package report.factory;

import report.MimeType;
import report.model.Attachment;

import java.nio.file.Path;

public class AttachmentFactory {

    public static Attachment screenshot(byte[] bytes) {
        return Attachment.builder()
                .name("Screenshot")
                .mimeType(MimeType.IMAGE_PNG)
                .content(bytes).build();
    }

    public static Attachment plainText(Path file) {
        return Attachment.builder()
                .name(file.getFileName().toString())
                .mimeType(MimeType.TEXT_PLAIN)
                .path(file)
                .build();
    }
}
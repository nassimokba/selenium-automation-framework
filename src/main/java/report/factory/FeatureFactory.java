package report.factory;

import io.cucumber.plugin.event.TestSourceRead;
import report.model.Feature;

import java.util.ArrayList;
import java.util.Arrays;

public class FeatureFactory {

    public static Feature buildFeature(TestSourceRead event) {
        return Feature.builder()
                .name(extractTicketKey(event.getSource()))
                .description(extractFeatureName(event.getSource()))
                .uri(event.getUri())
                .scenarios(new ArrayList<>())
                .build();
    }

    private static String extractFeatureName(String source) {
        return source.lines()
                .map(String::trim)
                .filter(line -> line.startsWith("Feature:"))
                .findFirst()
                .map(line -> line.substring("Feature:".length()).trim())
                .orElse("Unnamed feature");
    }

    private static String extractTicketKey(String source) {
        return source.lines()
                .map(String::trim)
                .takeWhile(line -> !line.startsWith("Feature:"))
                .filter(line -> line.startsWith("@"))
                .flatMap(line -> Arrays.stream(line.split("\\s+")))
                .filter(tag -> tag.matches("@[A-Z][A-Z0-9]*-\\d+"))
                .map(tag -> tag.substring(1))
                .findFirst()
                .orElse(null).replace("@", "");
    }
}

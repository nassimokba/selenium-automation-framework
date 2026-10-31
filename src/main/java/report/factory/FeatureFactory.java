package report.factory;

import io.cucumber.plugin.event.TestSourceRead;
import report.model.Feature;

import java.util.ArrayList;

public class FeatureFactory {

    public static Feature buildFeature(TestSourceRead event) {
        return Feature.builder()
                .name(extractFeatureName(event.getSource()))
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
}

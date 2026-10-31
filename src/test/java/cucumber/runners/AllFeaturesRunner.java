package cucumber.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"steps", "cucumber"},
        plugin = {"pretty", "cucumber.plugin.TestEventListenerPlugin"}
)
public class AllFeaturesRunner extends AbstractTestNGCucumberTests {
}

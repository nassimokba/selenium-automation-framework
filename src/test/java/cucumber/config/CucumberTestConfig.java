package cucumber.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@ContextConfiguration(classes = CucumberTestConfig.class)
@ComponentScan(basePackages = {"webdriver", "context", "report", "pages"})
@PropertySource("classpath:config.properties")
public class CucumberTestConfig {
}
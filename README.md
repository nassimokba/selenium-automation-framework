# selenium-automation-framework

> 🚧 Work in progress — actively being built and documented.

UI test automation framework built with Selenium, Spring and Cucumber.

**Application under test:** [Restful Booker](https://automationintesting.online)

## Tech stack

- [Selenium WebDriver](https://www.google.com/url?sa=t&source=web&rct=j&opi=89978449&url=https://www.selenium.dev/&ved=2ahUKEwjr_pTnvoqXAxWhzwIHHafTMG8QFnoECCcQAQ&usg=AOvVaw38IyEsg2ARkRX6lSh_KzqM)
- [Spring Framework](https://spring.io/projects/spring-framework)
- [Cucumber](https://cucumber.io/)
- [TestNG](https://testng.org/)
- [Maven](https://maven.apache.org/)
- [Extent Reports](https://extentreports.com/)
- [GitHub Actions](https://github.com/features/actions)

## Running locally

**Prerequisites:** JDK, Maven, Allure CLI, Firefox, Chrome or Edge (the browser is set in `config.properties`).

```bash
# Run all features
mvn test -Dtest=AllFeaturesRunner
mvn test -Dtest=BookingFeatureRunner
mvn test -Dtest=LoginFeatureAllRunner

# Run a single feature file 
mvn test -Dtest=AllFeaturesRunner -Dcucumber.features=src/test/resources/features/login.feature
mvn test -Dtest=AllFeaturesRunner -Dcucumber.features=src/test/resources/features/create_booking.feature

# Run scenarios by tag
mvn test -Dtest=AllFeaturesRunner -Dcucumber.filter.tags="@login"
mvn test -Dtest=AllFeaturesRunner -Dcucumber.filter.tags="@create_booking"
```
**ExtentReports:**
Each run writes its report to a timestamped folder under `output/`.

**Allure:**
Each run writes its report in `output/allure-report` folder
```bash
# Run to generate Allure report
allure generate output/<timestamp> --clean -o output/allure-report
```
```bash
# Run to open Allure generated report
allure open output/allure-report   
```

## Author

**Nassim Oufighou Okba** — [LinkedIn](https://linkedin.com/in/nassim-oufighou)

## License

MIT © Nassim Oufighou Okba
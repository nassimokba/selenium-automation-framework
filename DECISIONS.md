## Architecture decisions

### Webdriver

* Use a singleton in `WebdriverManager` just to make the whole flow work. Will be replaced by Spring-managed bean when
  DI is introduced — Spring components
  are singletons by default. _[Sept. 29, 2026]_
* Introduce a config management with `WebdriverConfig`.
* Inject `WebdriverConfig` into `WebdriverManager` via constructor injection, and have it pass the config to `WebdriverFactory` when creating the driver.
* Replace the individual parameters of `WebdriverFactory` with a single `WebdriverConfig` parameter.
### Reporting

* Keep the Cucumber plugin unaware of reporting. The plugin only tells the global execution context what happened. A
  `ReportObserver` picks up finished scenarios and hands them to an `IReporter`.
* Dropped TestNG listeners because they duplicate the lifecycle the plugin already covers. _[Oct. 31, 2026]_
* Drop `createFeature` from `IReporter`. Scenarios are the unit reported. `Scenario` carries its `featureName`, and
  `ExtentReporter` groups scenarios under their feature internally, so callers don't manage feature state.
  _[Oct. 31, 2026]_
* No separate config class for Extent. The setup stays inside `ExtentReporter` until a second reporter or a
  configurable path makes extracting it worthwhile. _[Oct. 31, 2026]_
* Signal the start of the execution from `onTestCaseStarted`, not `onTestRunStarted`. The Spring context isn't
  available at run start, so the plugin calls `startTestExecution()` on every scenario, and the global context only
  acts on the first call, when the start time isn't set yet: it creates the timestamped output folder and notifies
  the observers. _[Oct. 31, 2026]_
* Attachments (screenshots and the console and network logs) are `Attachment` objects on `Scenario`, each with a name,
  a `MimeType` and its content as `byte[]`. Reporters read the content from memory instead of from files on disk, so
  each one can embed or copy it in whatever way it needs. _[Oct. 31, 2026]_
* Each run writes to its own timestamped folder under `output`, created once at the start of the execution.
  _[Oct. 31, 2026]_

### Allure
* Use the `AllureLifecycle` API directly instead of the `allure-cucumber` plugin. The official plugin hooks into
  Cucumber on its own, which would bypass the observer and the global execution context, so Allure would sit outside
  the architecture. Writing an `IReporter` implementation keeps both reporters fed from the same `Scenario` objects.
  _[Nov. 5, 2026]_
* Build each Allure result after the scenario ends, from the finished `Scenario`. Allure needs start and end times
  and a status for every result, so the domain objects carry that data instead of the reporter measuring it live
  (as Extent does). _[Nov. 5, 2026]_
* Build `fullName` and `historyId` from the feature name plus the scenario name, not from the scenario tag. Several
  outlines share the same tag (`@RB-102`), and a shared `historyId` makes Allure treat different scenarios as
  retries of one test. _[Nov. 5, 2026]_
* Download the Allure CLI with Maven (`maven-dependency-plugin`) instead of requiring a local install, so the same
  setup works on a developer machine and in CI. The HTML is regenerated after every scenario, so progress is
  visible while the suite runs. _[Nov. 5, 2026]_
## Architecture decisions

### Webdriver

* Use a singleton in `WebdriverManager` just to make the whole flow work. Will be replaced by Spring-managed bean when
  DI is introduced — Spring components
  are singletons by default. _[Sept. 29, 2026]_

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
* One single report per run, with no folder hierarchy per feature or scenario. Extent already groups scenarios by
  feature, and a split would mean several reports plus the links between them. _[Oct. 31, 2026]_
* Screenshots are a `byte[]` on `Scenario`, taken only on failure and embedded as base64 in the HTML. No png files to
  manage, and other reporters can convert the same bytes to whatever format they need. _[Oct. 31, 2026]_
* Each run writes to its own timestamped folder under `output`, created once at the start of the execution.
  _[Oct. 31, 2026]_
* Custom report CSS lives in a single `customCss()` method inside `ExtentReporter`, so there's one place to look when
  the report needs visual tweaks. _[Oct. 31, 2026]_
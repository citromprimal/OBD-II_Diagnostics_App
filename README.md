# Car Diagnostics App – Unit Testing with Test Doubles

Android application for vehicle diagnostics using OBD-II and a Bluetooth adapter.

This repository was used as part of a semester project for the Software Systems Quality course. The project focuses on the use of test doubles for unit testing components with external dependencies.

## Semester Project

**Topic:** Test Doubles in Unit Testing

The goal was to separate selected application logic from external dependencies and enable automated testing without requiring:

- a real vehicle,
- an OBD-II adapter,
- a Bluetooth connection,
- a connection to Supabase.

## Technologies Used

- Kotlin
- Android
- JUnit 4
- MockK
- kotlinx-coroutines-test
- JaCoCo
- Supabase
- PostgreSQL

## Tested Areas

The unit tests focus mainly on:

- OBD-II diagnostic commands,
- VIN reading,
- polling control,
- DTC scan processing,
- comparison of diagnostic history,
- saving DTC scans,
- saving vehicle and VIN information,
- parsing OBD-II PID values,
- edge cases and error scenarios.

## Test Doubles

For OBD-II communication, a mock of the following interface is used:

`ObdManager`

For database-related logic, a mock of the following interface is used:

`DtcDataSource`

For vehicle and VIN-related database logic, a mock of the following interface is used:

`VehicleDataSource`

This makes it possible to test application logic without real external systems.

## Running the Tests

On Windows:

```powershell
.\gradlew.bat testDebugUnitTest
```

The HTML test report is generated at:

```text
app/build/reports/tests/testDebugUnitTest/index.html
```

## Code Coverage

The coverage report is generated using JaCoCo:

```powershell
.\gradlew.bat jacocoTestReport
```

The HTML coverage report is generated at:

```text
app/build/reports/jacoco/jacocoTestReport/html/index.html
```

The final project version contains:

- 34 unit tests,
- 100% test success rate,
- 68% instruction coverage,
- 60% branch coverage,
- 200 / 264 covered lines,
- 19 / 22 covered methods,
- 4 / 4 covered classes.

The database-related package reached 100% instruction and branch coverage within the selected tested scope.

## Git Tags

The repository contains two important project states.

### `before-mocks`

Original project state before introducing test doubles and unit tests.

### `after-mocks`

Final project state after refactoring, introducing test doubles, and adding unit tests.

The two versions can be compared using:

```bash
git diff before-mocks after-mocks
```

## Local Configuration

The `local.properties` file is not included in the repository.

To run the application locally, the required local values must be configured, for example:

```properties
SUPABASE_URL=YOUR_SUPABASE_URL
SUPABASE_ANON_KEY=YOUR_SUPABASE_ANON_KEY
```

These values are not required to run the unit tests based on mock objects.

## Author

Dávid Bohák  

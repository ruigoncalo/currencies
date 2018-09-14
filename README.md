## Build and run

1. Create a `local.properties` with `sdk.dir=<path_to_android_sdk>`and add to the root of the project

2. Run `./gradlew app:assemble` to generate the apk

### Tests

The `domain` module has unit tests (using the [Spek framework](https://spekframework.org/))

Run `./gradlew domain:test`

Run `./gradlew domain:test --tests com.ruigoncalo...<ClassNameSpec.kt>` to run a specific test file

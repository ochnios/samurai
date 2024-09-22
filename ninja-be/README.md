# DocsNinja backend application

## Formatting

Project is formatted with
[spotless](https://github.com/diffplug/spotless/tree/main/plugin-gradle#java)
and 
[palantir-java-format](https://github.com/palantir/palantir-java-format)
which provides more sophisticated way of formatting than 
[google-java-format](https://github.com/google/google-java-format/blob/master/README.md#intellij-jre-config)
which was used initially (especially in case of lambda expressions).

Run `./gradlew spotlessApply` to apply formatting
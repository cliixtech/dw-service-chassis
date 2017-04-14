# About DW-Chassis

This is a template project for REST Services using dropwizard.

It includes, datanucleus jdo, flywaydb and metrics using Abacus.

# Building

Run ``./gradlew check`` to check code formatting and run all the tests.

In case of any **format violations**, run ``./gradlew spotlessApply`` to format the code accordingly.

To avoid getting this violation messages, you can configure eclipse to use the same formatting
rules - just point your project's java formatter use cliix.eclipseformat.xml as formatting spec
file.

## Running tests continuously

To run all tests on every change you make, use ``./gradlew -t test`` 

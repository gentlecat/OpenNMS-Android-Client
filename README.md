# OpenNMS Android App

[Google Summer of Code 2013 project](https://www.google-melange.com/gsoc/project/google/gsoc2013/tsukanov/42001).


## Deploying application

If you want to deploy APK to the connected device:

    mvn install android:deploy

More information is available at https://code.google.com/p/maven-android-plugin/wiki/GettingStarted.


## Running tests

    mvn spoon:run

The execution result will be placed in the `target/spoon-output/` folder.
If you want to specify a test class to run, add `-Dspoon.test.class=fully.qualified.ClassName`.
If you only want to run a single test in that class, add `-Dspoon.test.method=testAllTheThings`.

More information is available at http://square.github.io/spoon/.

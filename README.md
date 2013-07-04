# OpenNMS Android Client

This is a Google Summer of Code 2013 [project](https://www.google-melange.com/gsoc/project/google/gsoc2013/tsukanov/42001).


## Deploying application

If you want to deploy APK to all connected devices:

    mvn clean package android:deploy

*More information about using Maven during Android development is available at
https://code.google.com/p/maven-android-plugin/wiki/GettingStarted.*

Since tests might not work use:

    mvn clean package android:deploy -Dmaven.test.skip=true


![](http://i.imgur.com/bWnpIQT.png)

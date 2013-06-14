# OpenNMS Android Application

This is a Google Summer of Code 2013 [project](https://www.google-melange.com/gsoc/project/google/gsoc2013/tsukanov/42001).


## Deploying application

You will need to set up [Maven Android SDK Deployer](https://github.com/mosabua/maven-android-sdk-deployer) to provide required dependencies.

After that, if you want to deploy APK to the connected device:

    mvn clean package android:deploy

*More information about using Maven during Android development is available at
https://code.google.com/p/maven-android-plugin/wiki/GettingStarted.*
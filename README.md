##### OpenNMS Android Client Sample
The application is a proof of concept for OpenNMS Android client project proposed to Google Summer of Code 2012.

###Features:

* displays nodes existing on demo.opennms.org server

###Technical aspects:

* the application uses Apache's HTTPClient to remotely connect to the rest service provided by demo.opennms.org:8980/rest/nodes
* the xml response is parsed and displayed in an Android ListView

* the nodes are displayed with their id, label and type in the Nodes Tab

![Nodes](http://i.imgur.com/FTFij.png)

* the About tab is dummy 

![About](http://i.imgur.com/xORBx.png)

###Installation

* install Android SDK 2.3.3 or newer
* install ADT
* import project to Eclipse
* the .apk file to install the application on device can be found at : [OpenNMS Android](http://ge.tt/4HdCvlF/v/0)

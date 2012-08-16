# OpenNMS Android Client Sample
The application is a proof of concept for [OpenNMS](http://www.opennms.org/) Android client project proposed to Google Summer of Code 2012.

######Features:

* displays nodes existing on [demo.opennms.org](http://demo.opennms.org/opennms/login.jsp) server

######Technical aspects:

* the application uses [Apache's HTTPClient](http://developer.android.com/reference/org/apache/http/client/HttpClient.html) to remotely connect to the rest service provided by [demo.opennms.org/opennms/rest/nodes](http://demo.opennms.org/opennms/rest/nodes)
* the process of fetching the nodes from the web service is performed within a Service 
* the xml response is parsed and displayed in an Android ListView

* the nodes are displayed with their id, label and type in the Nodes Tab

![Nodes](http://i.imgur.com/FTFij.png)

* the About tab is dummy 

![About](http://i.imgur.com/xORBx.png)

######Installation

* install Android SDK 2.3.3 or newer
* install ADT
* import project to Eclipse

######Execution

* from Eclipse using an emulator
* the .apk file to install the application on device can be found at : [Demo OpenNMS Android Client](http://ge.tt/8IClr4M/v/0)

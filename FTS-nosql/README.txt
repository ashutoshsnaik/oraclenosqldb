README

included:
All source files (.java,.properties,.html,.css) - Eclipse - Maven Project
War File - in case you want to get started immediately.
this README
Maven - pom.xml
Sample Dicom Images
Initial Scripts for setup

Dicom Demo

The use cases that this application performs are:
1. Maintain Dicom Metadata adn Dicom images in KV
2.  Index Dicom Metadata as Full Text Index in KV (the attributes have to get replicated into the registered ES)
3.  Parse and Read Dicom Images stored in a configured location and load Metadata in Dicom Table
4.  Use basic search/Query capabilities of ES and for each document provide ability to fetch corresponding Dicom Image from KV

Pre-Requisites:
1. latest KV installed - version 4.00
2. Elastic Search - version 2.0 and above
3. Tomcat V8.0 and above to deploy the Web Application.

Getting started:
To build the project:
1. Unzip DicomDemo.zip.
2. Copy the DicomRest folder to a desired location.
3. Import the DicomRest into Eclipse NOTE: you should have Maven installed and Configured in Eclipse and in your system
4. Under WEB-INF, there are 2 files DicomParser.properties and KVES.properties.
5. Ensure all your KV related information is specified in DicomParser.properties.eg is below:
======================================================================
host=localhost:5000
store=mystore
tablename=dicom
# Directory from where you would like to have your Dicom Images picked up.
directory=/scratch/vsettipa/DicomImages
======================================================================
6. Ensure all your Elastic Search related information is specified in KVES.properties. eg is below:
======================================================================
host=localhost
transportPort=9300
storename=mystore
tablename=dicom
indexname=dicomindex
======================================================================
7. The eg above is where ES and KV are running on the same machine. If ES is running on a different machine, then sure appropriate ES configuration is done in elasticsearch.yml available under <Elasticsearch Home>/config/elasticsearch.yml
8. Run Maven - Build Project - Clean Install. This will generate the DicomRest-0.0.1-SNAPSHOT.war file. Its desirable to rename this to DicomRest.war.
9. Deploy the application in Tomcat.
10. To run the application - http://<server-name>:<server-port>/DicomRest
11. There are 2 users provided:
	admin/admin - this user will have the ability to load Dicom Images and also search for Patients.
	dicom/dicom - this is the physician's role and will have the ability to search for Patients.

To run the application without building:
1. A pre-build war file is bundled in the provided zip file.
2. Follow steps 9 to 11 above.
3. This war ensures that the standard configuration's for KV and ES. In case you would like to change please refer to points 4, 5 and 6 above.

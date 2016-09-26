Runtime Introspection Module on class loader issue
==================================================

This module generates a custom OSGI classes which is used to print out Class Loading Errors.

User can set the offending class as a java VM argument (-D arg) and start the OSGI environment.
The modified classes will print the reason of the class loading error due to bundle conflict.

**Note** Please make sure you do this on offline pack, as this tool modifies the CARBON_HOME. The modifications are difficult to revert.

Build
=====

    mvn package
This will build the classes from the source

Setup
=====

    setup.sh <CARBON_HOME>

This will modify the existing CARBON_HOME so that it will print the class loading error

Run
===

Go to your \<CARBON_HOME\>, start the server with usual wso2server.sh. Add java option "-Dwso2.osgi.class.loader.intro.name=\<fully qualified class name\>"


   ./wso2server.sh -Dwso2.osgi.class.loader.intro.name=org.wso2.carbon.appmgt.impl.AppRepository


This will print Error like the following

    [ERROR] Loading Class [AAA] failed. There is a conflict in package: reference:file:../plugins/BBB.jar Exporting BBB1:VVV
    reference:file:../plugins/CCC.jar Exporting CCC1:VVV
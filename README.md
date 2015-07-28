Carbon OSGI Verifier
====================

Building
========

    mvn install
    mvn package appassembler:assemble


Running
=======

Either set your environment variable CARBON_HOME to correct unzipped directory or provide it
as a program argument

e.g.
sh target/appassembler/bin/scan -d {carbon_home}

Command Line Arguments

* -d carbon_home Carbon Home to analyze
* -pv   Processes the package versions(defualt false). The exported package versions are matched if this option is set.



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
sh target/appassembler/bin/scan <carbon_home>


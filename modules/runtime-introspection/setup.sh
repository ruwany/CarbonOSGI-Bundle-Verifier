#!/bin/sh

if [ "$#" -ne 1 ] || ! [ -d "$1" ];
then
    echo "Please provide the carbon home as the argument"
    echo "usage : setup.sh <CARBON_HOME> "
else

echo Using Carbon Home $1

echo Modifying the OSGI environment...


PLUGIN_DIR=$1/repository/components/plugins
unzip -qo $PLUGIN_DIR/org.eclipse.osgi_3.9.1.v20130814-1242.jar -d $PLUGIN_DIR/org.eclipse.osgi_3.9.1.v20130814-1242
cp $PLUGIN_DIR/org.eclipse.osgi_3.9.1.v20130814-1242.jar $PLUGIN_DIR/..
rm $PLUGIN_DIR/org.eclipse.osgi_3.9.1.v20130814-1242.jar
cd ./osgi-classes/target/classes
find . -type f -name "*.class" | cpio -pdv $PLUGIN_DIR/org.eclipse.osgi_3.9.1.v20130814-1242 \;
echo Modifying the OSGI environment Done

echo You can run the normal ws02server.sh with -Dwso2.osgi.class.loader.intro.name=xx option now.

fi


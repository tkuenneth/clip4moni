#!/bin/sh

JAVA_HOME=`/usr/libexec/java_home`
BASEDIR=`pwd`

VERSION=`sed -n -e 's/.*VERSION = \"\(.*\)\".*/\1/p' < $BASEDIR/src/main/classes/com/thomaskuenneth/clip4moni/Clip4Moni.java`

echo "JAVA_HOME: $JAVA_HOME"
echo "BASEDIR: $BASEDIR"
echo "Version: $VERSION"

rm -rf "$BASEDIR/Clip4Moni.app"

$JAVA_HOME/bin/jpackage --name Clip4Moni --icon $BASEDIR/artwork/Clip4Moni.icns --app-version $VERSION --type app-image --module-path $BASEDIR/build/modules -m main/com.thomaskuenneth.clip4moni.Clip4Moni

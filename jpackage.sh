#!/bin/sh

JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-14.jdk/Contents/Home
BASEDIR=/Users/thomas/Entwicklung/Bitbucket/clip4moni

VERSION=`sed -n -e 's/.*VERSION = \"\(.*\)\".*/\1/p' < $BASEDIR/src/main/classes/com/thomaskuenneth/clip4moni/Clip4Moni.java`

$JAVA_HOME/bin/jpackage --name Clip4Moni --icon $BASEDIR/artwork/Clip4Moni.icns --app-version $VERSION --type app-image --module-path $BASEDIR/build/modules -m main/com.thomaskuenneth.clip4moni.Clip4Moni

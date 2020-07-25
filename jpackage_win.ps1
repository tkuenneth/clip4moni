#!/bin/sh

$JAVA_HOME = "C:\Program Files\Java\jdk-14.0.2"
$BASEDIR = "C:\Users\tkuen\Entwicklung\Bitbucket\clip4moni"
$VERSION = "1.3.3" # `sed -n -e 's/.*VERSION = \"\(.*\)\".*/\1/p' < $BASEDIR/src/com/thomaskuenneth/Clip4Moni/Main.java`

Write-Output "JAVA_HOME: $JAVA_HOME"
Write-Output "BASEDIR: $BASEDIR"
Write-Output "Version: $VERSION"

#rmdir -r "$BASEDIR\Clip4Moni"

$COMMAND = "$JAVA_HOME\bin\jpackage.exe"
$ARGUMENTS = "--win-menu --win-menu-group `"Thomas Kuenneth`" --vendor `"Thomas Kuenneth`" --name Clip4Moni --icon $BASEDIR\artwork\Clip4Moni.ico --type msi --app-version $VERSION --module-path $BASEDIR\dist\main.jar;`"C:\Program Files\Java\javafx-jmods-13.0.2`" -m main/com.thomaskuenneth.clip4moni.Clip4Moni"

Write-Output $ARGUMENTS

Start-Process -RedirectStandardOutput stdout.txt -RedirectStandardError stderr.txt -FilePath $COMMAND -ArgumentList $ARGUMENTS -Wait
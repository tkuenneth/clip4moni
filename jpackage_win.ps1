$java_home = "C:\Program Files\Java\jdk-14.0.2"
$base_dir = "C:\Users\tkuen\Entwicklung\Bitbucket\clip4moni"
$version = "???"

$source = Get-Content -Path $base_dir\src\main\classes\com\thomaskuenneth\clip4moni\Clip4Moni.java
foreach($line in $source) {
    if ($line -match "version = `"(.+)`"") {
        $version = $matches[1]
        break
    }
}

Write-Output "java_home: $java_home"
Write-Output "base_dir: $base_dir"
Write-Output "version: $version"

$command = "$java_home\bin\jpackage.exe"
$arguments = "--win-menu --win-menu-group `"Thomas Kuenneth`" --vendor `"Thomas Kuenneth`" --name Clip4Moni --icon $base_dir\artwork\Clip4Moni.ico --type msi --app-version $version --module-path $base_dir\dist\main.jar;`"C:\Program Files\Java\javafx-jmods-13.0.2`" -m main/com.thomaskuenneth.clip4moni.Clip4Moni"

Write-Output $arguments
Start-Process -RedirectStandardOutput stdout.txt -RedirectStandardError stderr.txt -FilePath $command -ArgumentList $arguments -Wait
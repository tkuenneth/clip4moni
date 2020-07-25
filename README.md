# Welcome to Clip4Moni #

Clip4Moni is a small utility that manages text snippets. Text snippets are frequently used pieces of text. 
Instead of countlessly re-typing, for example, a greeting message, legal information or your address, 
just choose it from a small menu. The corresponding text is put on the system clipboard. Then use 
PASTE in the destination application to have the text or phrase inserted. Clip4Moni is written in Java. 
It requires Java SE 6 update 10 or later to run.

Clip4Moni is released under the terms of the GNU GENERAL PUBLIC LICENSE Version 2.

If you have built a native app bundle on macOS, you can edit `info.plist` to include
```xml
<key>LSUIElement</key>
<string>1</string>
```
This will hide the Dock icon.
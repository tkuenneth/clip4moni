/*
 * Helper.java
 *
 * This file is part of Clip4Moni.
 *
 * Copyright (C) 2008 - 2022  Thomas Kuenneth
 *
 * Clip4Moni is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.thomaskuenneth.clip4moni;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class Helper {

    private static final String TAG = Helper.class.getName();
    private static final Logger LOGGER = Logger.getLogger(TAG);

    private static final String OS_NAME = System.getProperty("os.name");
    private static final String HOME_DIR = System.getProperty("user.home");
    private static final Toolkit DEFAULT_TOOLKIT = Toolkit.getDefaultToolkit();
    private static final Dimension SCREEN_SIZE = DEFAULT_TOOLKIT.getScreenSize();

    private static final String SNIPPETS_DIR = "SnippetsDir";
    private static final String LOOK_AND_FEEL = "LookAndFeel";
    private static final String MACOSX_WORKAROUND = "MacOSXWorkaround";

    public static final String LISTNAME = "Clip4Moni.list";
    public static final int SCREEN_RESOLUTION = DEFAULT_TOOLKIT.getScreenResolution();
    public static final String PROGNAME = Messages.getString("PROGNAME");

    public static boolean isMacOSX() {
        return OS_NAME.toLowerCase().startsWith("mac os x");
    }

    public static String getLibraryDir() {
        String path;
        if (isMacOSX()) {
            path = HOME_DIR + File.separator + "Library" + File.separator
                    + "Preferences";
        } else {
            path = HOME_DIR;
        }
        return path;
    }

    public static Dimension getScreenSize() {
        return new Dimension(SCREEN_SIZE);
    }

    public static Clipboard getSystemClipboard() {
        return DEFAULT_TOOLKIT.getSystemClipboard();
    }

    public static File getSnippetsDir() {
        Preferences prefs = getPrefs();
        String path = prefs.get(SNIPPETS_DIR, getLibraryDir()
                + File.separator + PROGNAME);
        return getFileFromPath(path);
    }

    public static void setSnippetsDir(String dir) {
        Preferences prefs = getPrefs();
        prefs.put(SNIPPETS_DIR, dir);
    }

    public static boolean isMacOSXWorkaroundActive() {
        Preferences prefs = getPrefs();
        return prefs.getBoolean(MACOSX_WORKAROUND, true);
    }

    public static void setMacOSXWorkaroundActive(boolean active) {
        Preferences prefs = getPrefs();
        prefs.putBoolean(MACOSX_WORKAROUND, active);
    }

    public static void restoreLookAndFeel() {
        Preferences prefs = getPrefs();
        String lookAndFeelClassName = prefs.get(LOOK_AND_FEEL, UIManager.getSystemLookAndFeelClassName());
        try {
            UIManager.setLookAndFeel(lookAndFeelClassName);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException tr) {
            LOGGER.log(Level.SEVERE, "restoreLookAndFeel", tr);
        }
    }

    public static void storeLookAndFeel(String lookAndFeelClassName) {
        Preferences prefs = getPrefs();
        prefs.put(LOOK_AND_FEEL, lookAndFeelClassName);
        try {
            UIManager.setLookAndFeel(lookAndFeelClassName);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException tr) {
            LOGGER.log(Level.SEVERE, "storeLookAndFeel", tr);
        }
    }

    public static File getFileList() {
        return new File(getSnippetsDir(), LISTNAME);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File getFileFromPath(String path) {
        File f = new File(path);
        f.mkdirs();
        return f;
    }

    private static Preferences getPrefs() {
        Preferences prefs = Preferences.userRoot();
        return prefs.node(Clip4Moni.class.getName());
    }
}

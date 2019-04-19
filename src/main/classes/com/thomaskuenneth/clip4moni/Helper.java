/*
 * Helper.java
 * 
 * This file is part of Clip4Moni.
 * 
 * Copyright (C) 2008 - 2018  Thomas Kuenneth
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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * This class contains helper methods, which for example provide default
 * directory paths.
 *
 * @author Thomas Kuenneth
 */
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

    /**
     * Checks if the machine is running a version of Mac OS X
     *
     * @return if the machine is running a version of Mac OS X
     */
    public static boolean isMacOSX() {
        return OS_NAME.toLowerCase().startsWith("mac os x");
    }

    /**
     * Returns the system-dependent directory for storing user files.
     *
     * @return directory for storing user files
     */
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

    /**
     * Returns the screen size in pixels
     *
     * @return the screen size
     */
    public static Dimension getScreenSize() {
        return new Dimension(SCREEN_SIZE);
    }

    /**
     * Returns the system clipboard
     *
     * @return the system clipboard
     */
    public static Clipboard getSystemClipboard() {
        return DEFAULT_TOOLKIT.getSystemClipboard();
    }

    /**
     * Gets the base directory for snippets. Creates directories if neccessary.
     *
     * @return base directory for snippets
     */
    public static File getSnippetsDir() {
        Preferences prefs = getPrefs();
        String path = prefs.get(SNIPPETS_DIR, getLibraryDir()
                + File.separator + PROGNAME);
        return getFileFromPath(path);
    }

    /**
     * Stores the base directory for snippets in the preferences.
     *
     * @param dir base directory for snippets
     */
    public static void setSnippetsDir(String dir) {
        Preferences prefs = getPrefs();
        prefs.put(SNIPPETS_DIR, dir);
    }

    /**
     * Returns true if the Mac OS X workaround is active.
     *
     * @return true if the Mac OS X workaround is active
     */
    public static boolean isMacOSXWorkaroundActive() {
        Preferences prefs = getPrefs();
        return prefs.getBoolean(MACOSX_WORKAROUND, true);
    }

    /**
     * Configures if the Mac OS X workaround is active.
     *
     * @param active
     */
    public static void setMacOSXWorkaroundActive(boolean active) {
        Preferences prefs = getPrefs();
        prefs.putBoolean(MACOSX_WORKAROUND, active);
    }

    /**
     * Restores the look and feel from preferences.
     */
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

    /**
     * Sets the look and feel and stores its class name in the preferences.
     *
     * @param lookAndFeelClassName look and feel class name
     */
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

    /**
     * Returns a File object representing the the file list.
     *
     * @return a File object representing the the file list
     */
    public static File getFileList() {
        return new File(getSnippetsDir(), LISTNAME);
    }

    /**
     * Returns a File object representing a given path. Calls
     * <code>mkdirs()</code> to make sure that subdirectories exist
     *
     * @param path a path
     * @return a File object
     */
    public static File getFileFromPath(String path) {
        File f = new File(path);
        f.mkdirs();
        return f;
    }

    /**
     * Get the base for Clip4Moni-related user preferences.
     *
     * @return the base for Clip4Moni-related user preferences
     */
    private static Preferences getPrefs() {
        Preferences prefs = Preferences.userRoot();
        return prefs.node(Clip4Moni.class.getName());
    }
}
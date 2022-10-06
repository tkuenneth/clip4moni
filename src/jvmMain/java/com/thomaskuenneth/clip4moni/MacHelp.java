/*
 * MacHelp.java
 *
 * This file is part of Clip4Moni.
 *
 * Copyright (C) 2013 - 2022  Thomas Kuenneth
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

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a Mac-specific helper class. It executes AppleScript scripts.
 *
 * @author Thomas Kuenneth
 */
public class MacHelp {

    private static final String TAG = MacHelp.class.getName();
    private static final Logger LOGGER = Logger.getLogger(TAG);

    /**
     * Activates an app.
     *
     * @param name app to activate
     */
    public static void activateApp(String name) {
        if (name != null) {
            String script = "tell application \"" + name + "\"\nactivate\nend tell";
            run(script);
        }
    }

    /**
     * Gets the name of the frontmost app.
     *
     * @return name of the frontmost app or {@code null}
     */
    public static String getFrontmostApp() {
        String script = "tell application \"System Events\"\nitem 1 of (get name of processes whose frontmost is true)\nend tell";
        return run(script);
    }

    private static String run(String script) {
        StringBuilder sbIS = new StringBuilder();
        StringBuilder sbES = new StringBuilder();
        ProcessBuilder pb = new ProcessBuilder("/usr/bin/osascript", "-e", script);
        int result = start(pb, sbIS, sbES);
        if (result == 0) {
            return sbIS.toString().trim();
        } else {
            LOGGER.log(Level.SEVERE, sbES.toString());
            return null;
        }
    }

    /**
     * Starts a process.
     *
     * @param pb   ProcessBuilder instance
     * @param sbIS StringBuilder to recieve the input stream
     * @param sbES StringBuilder to recieve the error stream
     * @return return value of the process
     */
    private static int start(ProcessBuilder pb, StringBuilder sbIS, StringBuilder sbES) {
        int exit = 1;
        try {
            Process p = pb.start();
            InputStream is = p.getInputStream();
            int isData;
            InputStream es = p.getErrorStream();
            int esData;
            while (true) {
                isData = is.read();
                esData = es.read();
                if (isData != -1) {
                    sbIS.append((char) isData);
                }
                if (esData != -1) {
                    sbES.append((char) esData);
                }
                if ((isData == -1) && (esData == -1)) {
                    try {
                        exit = p.exitValue();
                        break;
                    } catch (IllegalThreadStateException e) {
                        // no logging needed... just waiting
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "exception while reading", e);
        }
        return exit;
    }
}

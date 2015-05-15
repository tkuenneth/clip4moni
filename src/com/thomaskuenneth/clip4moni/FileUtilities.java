package com.thomaskuenneth.clip4moni;

/*
 * FileUtilities.java - This file is part of TKNotesAndTasks.
 * Copyright (C) 2013 - 2014  Thomas Kuenneth
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class contains utility methods that deal with files.
 *
 * @author Thomas Kuenneth
 *
 */
public class FileUtilities {

    private static final String CLASS_NAME = FileUtilities.class.getName();
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Reads a file and returns its lines as a list of strings.
     *
     * @param f the file to read
     * @return a list of strings
     */
    public static List<String> getLines(File f) {
        ArrayList<String> l = new ArrayList<String>();
        FileReader r = null;
        BufferedReader br = null;
        try {
            r = new FileReader(f);
            br = new BufferedReader(r);
            String line;
            while ((line = br.readLine()) != null) {
                l.add(line);
            }
        } catch (IOException e) {
            LOGGER.throwing(CLASS_NAME, "getLines", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOGGER.throwing(CLASS_NAME, "getLines", e);
                }
            }
            if (r != null) {
                try {
                    r.close();
                } catch (IOException e) {
                    LOGGER.throwing(CLASS_NAME, "getLines", e);
                }
            }
        }
        return l;
    }

    public static String getResourceAsString(Class c, String url) {
        StringBuilder sb = new StringBuilder();
        InputStream is = null;
        try {
            is = c.getResourceAsStream(url);
            int i;
            while ((i = is.read()) != -1) {
                sb.append((char) i);
            }
        } catch (IOException e) {
            LOGGER.throwing(CLASS_NAME, "getResourceAsString", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.throwing(CLASS_NAME, "getResourceAsString", e);
                }
            }
        }
        return sb.toString();
    }
}

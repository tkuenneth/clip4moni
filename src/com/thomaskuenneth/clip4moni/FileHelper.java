/*
 * FileHelper.java
 * 
 * This file is part of Clip4Moni.
 * 
 * Copyright (C) 2013  Thomas Kuenneth
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * This class provides static helper methods to read and write files.
 *
 * @author Thomas Kuenneth
 */
public class FileHelper {

    private static final String TAG = FileHelper.class.getName();
    private static final Logger LOGGER = Logger.getLogger(TAG);
    private static final String UTF8 = "UTF-8";
    private static final byte[] MAGIC = {0x2, 0x9, 0x0, 0x8};

    /**
     * Saves a string to a file using UTF-8. The first four bytes form a magic
     * number: 0x2, 0x9, 0x0, 0x8
     *
     * @param f the file
     * @param text the string
     * @return true if the file could be written successfully, false otherwise
     */
    public static boolean saveFile(File f, String text) {
        boolean result = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            byte[] buf = text.getBytes(UTF8);
            fos.write(MAGIC);
            fos.write(buf);
            result = true;
        } catch (IOException e) {
            LOGGER.throwing(TAG, "saveFile", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LOGGER.throwing(TAG, "saveFile", e);
                }
            }
        }
        return result;
    }

    /**
     * Reads a file. It is assumed that the content is encoded using UTF-8 if
     * the first four bytes are equal to 0x2, 0x9, 0x0, 0x8.
     *
     * @param f the file
     * @return the contents of the file as a string
     */
    public static String loadFile(File f) {
        int len = (int) f.length();
        byte[] buf = new byte[len];
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(f);
            fin.read(buf);
        } catch (IOException e) {
            LOGGER.throwing(TAG, "loadFile", e);
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    LOGGER.throwing(TAG, "loadFile", e);
                }
            }
        }
        if (len > 4) {
            if (Arrays.equals(MAGIC, Arrays.copyOfRange(buf, 0, 4))) {
                try {
                    return new String(buf, 4, len - 4, UTF8);
                } catch (UnsupportedEncodingException e) {
                    LOGGER.throwing(TAG, "loadFile", e);
                }
            }
        }
        return new String(buf);
    }

    /**
     * Gets an instance of File.
     *
     * @param name name of the file
     * @return instance of File
     */
    public static File createFilename(String name) {
        return new File(Helper.getSnippetsDir(), name);
    }
}

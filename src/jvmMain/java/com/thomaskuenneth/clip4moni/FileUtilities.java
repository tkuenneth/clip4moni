package com.thomaskuenneth.clip4moni;

/*
 * FileUtilities.java - This file is part of TKNotesAndTasks.
 * Copyright (C) 2013 - 2022  Thomas Kuenneth
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

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * This class contains utility methods that deal with files.
 *
 * @author Thomas Kuenneth
 */
public class FileUtilities {

    private static final String CLASS_NAME = FileUtilities.class.getName();
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    public static String getResourceAsString(Class<? extends JComponent> c, String url) {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = c.getResourceAsStream(url)) {
            if (is != null) {
                int i;
                while ((i = is.read()) != -1) {
                    sb.append((char) i);
                }
            }
        } catch (IOException e) {
            LOGGER.throwing(CLASS_NAME, "getResourceAsString", e);
        }
        return sb.toString();
    }
}
